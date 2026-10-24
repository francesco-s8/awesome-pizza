package it.s8.awesomepizza.utils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import it.s8.awesomepizza.entity.Pizza;
import it.s8.awesomepizza.entity.PizzaOrder;
import it.s8.awesomepizza.helper.TestHelper;
import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.openapitools.model.PizzaOrderStatus;

@Slf4j
class PizzaOrderUtilsTest {

  @Test
  void givenAnOrder_whenGetTotalAsString_thenReturnFormattedString() {

    Locale.setDefault(Locale.ITALY);
    String formattedTotal = PizzaOrderUtils.getTotalAsString(new BigDecimal("12.50"));
    assertThat(formattedTotal.replace("\u00a0", StringUtils.SPACE)).isEqualTo("12,50 €");
  }

  @Test
  void givenAnOrder_whenTotalIsNegative_shouldBeThrownAnIllegalException() {
    var total = BigDecimal.valueOf(-10L);
    assertThatThrownBy(() -> PizzaOrderUtils.getTotalAsString(total))
        .isExactlyInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void givenAPizzaOrderShouldReturnTheProperTotalPrice() {
    var input = TestHelper.getOrder();
    var actual = PizzaOrderUtils.getTotal(input);
    assertThat(actual).isEqualTo(new BigDecimal("29.00"));
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

  @Test
  void orderCanBePaid() {
    var order = TestHelper.getOrder();
    order.setOrderStatus(PizzaOrderStatus.OrderStatusEnum.PAID_AND_DELIVERED.getValue());
    assertThat(PizzaOrderUtils.orderCanBePaid(order)).isFalse();
  }

  @ParameterizedTest
  @MethodSource("it.s8.awesomepizza.helper.TestHelper#checkOrderStatus")
  void orderCanBeDeleted(String orderStatus, boolean canBePaid) {
    var order = TestHelper.getOrder();
    order.setOrderStatus(orderStatus);
    log.info("Order status {}", order.getOrderStatus());
    assertThat(PizzaOrderUtils.orderCanBeDeleted(order)).isEqualTo(canBePaid);
  }
}
