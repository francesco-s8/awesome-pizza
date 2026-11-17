package it.s8.awesomepizza.controller;

import it.s8.awesomepizza.exception.InvalidInputException;
import it.s8.awesomepizza.service.IngredientService;
import java.net.URI;
import org.openapitools.model.IngredientDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
public class IngredientController implements IngredientApi {

  private final IngredientService ingredientService;

  public IngredientController(IngredientService ingredientService) {
    this.ingredientService = ingredientService;
  }

  @Override
  public ResponseEntity<IngredientDto> _addIngredient(IngredientDto ingredientDto) {
    if (ingredientDto == null
        || ingredientDto.getName() == null
        || ingredientDto.getName().trim().isEmpty()) {
      throw new InvalidInputException("Ingredient name must not be empty");
    }

    var ingredient = ingredientService.addIngredient(ingredientDto.getName().trim());
    URI location =
        UriComponentsBuilder.fromPath(PATH_ADD_INGREDIENT+"/{ingredientName}")
            .buildAndExpand(ingredient.getName())
            .encode()
            .toUri();
    return ResponseEntity.created(location)
        .body(IngredientDto.builder().name(ingredient.getName()).build());
  }

  @Override
  public ResponseEntity<Void> _disableIngredient(String ingredientName) {
    ingredientService.disableIngredient(ingredientName);
    return ResponseEntity.noContent().build();
  }
}
