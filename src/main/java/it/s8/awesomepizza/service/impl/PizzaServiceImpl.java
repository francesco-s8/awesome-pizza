package it.s8.awesomepizza.service.impl;

import it.s8.awesomepizza.dto.PizzaDtoInfo;
import it.s8.awesomepizza.entity.Pizza;
import it.s8.awesomepizza.exception.AwesomePizzaException;
import it.s8.awesomepizza.repository.PizzaRepository;
import it.s8.awesomepizza.service.PizzaService;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.openapitools.model.PizzaBaseDto;
import org.openapitools.model.PizzaDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
      throw new AwesomePizzaException("Some pizzas not found in the menu");
    }
    if (pizzas.stream().noneMatch(pizza -> Boolean.TRUE.equals(pizza.getIsAvailable()))) {
      throw new AwesomePizzaException("No pizzas available");
    }
    if (pizzas.stream().anyMatch(pizza -> !Boolean.TRUE.equals(pizza.getIsAvailable()))) {
      throw new AwesomePizzaException("Some pizzas are not available");
    }
    if (pizzas.stream()
        .flatMap(pizza -> pizza.getIngredients().stream())
        .anyMatch(ingredient -> !Boolean.TRUE.equals(ingredient.getIsAvailable()))) {
      throw new AwesomePizzaException("Some pizzas have unavailable ingredients");
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
    var pizzas = pizzaRepository.findByIsAvailableTrue();

    if ((pizzas.isEmpty())) {
      log.warn("Pizza list is empty");
      throw new EntityNotFoundException("No pizzas available");
    }

    return pizzas;
  }

  @Override
  public List<Pizza> getAvailablePizzasWithIngredient(String ingredientName) {
    return pizzaRepository.findByIngredients_NameAndIsAvailableTrue(ingredientName);
  }

  @Override
  public List<Pizza> getPizzasWithIngredient(String ingredientName) {
    return pizzaRepository.findByIngredients_Name(ingredientName);
  }

  @Override
  public Optional<Pizza> checkIfPizzaExists(PizzaDto pizzaDto) {
    return pizzaRepository.findByNameIgnoreCase(pizzaDto.getName());
  }

  @Override
  @Transactional
  public Pizza updatePizza(Long pizzaId, PizzaBaseDto pizzaBaseDto) {
    var pizza =
        pizzaRepository
            .findById(pizzaId)
            .orElseThrow(() -> new EntityNotFoundException("Pizza not found id " + pizzaId));

    log.info("Pizza fields to be updated {}", pizzaBaseDto);

    var descriptionChanged = !pizza.getDescription().equals(pizzaBaseDto.getDescription());
    var priceChanged = pizza.getPrice().compareTo(pizzaBaseDto.getPrice()) != 0;

    if (!priceChanged && !descriptionChanged) {
      log.warn("No changes for pizza {}, nothing to update", pizza);
      return pizza;
    }
    if (descriptionChanged) {
      pizza.setDescription(pizzaBaseDto.getDescription());
    }
    if (priceChanged) {
      pizza.setPrice(pizzaBaseDto.getPrice());
    }
    log.info("Pizza info has been updated {} ", pizza);
    return pizzaRepository.save(pizza);
  }

  @Override
  public Pizza savePizza(PizzaDtoInfo pizzaDtoInfo) {
    var pizza =
        Pizza.builder()
            .name(pizzaDtoInfo.pizzaDto().getName())
            .description(pizzaDtoInfo.pizzaDto().getDescription())
            .price(pizzaDtoInfo.pizzaDto().getPrice())
            .ingredients(pizzaDtoInfo.ingredients())
            .isAvailable(Boolean.TRUE)
            .build();
    return pizzaRepository.save(pizza);
  }

  @Override
  public void disablePizza(Long pizzaId) {
    var pizzaToDisabled =
        pizzaRepository
            .findById(pizzaId)
            .orElseThrow(() -> new EntityNotFoundException("No pizza with id " + pizzaId));
    pizzaToDisabled.setIsAvailable(Boolean.FALSE);
    pizzaRepository.save(pizzaToDisabled);
  }

  @Override
  public void enablePizza(Long pizzaId) {
    var pizzaToEnable =
        pizzaRepository
            .findById(pizzaId)
            .orElseThrow(() -> new EntityNotFoundException("No pizza with id " + pizzaId));
    pizzaToEnable.setIsAvailable(Boolean.TRUE);
    pizzaRepository.save(pizzaToEnable);
  }
}
