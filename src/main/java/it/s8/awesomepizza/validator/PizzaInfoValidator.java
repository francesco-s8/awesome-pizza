package it.s8.awesomepizza.validator;

import it.s8.awesomepizza.exception.AwesomePizzaException;
import java.math.BigDecimal;
import org.springframework.stereotype.Component;

@Component
public class PizzaInfoValidator {

  public boolean isPizzaPriceValid(BigDecimal pizzaPrice) {

    if (BigDecimal.ZERO.compareTo(pizzaPrice) >= 0) {
      throw new AwesomePizzaException("Pizza price can't be zero or less");
    }
    return true;
  }
}
