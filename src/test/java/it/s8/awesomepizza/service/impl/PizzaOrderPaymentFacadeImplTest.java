package it.s8.awesomepizza.service.impl;

import it.s8.awesomepizza.entity.PizzaOrder;
import it.s8.awesomepizza.exception.AwesomePizzaException;
import it.s8.awesomepizza.exception.OrderNotReadyException;
import it.s8.awesomepizza.helper.TestHelper;
import it.s8.awesomepizza.service.PizzaOrderService;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openapitools.model.PizzaOrderPaymentInfo;
import org.openapitools.model.PizzaOrderStatus;

import java.math.BigDecimal;
import java.text.NumberFormat;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@Slf4j
@ExtendWith(MockitoExtension.class)
class PizzaOrderPaymentFacadeImplTest {

  private static final String ORDER_BEFORE_PAYMENT = "Order before payment: {}";
  @InjectMocks private PizzaOrderPaymentFacadeImpl paymentFacade;

  @Mock private PizzaOrderService pizzaOrderService;

  private static void printOrder(PizzaOrder order) {
    log.info(ORDER_BEFORE_PAYMENT, order);
  }

  @Test
  void givenOrderWithMultiplePizzasShouldCalculateTotalCentsIgnoringNullPrices() {
    var order = TestHelper.getOrder();

    order.setOrderStatus(PizzaOrderStatus.OrderStatusEnum.READY_FOR_DELIVERY.getValue());
    when(pizzaOrderService.getOrder(order.getId())).thenReturn(order);

    var actual = paymentFacade.calculateOrderTotal(order.getId());

    assertThat(actual.getName()).isEqualTo("alice");
    assertThat(actual.getTotal())
        .isEqualTo(NumberFormat.getCurrencyInstance().format(2900 / 100.0));
  }

  @Test
  void givenOrderNotReadyForDeliveryShouldThrowException() {
    var order = TestHelper.getOrder();
    order.setOrderStatus(PizzaOrderStatus.OrderStatusEnum.IN_PROCESS.getValue());
    when(pizzaOrderService.getOrder(order.getId())).thenReturn(order);

    assertThatThrownBy(() -> paymentFacade.calculateOrderTotal(1L))
        .isInstanceOf(AwesomePizzaException.class)
        .hasMessageContaining("is not ready for payment");
  }

  @Test
  void givenMissingOrderShouldPropagateException() {
    when(pizzaOrderService.getOrder(99L))
        .thenThrow(new EntityNotFoundException("Order 99 not found"));

    assertThatThrownBy(() -> paymentFacade.calculateOrderTotal(99L))
        .isInstanceOf(EntityNotFoundException.class)
        .hasMessageContaining("Order 99 not found");
  }

  @Test
  void givenAValidPaymentInfoShouldInvokeSaveOrderMethodOnce() {

    var order = TestHelper.getOrder();
    order.setOrderStatus(PizzaOrderStatus.OrderStatusEnum.TO_BE_PAID.getValue());
    order.setTotal(BigDecimal.ONE);
    printOrder(order);
    when(pizzaOrderService.getOrder(order.getId())).thenReturn(order);
    var pizzaOrderPaymentInfo =
        PizzaOrderPaymentInfo.builder()
            .paymentMethod(PizzaOrderPaymentInfo.PaymentMethodEnum.CREDIT_CARD)
            .build();
    paymentFacade.processPayment(order.getId(), pizzaOrderPaymentInfo);
    verify(pizzaOrderService, times(1)).saveOrder(order);
  }

  @Test
  void givenAValidPaymentInfoAndTheOrderStatusIsNotReadyShouldThrowException() {

    var order = TestHelper.getOrder();
    order.setOrderStatus("IN_PROGRESS");
    order.setTotal(BigDecimal.ONE);
    printOrder(order);
    when(pizzaOrderService.getOrder(order.getId())).thenReturn(order);
    var pizzaOrderPaymentInfo =
        PizzaOrderPaymentInfo.builder()
            .paymentMethod(PizzaOrderPaymentInfo.PaymentMethodEnum.CREDIT_CARD)
            .build();
    assertThatThrownBy(() -> paymentFacade.processPayment(1L, pizzaOrderPaymentInfo))
        .isExactlyInstanceOf(OrderNotReadyException.class)
        .hasMessageContaining("is not ready for delivery, cannot process payment");
  }
}
