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

    if (pizzaDto.getName() != null && pizzaDto.getName().trim().isEmpty()) {
      log.error("Pizza name is empty");
      return false;
    }
    if (pizzaDto.getDescription() != null && pizzaDto.getDescription().trim().isEmpty()) {
      log.error("Pizza description is empty");
      return false;
    }
    if (!isPizzaPriceValid(pizzaDto.getPrice())) {
      log.error("Pizza price is invalid");
      return false;
    }
    if (pizzaDto.getIngredients() != null && pizzaDto.getIngredients().isEmpty()) {
      log.error("Pizza ingredients are empty");
      return false;
    }
    return true;
  }
}
