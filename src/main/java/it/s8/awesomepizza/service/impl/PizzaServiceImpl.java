package it.s8.awesomepizza.service.impl;

import it.s8.awesomepizza.entity.Pizza;
import it.s8.awesomepizza.exception.AwesomePizzaException;
import it.s8.awesomepizza.repository.PizzaRepository;
import it.s8.awesomepizza.service.PizzaService;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class PizzaServiceImpl implements PizzaService {

  private final PizzaRepository pizzaRepository;

  public PizzaServiceImpl(PizzaRepository pizzaRepository) {
    this.pizzaRepository = pizzaRepository;
  }

  @Override
  public List<Pizza> getPizzas(List<String> pizzaNames) {
    var pizzas = pizzaRepository.findByNameIn(pizzaNames);
    if (pizzas.size() != pizzaNames.size()) {
      throw new AwesomePizzaException("Some pizzas not found");
    }
    return pizzas;
  }

  @Override
  public List<Pizza> getAvailablePizzas() {
    var pizzas= pizzaRepository.findAll();
    if(pizzas.isEmpty()){
      throw new AwesomePizzaException("No pizzas available");
    }
    return pizzas;

  }
}
