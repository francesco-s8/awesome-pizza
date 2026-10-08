package it.s8.awesomepizza.service;

import it.s8.awesomepizza.entity.Pizza;
import java.util.List;
import org.openapitools.model.PizzaDto;

public interface PizzaService {

    List<Pizza> getPizzas(List<String> pizzaNames);

  Pizza getPizzaDetails(Long pizzaId);

    List<Pizza> getAvailablePizzas();
    Pizza addPizza(PizzaDto pizzaDto);
    Pizza updatePizza(Long pizzaId, PizzaDto pizzaDto);
}
