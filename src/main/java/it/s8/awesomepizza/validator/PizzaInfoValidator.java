package it.s8.awesomepizza.validator;

import it.s8.awesomepizza.exception.AwesomePizzaException;
import it.s8.awesomepizza.exception.InvalidInputException;
import java.math.BigDecimal;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.openapitools.model.PizzaDto;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PizzaInfoValidator {

  public boolean isPizzaPriceValid(BigDecimal pizzaPrice) {

    if (BigDecimal.ZERO.compareTo(pizzaPrice) >= 0) {
      throw new AwesomePizzaException("Pizza price can't be zero or less");
    }
    return true;
  }

  public boolean isValidPizzaInfo(PizzaDto pizzaDto) {
    if (StringUtils.isBlank(pizzaDto.getName())
        || StringUtils.isBlank(pizzaDto.getDescription()) || pizzaDto.getPrice()==null
        || !isPizzaPriceValid(pizzaDto.getPrice())) {
      throw new InvalidInputException("Pizza info are invalid "+pizzaDto);
    }
    return true;
  }
}
