package it.s8.awesomepizza.service;

import it.s8.awesomepizza.dto.PizzaDtoInfo;
import it.s8.awesomepizza.entity.Pizza;
import it.s8.awesomepizza.exception.AwesomePizzaException;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;
import org.openapitools.model.PizzaBaseDto;
import org.openapitools.model.PizzaDto;

public interface PizzaService {

  List<Pizza> getPizzas(List<String> pizzaNames)
      throws EntityNotFoundException, AwesomePizzaException;

  Pizza getPizzaDetails(Long pizzaId) throws EntityNotFoundException, AwesomePizzaException;

  List<Pizza> getAvailablePizzas() throws EntityNotFoundException, AwesomePizzaException;

  Optional<Pizza> checkIfPizzaExists(PizzaDto pizzaDto);

  Pizza updatePizza(Long pizzaId, PizzaBaseDto pizzaBaseDto) throws EntityNotFoundException;

  Pizza savePizza(PizzaDtoInfo pizzaDtoInfo);

  void disablePizza(Long pizzaId);
}
