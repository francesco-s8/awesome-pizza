package it.s8.awesomepizza.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import it.s8.awesomepizza.entity.Pizza;
import it.s8.awesomepizza.entity.PizzaOrder;
import it.s8.awesomepizza.service.PizzaOrderService;
import jakarta.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PizzaOrderPaymentFacadeImplTest {

  @InjectMocks private PizzaOrderPaymentFacadeImpl paymentFacade;

  @Mock private PizzaOrderService pizzaOrderService;

  private static PizzaOrder getOrder() {
    return PizzaOrder.builder()
        .username("alice")
        .pizzaList(
            List.of(
                Pizza.builder().name("Margherita").price(new BigDecimal("10.00")).build(),
                Pizza.builder().name("Capricciosa").price(new BigDecimal("11.50")).build(),
                Pizza.builder().name("Marinara").price(new BigDecimal("7.50")).build()))
        .build();
  }

  @Test
  void givenOrderWithMultiplePizzasShouldCalculateTotalCentsIgnoringNullPrices() {
    var order = getOrder();

    when(pizzaOrderService.getOrder(1L)).thenReturn(order);

    var actual = paymentFacade.calculateOrderTotal(1L);

    assertThat(actual.getName()).isEqualTo("alice");
    assertThat(actual.getTotal()).isEqualTo(NumberFormat.getCurrencyInstance().format(2900 / 100.0));
  }

  @Test
  void givenMissingOrderShouldPropagateException() {
    when(pizzaOrderService.getOrder(99L))
        .thenThrow(new EntityNotFoundException("Order 99 not found"));

    assertThatThrownBy(() -> paymentFacade.calculateOrderTotal(99L))
        .isInstanceOf(EntityNotFoundException.class)
        .hasMessageContaining("Order 99 not found");
  }
}
