package it.s8.awesomepizza.utils;

import java.math.BigDecimal;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.openapitools.model.PizzaDto;

@Slf4j
@UtilityClass
public class PizzaInfoValidator {

  public boolean isPizzaPriceValid(BigDecimal pizzaPrice) {

    if (pizzaPrice == null) {
      return false;
    }
    return BigDecimal.ZERO.compareTo(pizzaPrice) < 0;
  }

  public boolean isValidPizzaInfo(PizzaDto pizzaDto) {
    if (pizzaDto == null
        || (pizzaDto.getName() != null && pizzaDto.getName().trim().isEmpty())
        || (pizzaDto.getDescription() != null && pizzaDto.getDescription().trim().isEmpty())
        || !isPizzaPriceValid(pizzaDto.getPrice())
        || (pizzaDto.getIngredients() != null && pizzaDto.getIngredients().isEmpty())) {
      log.error("Pizza info is invalid");
      return false;
    }
    return true;
  }
}
