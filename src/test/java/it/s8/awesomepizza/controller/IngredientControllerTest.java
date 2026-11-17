package it.s8.awesomepizza.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import it.s8.awesomepizza.entity.Ingredient;
import it.s8.awesomepizza.exception.InvalidInputException;
import it.s8.awesomepizza.service.IngredientService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.openapitools.model.IngredientDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import tools.jackson.databind.json.JsonMapper;

@WebMvcTest(controllers = IngredientController.class)
class IngredientControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockitoBean IngredientService ingredientService;

  private final JsonMapper jsonMapper = new JsonMapper();

  @Test
  void addIngredientReturnsCreatedIngredient() throws Exception {
    var request = IngredientDto.builder().name("Truffle").build();
    when(ingredientService.addIngredient("Truffle"))
        .thenReturn(Ingredient.builder().id(10L).name("Truffle").isAvailable(true).build());

    var response =
        mockMvc
            .perform(
                MockMvcRequestBuilders.post("/api/awesome-pizza/ingredients")
                    .content(jsonMapper.writeValueAsString(request))
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isCreated())
            .andReturn()
            .getResponse();

    assertThat(response.getHeader("Location"))
        .isEqualTo("/api/awesome-pizza/ingredients/Truffle");
    assertThat(jsonMapper.readValue(response.getContentAsString(), IngredientDto.class).getName())
        .isEqualTo("Truffle");
  }

  @Test
  void addIngredientRejectsBlankName() throws Exception {
    var request = IngredientDto.builder().name(" ").build();

    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/api/awesome-pizza/ingredients")
                .content(jsonMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest());
  }

  @Test
  void addIngredientRejectsNullBody() throws Exception {
    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/api/awesome-pizza/ingredients")
                .content("null")
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest());
  }

  @Test
  void addIngredientRejectsDuplicateName() throws Exception {
    var request = IngredientDto.builder().name("Truffle").build();
    when(ingredientService.addIngredient("Truffle"))
        .thenThrow(new InvalidInputException("Ingredient Truffle already exists"));

    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/api/awesome-pizza/ingredients")
                .content(jsonMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest());
  }

  @Test
  void disableIngredientReturnsNoContent() throws Exception {
    mockMvc
        .perform(
            MockMvcRequestBuilders.delete("/api/awesome-pizza/ingredients/Basil")
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNoContent());
  }

  @Test
  void disableMissingIngredientReturnsNotFound() throws Exception {
    doThrow(new EntityNotFoundException("Ingredient not found"))
        .when(ingredientService)
        .disableIngredient("Unknown");

    mockMvc
        .perform(
            MockMvcRequestBuilders.delete("/api/awesome-pizza/ingredients/Unknown")
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound());
  }
}
