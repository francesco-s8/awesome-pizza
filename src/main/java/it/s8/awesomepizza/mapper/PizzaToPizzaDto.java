package it.s8.awesomepizza.mapper;

import it.s8.awesomepizza.entity.Pizza;
import org.openapitools.model.PizzaDto;
import org.springframework.cglib.core.internal.Function;
import org.springframework.stereotype.Component;

@Component
public class PizzaToPizzaDto implements Function<Pizza, PizzaDto> {
  @Override
  public PizzaDto apply(Pizza pizza) {
    return PizzaDto.builder()
        .price(pizza.getPrice())
        .description(pizza.getDescription())
        .name(pizza.getName())
        .build();
  }
}
