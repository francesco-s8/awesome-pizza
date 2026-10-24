package it.s8.awesomepizza.service;

import it.s8.awesomepizza.entity.Pizza;
import it.s8.awesomepizza.exception.AwesomePizzaException;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import org.openapitools.model.PizzaDto;

public interface PizzaService {

  List<Pizza> getPizzas(List<String> pizzaNames)
      throws EntityNotFoundException, AwesomePizzaException;

  Pizza getPizzaDetails(Long pizzaId) throws EntityNotFoundException, AwesomePizzaException;

  List<Pizza> getAvailablePizzas() throws EntityNotFoundException, AwesomePizzaException;

  Pizza addPizza(PizzaDto pizzaDto) throws EntityNotFoundException, AwesomePizzaException;

  Pizza updatePizza(Long pizzaId, PizzaDto pizzaDto) throws EntityNotFoundException;

  void disablePizza(Long pizzaId);
}
