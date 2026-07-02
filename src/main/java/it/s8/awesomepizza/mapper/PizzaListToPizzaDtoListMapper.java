package it.s8.awesomepizza.mapper;

import it.s8.awesomepizza.entity.Pizza;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import org.openapitools.model.PizzaDto;
import org.openapitools.model.PizzaMenu;
import org.springframework.stereotype.Component;

@Component
public class PizzaListToPizzaDtoListMapper implements Function<List<Pizza>, List<PizzaDto>> {

  List<PizzaMenu> pizzaDtoList = new ArrayList<>();


  @Override
  public List<PizzaDto> apply(List<Pizza> pizzas) {
    return pizzas.stream()
        .map(
            pizza ->
                org.openapitools.model.PizzaDto.builder()
                    .name(pizza.getName())
                    .description(pizza.getDescription())
                    .price(pizza.getPrice())
                    .build())
        .toList();
  }
}
