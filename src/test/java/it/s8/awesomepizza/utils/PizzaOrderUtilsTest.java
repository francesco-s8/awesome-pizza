package it.s8.awesomepizza.utils;

import static org.assertj.core.api.Assertions.assertThat;

import it.s8.awesomepizza.entity.Pizza;
import it.s8.awesomepizza.entity.PizzaOrder;
import it.s8.awesomepizza.helper.TestHelper;
import java.math.BigDecimal;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;

class PizzaOrderUtilsTest {

  @Test
  void givenAnOrder_whenGetTotalAsString_thenReturnFormattedString() {
    var total = 1250;
    String formattedTotal = PizzaOrderUtils.getTotalAsString(total);
    assertThat(formattedTotal.replace("\u00a0", StringUtils.SPACE)).isEqualTo("12,50 €");
  }

  @Test
  void givenAPizzaOrderShouldReturnTheProperTotalPrice() {
    var input = TestHelper.getOrder();
    var actual = PizzaOrderUtils.getTotal(input);
    assertThat(actual).isEqualTo(BigDecimal.valueOf(2900));
  }

  @Test
  void givenAnOrderWithNullPriceShouldReturnZero() {
    var order =
        PizzaOrder.builder()
            .username("alice")
            .pizzaList(List.of(Pizza.builder().name("Marinara").price(null).build()))
            .build();
    var actual = PizzaOrderUtils.getTotal(order);
    assertThat(actual).isZero();
  }
}
