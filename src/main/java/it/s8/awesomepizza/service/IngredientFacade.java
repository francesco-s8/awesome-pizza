package it.s8.awesomepizza.service;

public interface IngredientFacade {

  void disableIngredient(String ingredientName);

  void enableIngredient(String ingredientName);
}
