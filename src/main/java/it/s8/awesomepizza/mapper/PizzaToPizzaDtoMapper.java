package it.s8.awesomepizza.mapper;

import it.s8.awesomepizza.entity.Ingredient;
import it.s8.awesomepizza.entity.Pizza;
import org.apache.commons.lang3.StringUtils;
import org.openapitools.model.PizzaDto;
import org.springframework.cglib.core.internal.Function;
import org.springframework.stereotype.Component;

@Component
public class PizzaToPizzaDtoMapper implements Function<Pizza, PizzaDto> {
  @Override
  public PizzaDto apply(Pizza pizza) {
    if (pizza.getPrice().signum() <= 0) {
      throw new IllegalArgumentException("Price cannot be negative or zero");
    }
    if (StringUtils.isBlank(pizza.getName())) {
      throw new IllegalArgumentException("Name cannot be blank");
    }
    if (StringUtils.isBlank(pizza.getDescription())) {
      throw new IllegalArgumentException("Description cannot be blank");
    }
    return PizzaDto.builder()
        .price(pizza.getPrice())
        .description(pizza.getDescription())
        .name(pizza.getName())
        .ingredients(pizza.getIngredients().stream().map(Ingredient::getName).toList())
        .build();
  }
}
