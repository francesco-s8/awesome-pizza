package it.s8.awesomepizza.service.impl;

import it.s8.awesomepizza.service.IngredientFacade;
import it.s8.awesomepizza.service.IngredientService;
import it.s8.awesomepizza.service.PizzaService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class IngredientFacadeImpl implements IngredientFacade {
  private final PizzaService pizzaService;
  private final IngredientService ingredientService;

  public IngredientFacadeImpl(PizzaService pizzaService, IngredientService ingredientService) {
    this.pizzaService = pizzaService;
    this.ingredientService = ingredientService;
  }

  @Override
  @Transactional
  public void disableIngredient(String ingredientName) {
    ingredientService.disableIngredient(ingredientName);
    pizzaService.getAvailablePizzasWithIngredient(ingredientName)
        .forEach(pizza -> pizzaService.disablePizza(pizza.getId()));
  }
}
