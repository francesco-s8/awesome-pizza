package it.s8.awesomepizza.service.impl;

import it.s8.awesomepizza.entity.Pizza;
import it.s8.awesomepizza.exception.AwesomePizzaException;
import it.s8.awesomepizza.repository.PizzaRepository;
import it.s8.awesomepizza.service.PizzaService;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.openapitools.model.PizzaDto;
import org.springframework.stereotype.Service;

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
  public Pizza getPizzaDetails(Long pizzaId) {
    return pizzaRepository
        .findById(pizzaId)
        .orElseThrow(() -> new EntityNotFoundException("Pizza not found id " + pizzaId));
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
        Pizza.builder().name(pizzaDto.getName()).price(pizzaDto.getPrice()).build());
  }

  @Override
  public Pizza updatePizza(Long pizzaId, PizzaDto pizzaDto) {
    var pizza =
        pizzaRepository
            .findById(pizzaId)
            .orElseThrow(() -> new EntityNotFoundException("Pizza not found id " + pizzaId));
    log.info("Pizza fields to be updated {}", pizzaDto);

    var isDescriptionChanged=StringUtils.isNotBlank(pizzaDto.getDescription());

    if (isDescriptionChanged) {
      pizza.setDescription(pizzaDto.getDescription());
    }

    if (pizza.getPrice().compareTo(pizzaDto.getPrice()) == 0
        && !isDescriptionChanged) {
      log.warn(
          "New price {} for pizza {} is the same nothing to update", pizzaDto.getPrice(), pizza);
      return pizza;
    }
    pizza.setPrice(pizzaDto.getPrice());
    log.info("New record updated {}",pizza);
    return pizzaRepository.save(pizza);
  }
}
