package it.s8.awesomepizza.utils;

import it.s8.awesomepizza.entity.Pizza;
import it.s8.awesomepizza.entity.PizzaOrder;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import lombok.experimental.UtilityClass;
import org.jspecify.annotations.NonNull;

@UtilityClass
public class PizzaOrderUtils {

  @NonNull
  public String getTotalAsString(long total) {
    return NumberFormat.getCurrencyInstance().format(total / 100.00);
  }

  public BigDecimal getTotal(PizzaOrder order) {
    return order.getPizzaList().stream()
        .map(Pizza::getPrice)
        .filter(java.util.Objects::nonNull)
        .reduce(BigDecimal.ZERO, BigDecimal::add)
        .setScale(2, RoundingMode.HALF_UP)
        .movePointRight(2);
  }
}
