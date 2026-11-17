package it.s8.awesomepizza.service;

import it.s8.awesomepizza.entity.Ingredient;
import java.util.List;

public interface IngredientService {

  List<Ingredient> retrieveAllAvailableIngredients();

  List<Ingredient> retrieveAvailableIngredients(List<String> names);

  Ingredient addIngredient(String name);

  void disableIngredient(String name);
}
