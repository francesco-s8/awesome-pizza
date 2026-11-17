package it.s8.awesomepizza.service;

import it.s8.awesomepizza.entity.Ingredient;
import it.s8.awesomepizza.entity.Pizza;
import java.util.List;
import org.openapitools.model.PizzaBaseDto;
import org.openapitools.model.PizzaDto;

public interface PizzaServiceFacade {


  Pizza addNewPizza(PizzaDto pizzaDto);

  List<Ingredient> getIngredients(List<String> ingredientNames);

  void disablePizza(Long pizzaId);

  List<Pizza> pizzaMenu();

  Pizza pizzaDetails(Long pizzaId);

  Pizza updatePizza(Long pizzaId, PizzaBaseDto pizzaBaseDto);
}
