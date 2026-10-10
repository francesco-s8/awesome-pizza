package it.s8.awesomepizza.mapper;

import it.s8.awesomepizza.entity.Pizza;
import java.math.BigDecimal;
import org.apache.commons.lang3.StringUtils;
import org.openapitools.model.PizzaDto;
import org.springframework.cglib.core.internal.Function;
import org.springframework.stereotype.Component;

@Component
public class PizzaToPizzaDtoMapper implements Function<Pizza, PizzaDto> {
  @Override
  public PizzaDto apply(Pizza pizza) {
    if (pizza.getPrice().compareTo(BigDecimal.ZERO) < 0) {
      throw new IllegalArgumentException("Price cannot be negative");
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
        .build();
  }
}
