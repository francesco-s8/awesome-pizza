package it.s8.awesomepizza.utils;

import it.s8.awesomepizza.entity.Pizza;
import it.s8.awesomepizza.entity.PizzaOrder;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.Locale;
import lombok.experimental.UtilityClass;
import org.jspecify.annotations.NonNull;
import org.openapitools.model.PizzaOrderStatus;

@UtilityClass
public class PizzaOrderUtils {

  public String getTotalAsString(BigDecimal total) {
    if (total.compareTo(BigDecimal.ZERO) < 0) {
      throw new IllegalArgumentException("Total amount is negative");
    }
    return NumberFormat.getCurrencyInstance(Locale.ITALY).format(total);
  }

  public BigDecimal getTotal(PizzaOrder order) {
    return order.getPizzaList().stream()
        .map(Pizza::getPrice)
        .filter(java.util.Objects::nonNull)
        .reduce(BigDecimal.ZERO, BigDecimal::add)
        .setScale(2, RoundingMode.HALF_UP);
  }

  public boolean orderCanBePaid(@NonNull PizzaOrder order) {

    return order.getOrderStatus().equals(PizzaOrderStatus.OrderStatusEnum.TO_BE_PAID.getValue());
  }
}
