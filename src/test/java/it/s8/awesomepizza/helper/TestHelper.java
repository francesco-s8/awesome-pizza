package it.s8.awesomepizza.helper;

import it.s8.awesomepizza.entity.Pizza;
import it.s8.awesomepizza.entity.PizzaOrder;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.params.provider.Arguments;
import org.openapitools.model.PizzaOrderStatus;

public class TestHelper {

  public static PizzaOrder getOrder() {
    return PizzaOrder.builder()
        .id(1L)
        .username("alice")
        .pizzaList(
            List.of(
                Pizza.builder()
                    .description("Margherita")
                    .name("Margherita")
                    .price(new BigDecimal("10.00"))
                    .build(),
                Pizza.builder()
                    .description("Capricciosa")
                    .name("Capricciosa")
                    .price(new BigDecimal("11.50"))
                    .build(),
                Pizza.builder()
                    .description("Marinara")
                    .name("Marinara")
                    .price(new BigDecimal("7.50"))
                    .build()))
        .build();
  }

  public static Stream<Arguments> checkOrderStatus() {
    return Stream.of(
        Arguments.of(PizzaOrderStatus.OrderStatusEnum.PAID_AND_DELIVERED.getValue(), false),
        Arguments.of(PizzaOrderStatus.OrderStatusEnum.TO_BE_PAID.getValue(), false),
        Arguments.of(PizzaOrderStatus.OrderStatusEnum.CANCELLED.getValue(), false),
        Arguments.of(PizzaOrderStatus.OrderStatusEnum.IN_PROCESS.getValue(), true));
  }
}
