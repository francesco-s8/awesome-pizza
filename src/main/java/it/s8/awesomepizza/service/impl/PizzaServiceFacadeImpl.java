package it.s8.awesomepizza.service.impl;

import it.s8.awesomepizza.dto.PizzaDtoInfo;
import it.s8.awesomepizza.entity.Ingredient;
import it.s8.awesomepizza.entity.Pizza;
import it.s8.awesomepizza.exception.AwesomePizzaException;
import it.s8.awesomepizza.repository.IngredientRepository;
import it.s8.awesomepizza.service.PizzaService;
import it.s8.awesomepizza.service.PizzaServiceFacade;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.openapitools.model.PizzaBaseDto;
import org.openapitools.model.PizzaDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class PizzaServiceFacadeImpl implements PizzaServiceFacade {

  private final IngredientRepository ingredientRepository;
  private final PizzaService pizzaService;

  public PizzaServiceFacadeImpl(
      IngredientRepository ingredientRepository, PizzaService pizzaService) {
    this.ingredientRepository = ingredientRepository;
    this.pizzaService = pizzaService;
  }

  @Override
  @Transactional
  public Pizza addNewPizza(PizzaDto pizzaDto) {
    pizzaService
        .checkIfPizzaExists(pizzaDto)
        .ifPresent(pizza -> {
          log.info("Pizza {} already exists", pizza.getName());
          throw new AwesomePizzaException("Pizza " + pizzaDto.getName() + " already exists");
        });
    var ingredients = getIngredients(pizzaDto.getIngredients());
    var unavailableIngredients =
        ingredients.stream()
            .filter(ingredient -> !Boolean.TRUE.equals(ingredient.getIsAvailable()))
            .toList();
    if (!unavailableIngredients.isEmpty()) {
      var names = unavailableIngredients.stream().map(Ingredient::getName).toList();
      log.info("Cannot add pizza with unavailable ingredients {}", names);
      throw new AwesomePizzaException("Some ingredients are unavailable: " + names);
    }
    return pizzaService.savePizza(new PizzaDtoInfo(pizzaDto, ingredients));
  }

  @Override
  public List<Ingredient> getIngredients(List<String> ingredientNames) {

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

    pizzaService.disablePizza(pizzaId);
  }

  @Override
  public List<Pizza> pizzaMenu() {
    return pizzaService.getAvailablePizzas();
  }

  @Override
  public Pizza pizzaDetails(Long pizzaId) {
    return pizzaService.getPizzaDetails(pizzaId);
  }

  @Override
  @Transactional
  public Pizza updatePizza(Long pizzaId, PizzaBaseDto pizzaBaseDto) {
    return pizzaService.updatePizza(pizzaId, pizzaBaseDto);
  }
}
