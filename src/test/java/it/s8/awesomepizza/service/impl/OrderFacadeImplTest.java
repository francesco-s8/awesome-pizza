package it.s8.awesomepizza.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import it.s8.awesomepizza.dto.OrderRequest;
import it.s8.awesomepizza.entity.Pizza;
import it.s8.awesomepizza.entity.PizzaOrder;
import it.s8.awesomepizza.exception.AwesomePizzaException;
import it.s8.awesomepizza.exception.PizzaOrderAlreadyDeliveredException;
import it.s8.awesomepizza.service.PizzaOrderService;
import it.s8.awesomepizza.service.PizzaQueueService;
import it.s8.awesomepizza.service.PizzaService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OrderFacadeImplTest {

  public static final String IN_PROCESS = "IN_PROCESS";
  public static final String SOME_PIZZAS_NOT_FOUND = "Some pizzas not found";
  public static final String MARGHERITA_PIZZA = "Margherita";
  public static final String READY = "READY";
  @InjectMocks OrderFacadeImpl orderFacade;

  @Mock PizzaService pizzaService;
  @Mock PizzaQueueService pizzaQueueService;
  @Mock PizzaOrderService pizzaOrderService;

  @Test
  void givenAValidOrderRequestShouldReturnTheOrderId() {

    var pizza =
        Pizza.builder()
            .id(1L)
            .name(MARGHERITA_PIZZA)
            .description(MARGHERITA_PIZZA)
            .version(0)
            .build();

    when(pizzaService.getPizzas(any())).thenReturn(List.of(pizza));
    doNothing().when(pizzaQueueService).sendOrder(anyLong());

    when(pizzaOrderService.saveOrder(any()))
        .thenReturn(PizzaOrder.builder().pizzas(List.of(pizza)).id(1L).build());

    var actual = orderFacade.processOrder(new OrderRequest("test", List.of(anyString())));
    assertThat(actual).isNotNull();
    assertThat(actual.orderId()).isEqualTo(1L);
  }

  @Test
  void givenANotAvailablePizzaShouldThrowException() {

    var specialPizza = List.of("Special pizza");
    when(pizzaService.getPizzas(specialPizza))
        .thenThrow(new AwesomePizzaException(SOME_PIZZAS_NOT_FOUND));
    assertThatThrownBy(() -> orderFacade.processOrder(new OrderRequest("test", specialPizza)))
        .hasMessageContaining(SOME_PIZZAS_NOT_FOUND);
  }

  @Test
  void givenAValidOrderIdShouldReturnTheOrderStatus() {

    when(pizzaOrderService.getOrderStatus(anyLong()))
        .thenReturn(PizzaOrder.builder().orderStatus(IN_PROCESS).build());

    var actual = orderFacade.retrieveOrderStatus(1L);
    assertThat(actual).isNotNull().isEqualTo(IN_PROCESS);
  }

  @Test
  void givenAReadyOrderShouldThrowException() {

    when(pizzaOrderService.getOrderStatus(anyLong()))
        .thenReturn(PizzaOrder.builder().orderStatus(READY).build());

    assertThatThrownBy(() -> orderFacade.retrieveOrderStatus(1L))
        .isExactlyInstanceOf(PizzaOrderAlreadyDeliveredException.class)
        .hasMessageContaining("Order 1 is already delivered");
  }
}
