package it.s8.awesomepizza.service.impl;

import it.s8.awesomepizza.entity.Ingredient;
import it.s8.awesomepizza.entity.Pizza;
import it.s8.awesomepizza.exception.AwesomePizzaException;
import it.s8.awesomepizza.repository.IngredientRepository;
import it.s8.awesomepizza.repository.PizzaRepository;
import it.s8.awesomepizza.service.PizzaService;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.openapitools.model.PizzaBaseDto;
import org.openapitools.model.PizzaDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class PizzaServiceImpl implements PizzaService {

  private final PizzaRepository pizzaRepository;
  private final IngredientRepository ingredientRepository;

  public PizzaServiceImpl(
      PizzaRepository pizzaRepository, IngredientRepository ingredientRepository) {
    this.pizzaRepository = pizzaRepository;
    this.ingredientRepository = ingredientRepository;
  }

  @Override
  public List<Pizza> getPizzas(List<String> pizzaNames) {
    var pizzas = pizzaRepository.findByNameIn(pizzaNames);

    if (pizzas.size() != pizzaNames.size()) {
      throw new AwesomePizzaException("Some pizzas not found in the menu");
    }
    if (pizzas.stream().filter(Pizza::getIsAvailable).toList().isEmpty()) {
      throw new AwesomePizzaException("No pizzas available");
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
  @Transactional
  public Pizza addPizza(PizzaDto pizzaDto) {

    var existPizza =
        pizzaRepository.findByNameIgnoreCase(pizzaDto.getName());
    if (existPizza.isPresent()) {
      log.info("Pizza with name {} already exists", pizzaDto.getName());
      throw new AwesomePizzaException("Pizza with name " + pizzaDto.getName() + " already exists");
    }
    var pizza =
        Pizza.builder()
            .name(pizzaDto.getName())
            .description(pizzaDto.getDescription())
            .price(pizzaDto.getPrice())
            .ingredients(resolveIngredients(pizzaDto.getIngredients()))
            .isAvailable(Boolean.TRUE)
            .build();
    return pizzaRepository.save(pizza);
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

  private List<Ingredient> resolveIngredients(List<String> ingredientNames) {
    var uniqueNames = ingredientNames.stream().distinct().toList();
    var foundIngredients = ingredientRepository.findByNameIn(uniqueNames);
    if (foundIngredients.isEmpty() || foundIngredients.size() != uniqueNames.size()) {
      log.info(
          " Expected ingredients {}  mismatched with one in the database {}",
          uniqueNames,
          foundIngredients);
      throw new AwesomePizzaException("Some ingredients " + uniqueNames + "not found");
    }
    return foundIngredients;
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
}
