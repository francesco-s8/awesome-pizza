package it.s8.awesomepizza.helper;

import it.s8.awesomepizza.entity.Pizza;
import it.s8.awesomepizza.entity.PizzaOrder;
import java.math.BigDecimal;
import java.util.List;

public class TestHelper {


    public static PizzaOrder getOrder() {
      return PizzaOrder.builder()
          .username("alice")
          .pizzaList(
              List.of(
                  Pizza.builder().name("Margherita").price(new BigDecimal("10.00")).build(),
                  Pizza.builder().name("Capricciosa").price(new BigDecimal("11.50")).build(),
                  Pizza.builder().name("Marinara").price(new BigDecimal("7.50")).build()))
          .build();
    }
}
