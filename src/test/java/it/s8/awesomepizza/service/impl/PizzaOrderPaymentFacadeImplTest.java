package it.s8.awesomepizza.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import it.s8.awesomepizza.helper.TestHelper;
import it.s8.awesomepizza.service.PizzaOrderService;
import jakarta.persistence.EntityNotFoundException;
import java.text.NumberFormat;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PizzaOrderPaymentFacadeImplTest {

  @InjectMocks private PizzaOrderPaymentFacadeImpl paymentFacade;

  @Mock private PizzaOrderService pizzaOrderService;

  @Test
  void givenOrderWithMultiplePizzasShouldCalculateTotalCentsIgnoringNullPrices() {
    var order = TestHelper.getOrder();

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
