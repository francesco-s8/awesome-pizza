package it.s8.awesomepizza.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

import it.s8.awesomepizza.entity.PizzaOrder;
import it.s8.awesomepizza.exception.AwesomePizzaException;
import it.s8.awesomepizza.exception.AwesomePizzaOperationNotSupportedException;
import it.s8.awesomepizza.exception.OrderNotReadyToBePaidException;
import it.s8.awesomepizza.helper.TestHelper;
import jakarta.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openapitools.model.PizzaOrderPaymentInfo;
import org.openapitools.model.PizzaOrderStatus;

@Slf4j
@ExtendWith(MockitoExtension.class)
class PizzaOrderPaymentFacadeImplTest {

  private static final String ORDER_BEFORE_PAYMENT = "Order before payment: {}";
  @InjectMocks private PizzaOrderPaymentFacadeImpl paymentFacade;

  @Mock private PizzaOrderServiceImpl pizzaOrderService;

  @Captor private ArgumentCaptor<PizzaOrder> pizzaOrderArgumentCaptor;

  private static void printOrder(PizzaOrder order) {
    log.info(ORDER_BEFORE_PAYMENT, order);
  }

  @BeforeAll
  static void setUp() {
    Locale.setDefault(Locale.ITALY);
  }

  @Test
  void givenOrderWithMultiplePizzasShouldCalculateTotalCentsIgnoringNullPrices() {
    var order = TestHelper.getOrder();

    order.setOrderStatus(PizzaOrderStatus.OrderStatusEnum.READY_FOR_DELIVERY.getValue());
    when(pizzaOrderService.getOrder(order.getId())).thenReturn(order);

    var actual = paymentFacade.calculateOrderTotal(order.getId());

    var expected = NumberFormat.getCurrencyInstance(Locale.ITALY).format(2900 / 100.00);
    assertThat(actual.getName()).isEqualTo("alice");
    assertThat(actual.getTotal()).isEqualTo(expected);
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
    verify(pizzaOrderService, times(0)).saveOrder(any());
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
            .total(BigDecimal.ONE)
            .paymentMethod(PizzaOrderPaymentInfo.PaymentMethodEnum.CREDIT_CARD)
            .build();

    paymentFacade.processPayment(order.getId(), pizzaOrderPaymentInfo);
    verify(pizzaOrderService, times(1)).saveOrder(pizzaOrderArgumentCaptor.capture());
    assertThat(pizzaOrderArgumentCaptor.getValue().getOrderStatus())
        .isEqualTo(PizzaOrderStatus.OrderStatusEnum.PAID_AND_DELIVERED.getValue());
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
            .total(BigDecimal.ONE)
            .build();
    assertThatThrownBy(() -> paymentFacade.processPayment(1L, pizzaOrderPaymentInfo))
        .isExactlyInstanceOf(OrderNotReadyToBePaidException.class)
        .hasMessageContaining(" is not ready to be paid, cannot process payment");
  }

  @Test
  void calculateOrderTotal() {
    var order = TestHelper.getOrder();
    order.setOrderStatus(PizzaOrderStatus.OrderStatusEnum.READY_FOR_DELIVERY.getValue());
    when(pizzaOrderService.getOrder(order.getId())).thenReturn(order);

    var actual = paymentFacade.calculateOrderTotal(1L);
    assertThat(actual).isNotNull();
    assertThat(actual.getTotal()).startsWith("29,00");
  }

  @Test
  void processPayment() {

    var order = TestHelper.getOrder();
    order.setOrderStatus(PizzaOrderStatus.OrderStatusEnum.IN_PROCESS.getValue());
    when(pizzaOrderService.getOrder(order.getId())).thenReturn(order);
    assertThatThrownBy(() -> paymentFacade.calculateOrderTotal(1L))
        .isExactlyInstanceOf(AwesomePizzaException.class);
  }

  @Test
  void deletePayment() {

    doThrow(AwesomePizzaOperationNotSupportedException.class)
        .when(pizzaOrderService)
        .deleteOrder(anyLong());

    assertThatThrownBy(() -> paymentFacade.deletePayment(1L))
            .isExactlyInstanceOf(AwesomePizzaOperationNotSupportedException.class);


  }
}
