package it.s8.awesomepizza.service;

import it.s8.awesomepizza.entity.Pizza;
import it.s8.awesomepizza.exception.AwesomePizzaException;
import java.util.List;
import org.openapitools.model.PizzaDto;
import org.springframework.dao.DataAccessException;

public interface PizzaService {

  List<Pizza> getPizzas(List<String> pizzaNames) throws DataAccessException, AwesomePizzaException;

  Pizza getPizzaDetails(Long pizzaId) throws DataAccessException, AwesomePizzaException;

  List<Pizza> getAvailablePizzas() throws DataAccessException, AwesomePizzaException;

  Pizza addPizza(PizzaDto pizzaDto) throws DataAccessException, AwesomePizzaException;

  Pizza updatePizza(Long pizzaId, PizzaDto pizzaDto)
      throws DataAccessException, AwesomePizzaException;
}
