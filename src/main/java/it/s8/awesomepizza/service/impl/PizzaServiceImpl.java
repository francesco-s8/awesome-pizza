package it.s8.awesomepizza.service.impl;

import it.s8.awesomepizza.entity.Ingredient;
import it.s8.awesomepizza.entity.Pizza;
import it.s8.awesomepizza.exception.AwesomePizzaException;
import it.s8.awesomepizza.repository.IngredientRepository;
import it.s8.awesomepizza.repository.PizzaRepository;
import it.s8.awesomepizza.service.PizzaService;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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
  public Pizza addPizza(PizzaDto pizzaDto) {

    var pizza = new Pizza();
    pizza.setDescription(pizzaDto.getDescription());
    pizza.setName(pizzaDto.getName());
    pizza.setPrice(pizzaDto.getPrice());
    pizza.setIngredients(
        pizzaDto.getIngredients() == null
            ? List.of()
            : pizzaDto.getIngredients().stream()
                .map(name -> Ingredient.builder().name(name).build())
                .toList());
    return pizzaRepository.save(pizza);
  }

  @Override
  @Transactional
  public Pizza updatePizza(Long pizzaId, PizzaDto pizzaDto) {
    var pizza =
        pizzaRepository
            .findById(pizzaId)
            .orElseThrow(() -> new EntityNotFoundException("Pizza not found id " + pizzaId));

    log.info("Pizza fields to be updated {}", pizzaDto);

    var descriptionChanged = StringUtils.isNotBlank(pizzaDto.getDescription());
    if (descriptionChanged) {
      pizza.setDescription(pizzaDto.getDescription());
    }
    var priceChanged = pizza.getPrice().compareTo(pizzaDto.getPrice()) != 0;
    var ingredientNames = pizzaDto.getIngredients();
    var ingredientsChanged =
        ingredientNames != null
            && !pizza.getIngredients().stream()
                .map(Ingredient::getName)
                .toList()
                .equals(ingredientNames.stream().distinct().toList());
    if (!priceChanged && !descriptionChanged && !ingredientsChanged) {
      log.warn("No changes for pizza {} is the same nothing to update", pizza);
      return pizza;
    }

    if (priceChanged) {
      pizza.setPrice(pizzaDto.getPrice());
    }
    if (ingredientsChanged) {
      pizza.setIngredients(resolveIngredients(ingredientNames));
    }
    log.info("Pizza {} updated ", pizza);
    return pizzaRepository.save(pizza);
  }

  private List<Ingredient> resolveIngredients(List<String> ingredientNames) {
    var uniqueNames = ingredientNames.stream().distinct().toList();
    var ingredientsByName =
        ingredientRepository.findByNameIn(uniqueNames).stream()
            .collect(
                Collectors.toMap(
                    Ingredient::getName, Function.identity(), (first, ignored) -> first));
    var missingIngredients =
        uniqueNames.stream()
            .filter(name -> !ingredientsByName.containsKey(name))
            .map(name -> Ingredient.builder().name(name).build())
            .toList();
    ingredientRepository
        .saveAll(missingIngredients)
        .forEach(ingredient -> ingredientsByName.put(ingredient.getName(), ingredient));

    return uniqueNames.stream().map(ingredientsByName::get).toList();
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
