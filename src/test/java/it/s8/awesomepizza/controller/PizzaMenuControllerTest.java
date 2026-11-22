package it.s8.awesomepizza.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import it.s8.awesomepizza.entity.Ingredient;
import it.s8.awesomepizza.entity.Pizza;
import it.s8.awesomepizza.exception.AwesomePizzaException;
import it.s8.awesomepizza.helper.TestHelper;
import it.s8.awesomepizza.mapper.PizzaListToPizzaDtoListMapper;
import it.s8.awesomepizza.mapper.PizzaToPizzaDtoMapper;
import it.s8.awesomepizza.service.PizzaServiceFacade;
import jakarta.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openapitools.model.PizzaBaseDto;
import org.openapitools.model.PizzaDto;
import org.openapitools.model.PizzaMenu;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import tools.jackson.databind.json.JsonMapper;

@Slf4j
@WebMvcTest(controllers = PizzaMenuController.class)
class PizzaMenuControllerTest {

  @Autowired private MockMvc mockMvc;

  private final JsonMapper jsonMapper = new JsonMapper();

  private List<Pizza> pizzasList;
  private List<PizzaDto> pizzasDtoList;

  @MockitoBean PizzaServiceFacade pizzaServiceFacade;
  @MockitoBean PizzaListToPizzaDtoListMapper pizzaListToPizzaDtoListMapper;
  @MockitoBean PizzaToPizzaDtoMapper pizzaToPizzaDtoMapper;

  @BeforeEach
  void setupStubs() {
    pizzasList = TestHelper.getPizzaListStub();
    pizzasDtoList = TestHelper.getPizzaDtoListStub();
  }

  @Test
  void shouldReturnMenuWithAvailablePizzas() throws Exception {
    when(pizzaServiceFacade.pizzaMenu()).thenReturn(pizzasList);
    when(pizzaListToPizzaDtoListMapper.apply(pizzasList)).thenReturn(pizzasDtoList);

    var actual =
        mockMvc
            .perform(
                MockMvcRequestBuilders.get("/api/awesome-pizza/menu")
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn();

    assertThat(actual.getResponse().getContentAsString()).isNotEmpty();
  }

  @Test
  void shouldReturnMenuWithEmptyListWhenNoPizzasAvailable() throws Exception {

    when(pizzaServiceFacade.pizzaMenu()).thenReturn(List.of());
    when(pizzaListToPizzaDtoListMapper.apply(List.of())).thenReturn(List.of());

    var actual =
        mockMvc
            .perform(
                MockMvcRequestBuilders.get("/api/awesome-pizza/menu")
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn();

    var response = jsonMapper.readValue(actual.getResponse().getContentAsString(), PizzaMenu.class);
    assertThat(response.getMenu()).isEmpty();
  }

  @Test
  void _pizzaDetails() throws Exception {

    when(pizzaServiceFacade.pizzaDetails(anyLong())).thenReturn(pizzasList.getFirst());
    when(pizzaToPizzaDtoMapper.apply(pizzasList.getFirst())).thenReturn(pizzasDtoList.getFirst());
    var actual =
        mockMvc
            .perform(
                MockMvcRequestBuilders.get("/api/awesome-pizza/menu/1")
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn();
    log.info("response {}", actual);
    var response = jsonMapper.readValue(actual.getResponse().getContentAsString(), PizzaDto.class);
    assertThat(response).isNotNull().isEqualTo(pizzasDtoList.getFirst());
  }

  @Test
  void givenANonExistingOrderIdShouldBeReturnANotFoundStatus() throws Exception {

    when(pizzaServiceFacade.pizzaDetails(99L))
        .thenThrow(new EntityNotFoundException("Pizza not found id " + 99));

    mockMvc
        .perform(
            MockMvcRequestBuilders.get("/api/awesome-pizza/menu/99")
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound())
        .andReturn();
  }

  @Test
  void givenValidInputWithAnUnknownPizzaIsAnInternalErrorShouldBeReturned() throws Exception {

    when(pizzaServiceFacade.pizzaMenu())
        .thenThrow(new AwesomePizzaException("Some pizzas not found"));

    mockMvc
        .perform(
            MockMvcRequestBuilders.get("/api/awesome-pizza/menu")
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isInternalServerError())
        .andReturn();
  }

  @Test
  void givenValidInputWithADatabaseIssuesAnInternalErrorShouldBeReturned() throws Exception {

    doThrow(EmptyResultDataAccessException.class).when(pizzaServiceFacade).pizzaMenu();
    mockMvc
        .perform(
            MockMvcRequestBuilders.get("/api/awesome-pizza/menu")
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isInternalServerError())
        .andReturn();
  }

  @Test
  void _addPizzaToMenu() throws Exception {
    var pizzaDtoStub =
        PizzaDto.builder()
            .name("Margherita")
            .description("Tomato and mozzarella")
            .price(new BigDecimal("8.00"))
            .ingredients(List.of("Tomato", "Mozzarella"))
            .build();
    var pizzaStub =
        Pizza.builder()
            .id(1L)
            .version(1)
            .description(pizzaDtoStub.getDescription())
            .price(pizzaDtoStub.getPrice())
            .name(pizzaDtoStub.getName())
            .ingredients(
                List.of(
                    Ingredient.builder().name("Tomato").build(),
                    Ingredient.builder().name("Mozzarella").build()))
            .build();
    when(pizzaServiceFacade.addNewPizza(any())).thenReturn(pizzaStub);
    when(pizzaToPizzaDtoMapper.apply(pizzaStub)).thenReturn(pizzaDtoStub);
    var response =
        mockMvc
            .perform(
                MockMvcRequestBuilders.post("/api/awesome-pizza/menu")
                    .content(jsonMapper.writeValueAsString(pizzaDtoStub))
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isCreated())
            .andReturn();

    var createdPizza =
        jsonMapper.readValue(response.getResponse().getContentAsString(), PizzaDto.class);
    assertThat(createdPizza.getIngredients()).containsExactly("Tomato", "Mozzarella");
  }

  @Test
  void givenAnInvalidInputShouldBeRaisedAnInvalidInputException() throws Exception {
    var newPizza = Pizza.builder().build();
    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/api/awesome-pizza/menu")
                .content(jsonMapper.writeValueAsString(newPizza))
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest())
        .andReturn();
  }

  @Test
  void _getMenu() throws Exception {
    when(pizzaServiceFacade.pizzaMenu())
        .thenThrow(new EntityNotFoundException("No pizzas available"));

    mockMvc
        .perform(
            MockMvcRequestBuilders.get("/api/awesome-pizza/menu")
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound())
        .andReturn();
  }

  @Test
  void _disablePizzaAvailability() throws Exception {
    mockMvc
        .perform(
            MockMvcRequestBuilders.delete("/api/awesome-pizza/menu/1")
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNoContent())
        .andReturn();
  }

  @Test
  void disablePizzaWhenPizzaIsNotFound() throws Exception {
    doThrow(new EntityNotFoundException("No pizza with id " + 1))
        .when(pizzaServiceFacade)
        .disablePizza(anyLong());
    mockMvc
        .perform(
            MockMvcRequestBuilders.delete("/api/awesome-pizza/menu/1")
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound())
        .andReturn();
  }

  @Test
  void _updatePizzaInMenuValidInput() throws Exception {

    var stub = TestHelper.getPizzaListStub().getFirst();
    var input = PizzaBaseDto.builder().price(BigDecimal.ONE).build();

    when(pizzaServiceFacade.updatePizza(1L, input)).thenReturn(stub);
    when(pizzaToPizzaDtoMapper.apply(stub))
        .thenReturn(
            PizzaDto.builder()
                .name(stub.getName())
                .description(stub.getDescription())
                .price(input.getPrice())
                .build());
    var actual =
        mockMvc
            .perform(
                MockMvcRequestBuilders.patch("/api/awesome-pizza/menu/1")
                    .content(jsonMapper.writeValueAsString(input))
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn();
    assertThat(actual.getResponse().getContentAsString()).isNotEmpty();
    var updatedPizza =
        jsonMapper.readValue(actual.getResponse().getContentAsString(), PizzaDto.class);
    assertThat(updatedPizza.getPrice()).isEqualTo(input.getPrice());
    assertThat(updatedPizza.getDescription()).isEqualTo(stub.getDescription());
  }

  @Test
  void _updatePizzaInMenuInvalidInput() throws Exception {

    var responseObject =
        "{\"detail\":\"Check the request body, some fields are not valid\",\"status\":400,\"title\":\"Bad Request\"}";
    var input =
        PizzaDto.builder()
            .name("Margherita")
            .price(new BigDecimal(-1))
            .description("new desc")
            .build();
    var actual =
        mockMvc
            .perform(
                MockMvcRequestBuilders.patch("/api/awesome-pizza/menu/1")
                    .content(jsonMapper.writeValueAsString(input))
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andReturn();
    assertThat(actual.getResponse().getContentAsString()).isNotEmpty().isEqualTo(responseObject);
  }
}
