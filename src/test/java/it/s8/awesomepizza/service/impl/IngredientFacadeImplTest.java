package it.s8.awesomepizza.service.impl;

import static org.mockito.Mockito.*;

import it.s8.awesomepizza.entity.Ingredient;
import it.s8.awesomepizza.entity.Pizza;
import it.s8.awesomepizza.service.IngredientService;
import it.s8.awesomepizza.service.PizzaService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class IngredientFacadeImplTest {

  @Mock private PizzaService pizzaService;
  @Mock private IngredientService ingredientService;

  @InjectMocks private IngredientFacadeImpl ingredientFacade;

  @Test
  void disablingIngredientAlsoDisablesEveryAvailablePizzaThatContainsIt() {
    var targetIngredient = Ingredient.builder().name("Basil").build();
    var unrelatedIngredient = Ingredient.builder().name("Tomato").build();
    var pizzaWithIngredient =
        Pizza.builder().id(1L).ingredients(List.of(targetIngredient, unrelatedIngredient)).build();
    var anotherPizzaWithIngredient =
        Pizza.builder().id(2L).ingredients(List.of(targetIngredient)).build();
    var pizzaWithoutIngredient =
        Pizza.builder().id(3L).ingredients(List.of(unrelatedIngredient)).build();
    when(pizzaService.getAvailablePizzasWithIngredient("Basil"))
        .thenReturn(List.of(pizzaWithIngredient, anotherPizzaWithIngredient));

    ingredientFacade.disableIngredient("Basil");

    verify(ingredientService).disableIngredient("Basil");
    verify(pizzaService).getAvailablePizzasWithIngredient("Basil");
    verify(pizzaService).disablePizza(1L);
    verify(pizzaService).disablePizza(2L);
    verify(pizzaService, never()).disablePizza(3L);
  }

  @Test
  void enablingIngredientOnlyEnablesRelatedPizzasWithAllIngredientsAvailable() {
    var ingredient = Ingredient.builder().name("Basil").isAvailable(true).build();
    var availableIngredient = Ingredient.builder().name("Tomato").isAvailable(true).build();
    var unavailableIngredient = Ingredient.builder().name("Cheese").isAvailable(false).build();
    var eligiblePizza =
        Pizza.builder()
            .id(1L)
            .isAvailable(false)
            .ingredients(List.of(ingredient, availableIngredient))
            .build();
    var pizzaWithUnavailableIngredient =
        Pizza.builder()
            .id(2L)
            .isAvailable(false)
            .ingredients(List.of(ingredient, unavailableIngredient))
            .build();
    var alreadyAvailablePizza =
        Pizza.builder()
            .id(3L)
            .isAvailable(true)
            .ingredients(List.of(ingredient))
            .build();
    when(pizzaService.getPizzasWithIngredient("Basil"))
        .thenReturn(List.of(eligiblePizza, pizzaWithUnavailableIngredient, alreadyAvailablePizza));

    ingredientFacade.enableIngredient("Basil");

    var inOrder = inOrder(ingredientService, pizzaService);
    inOrder.verify(ingredientService).enableIngredient("Basil");
    inOrder.verify(pizzaService).getPizzasWithIngredient("Basil");
    verify(pizzaService).enablePizza(1L);
    verify(pizzaService, never()).enablePizza(2L);
    verify(pizzaService, never()).enablePizza(3L);
  }
}
