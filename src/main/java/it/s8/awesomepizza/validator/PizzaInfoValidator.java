package it.s8.awesomepizza.validator;

import it.s8.awesomepizza.exception.AwesomePizzaException;
import org.apache.commons.lang3.StringUtils;
import org.openapitools.model.PizzaDto;
import org.springframework.stereotype.Component;

@Component
public class PizzaInfoValidator {

  public boolean isValid(PizzaDto pizzaDto) {

    if (StringUtils.isBlank(pizzaDto.getName())
        && StringUtils.isBlank(pizzaDto.getDescription())
        && pizzaDto.getPrice() == null) {
      throw new AwesomePizzaException("Pizza info not provided");
    }
      if(pizzaDto.getPrice() !=null && pizzaDto.getPrice().doubleValue() < 0) {
      throw new AwesomePizzaException("Pizza price cannot be negative");
    }
    return true;
  }
}
