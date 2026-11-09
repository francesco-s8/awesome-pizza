package it.s8.awesomepizza.utils;

import java.math.BigDecimal;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.openapitools.model.PizzaDto;

@Slf4j
@UtilityClass
public class PizzaInfoValidator {

  public boolean isPizzaPriceValid(BigDecimal pizzaPrice) {

    return BigDecimal.ZERO.compareTo(pizzaPrice) < 0;
  }

  public boolean isValidPizzaInfo(PizzaDto pizzaDto) {

    return StringUtils.isNotBlank(pizzaDto.getName())
        && StringUtils.isNotBlank(pizzaDto.getDescription())
        && pizzaDto.getPrice() != null
        && isPizzaPriceValid(pizzaDto.getPrice());
  }
}
