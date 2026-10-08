package it.s8.awesomepizza.service;

import it.s8.awesomepizza.entity.Pizza;
import org.openapitools.model.PizzaDto;

import java.util.List;

public interface PizzaService {

    List<Pizza> getPizzas(List<String> pizzaNames);
    List<Pizza> getAvailablePizzas();
    Pizza addPizza(PizzaDto pizzaDto);
    Pizza updatePizza(Long pizzaId, PizzaDto pizzaDto);
}
