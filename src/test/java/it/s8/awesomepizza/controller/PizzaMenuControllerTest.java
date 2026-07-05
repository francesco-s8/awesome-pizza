package it.s8.awesomepizza.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.json.JsonMapper;
import it.s8.awesomepizza.entity.Pizza;
import it.s8.awesomepizza.mapper.PizzaListToPizzaDtoListMapper;
import it.s8.awesomepizza.service.PizzaService;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openapitools.model.PizzaDto;
import org.openapitools.model.PizzaMenu;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@WebMvcTest(controllers = PizzaMenuController.class)
class PizzaMenuControllerTest {

  @Autowired private MockMvc mockMvc;

  private final JsonMapper jsonMapper = new JsonMapper();

  private List<Pizza> pizzasList;

  private List<PizzaDto> pizzasDtoList;

  @MockitoBean PizzaService pizzaService;

  @MockitoBean PizzaListToPizzaDtoListMapper pizzaListToPizzaDtoListMapper;

  @BeforeEach
  void setupStubs() {
    var margherita =
        Pizza.builder()
            .id(1L)
            .name("Margherita")
            .price(new BigDecimal("8.00"))
            .description("La classica con pomodoro, mozzarella e basilico")
            .build();
    var marinara =
        Pizza.builder()
            .id(2L)
            .name("Marinara")
            .price(new BigDecimal("7.00"))
            .description("Pomodoro, aglio e origano")
            .build();

    var margheritaDto =
        PizzaDto.builder()
            .name("Margherita")
            .description("La classica con pomodoro, mozzarella e basilico")
            .price(new BigDecimal("8.00"))
            .build();
    var marinaraDto =
        PizzaDto.builder()
            .name("Marinara")
            .description("Pomodoro, aglio e origano")
            .price(new BigDecimal("7.00"))
            .build();
    pizzasList = List.of(margherita, marinara);
    pizzasDtoList = List.of(margheritaDto, marinaraDto);
  }

  @Test
  void shouldReturnMenuWithAvailablePizzas() throws Exception {

    var margherita =
        Pizza.builder()
            .id(1L)
            .name("Margherita")
            .price(new BigDecimal("8.00"))
            .description("La classica con pomodoro, mozzarella e basilico")
            .build();
    var marinara =
        Pizza.builder()
            .id(2L)
            .name("Marinara")
            .price(new BigDecimal("7.00"))
            .description("Pomodoro, aglio e origano")
            .build();

    var margheritaDto =
        PizzaDto.builder()
            .name("Margherita")
            .description("La classica con pomodoro, mozzarella e basilico")
            .price(new BigDecimal("8.00"))
            .build();
    var marinaraDto =
        PizzaDto.builder()
            .name("Marinara")
            .description("Pomodoro, aglio e origano")
            .price(new BigDecimal("7.00"))
            .build();
    pizzasList = List.of(margherita, marinara);
    pizzasDtoList = List.of(margheritaDto, marinaraDto);

    when(pizzaService.getAvailablePizzas()).thenReturn(pizzasList);
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

    when(pizzaService.getAvailablePizzas()).thenReturn(List.of());
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
}
