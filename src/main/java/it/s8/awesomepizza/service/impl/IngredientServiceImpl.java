package it.s8.awesomepizza.service.impl;

import it.s8.awesomepizza.entity.Ingredient;
import it.s8.awesomepizza.exception.InvalidInputException;
import it.s8.awesomepizza.repository.IngredientRepository;
import it.s8.awesomepizza.service.IngredientService;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class IngredientServiceImpl implements IngredientService {

  final IngredientRepository ingredientRepository;

  public IngredientServiceImpl(IngredientRepository ingredientRepository) {
    this.ingredientRepository = ingredientRepository;
  }

  @Override
  public List<Ingredient> retrieveAllAvailableIngredients() {
    return ingredientRepository.findByIsAvailableTrue();
  }

  @Override
  public List<Ingredient> retrieveAvailableIngredients(List<String> names) {
    return ingredientRepository.findByNameInAndIsAvailableTrue(names);
  }

  @Override
  public Ingredient addIngredient(String name) {
    if (ingredientRepository.findByName(name).isPresent()) {
      throw new InvalidInputException("Ingredient " + name + " already exists");
    }

    var ingredient = Ingredient.builder().name(name).isAvailable(true).build();
    return ingredientRepository.save(ingredient);
  }

  @Override
  public void disableIngredient(String name) {

    var ingredient =
        ingredientRepository
            .findByNameAndIsAvailableTrue(name)
            .orElseThrow(
                () -> new EntityNotFoundException("Ingredient: " + name+ "is already disabled"));

    ingredient.setIsAvailable(false);
    ingredientRepository.save(ingredient);
  }
}
