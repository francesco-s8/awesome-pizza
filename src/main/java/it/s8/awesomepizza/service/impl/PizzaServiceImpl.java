package it.s8.awesomepizza.service.impl;

import it.s8.awesomepizza.entity.Pizza;
import it.s8.awesomepizza.exception.AwesomePizzaException;
import it.s8.awesomepizza.repository.PizzaRepository;
import it.s8.awesomepizza.service.PizzaService;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.openapitools.model.PizzaDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
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
    var pizzas = pizzaRepository.findAll();
    if ((pizzas.isEmpty())) {
      log.warn("Pizza list is empty");
    }
    return pizzas;
  }

  @Override
  public Pizza addPizza(PizzaDto pizzaDto) {
    return pizzaRepository.save(
        Pizza.builder()
            .name(pizzaDto.getName())
            .description(pizzaDto.getDescription())
            .price(pizzaDto.getPrice())
            .build());
  }

  @Override
  public Pizza updatePizza(Long pizzaId, PizzaDto pizzaDto) {
    var pizza =
        pizzaRepository
            .findById(pizzaId)
            .orElseThrow(() -> new EntityNotFoundException("Pizza not found id " + pizzaId));
    if (pizzaDto.getDescription() != null) {
      pizza.setDescription(pizzaDto.getDescription());
    }
    if (pizzaDto.getName() != null) {
      pizza.setName(pizzaDto.getName());
    }
    if (pizzaDto.getPrice() != null) {
      pizza.setPrice(pizzaDto.getPrice());
    }
    return pizzaRepository.save(pizza);
  }
}
