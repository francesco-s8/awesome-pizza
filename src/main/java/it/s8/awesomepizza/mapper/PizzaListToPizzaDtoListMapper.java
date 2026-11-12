package it.s8.awesomepizza.mapper;

import it.s8.awesomepizza.entity.Ingredient;
import it.s8.awesomepizza.entity.Pizza;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import org.openapitools.model.PizzaDto;
import org.springframework.stereotype.Component;

@Component
public class PizzaListToPizzaDtoListMapper implements Function<List<Pizza>, List<PizzaDto>> {

  @Override
  public List<PizzaDto> apply(List<Pizza> pizzas) {
    Objects.requireNonNull(pizzas);
    return pizzas.stream()
        .map(pizza ->
            PizzaDto.builder()
                .name(pizza.getName())
                .description(pizza.getDescription())
                .price(pizza.getPrice())
                .ingredients(pizza.getIngredients().stream().map(Ingredient::getName).toList())
                .build())
        .toList();
  }
}
