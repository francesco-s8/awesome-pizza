package it.s8.awesomepizza.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import it.s8.awesomepizza.entity.Pizza;
import it.s8.awesomepizza.entity.PizzaOrder;
import it.s8.awesomepizza.exception.AwesomePizzaException;
import it.s8.awesomepizza.service.PizzaOrderService;
import it.s8.awesomepizza.service.PizzaService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openapitools.model.PizzaOrderRequest;
import org.springframework.context.ApplicationContext;

@ExtendWith(MockitoExtension.class)
class PizzaOrderFacadeImplTest {

  public static final String IN_PROCESS = "IN_PROCESS";
  public static final String SOME_PIZZAS_NOT_FOUND = "Some pizzas not found";
  public static final String MARGHERITA_PIZZA = "Margherita";
  @InjectMocks PizzaOrderFacadeImpl orderFacade;

  @Mock PizzaService pizzaService;
  @Mock PizzaOrderService pizzaOrderService;
  @Mock ApplicationContext applicationContext;

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
    doNothing().when(applicationContext).publishEvent(any());

    when(pizzaOrderService.saveOrder(any()))
        .thenReturn(PizzaOrder.builder().pizzaList(List.of(pizza)).id(1L).build());

    var request =
        PizzaOrderRequest.builder().customerName("test").pizzas(List.of(anyString())).build();
    var actual = orderFacade.processOrder(request);
    assertThat(actual).isNotNull();
    assertThat(actual.orderId()).isEqualTo(1L);
  }

  @Test
  void givenANotAvailablePizzaShouldThrowException() {

    var specialPizza = List.of("Special pizza");
    var request = PizzaOrderRequest.builder().customerName("test").pizzas(specialPizza).build();
    when(pizzaService.getPizzas(specialPizza))
        .thenThrow(new AwesomePizzaException(SOME_PIZZAS_NOT_FOUND));
    assertThatThrownBy(() -> orderFacade.processOrder(request))
        .hasMessageContaining(SOME_PIZZAS_NOT_FOUND);
  }

  @Test
  void givenAValidOrderIdShouldReturnTheOrderStatus() {

    when(pizzaOrderService.getOrderStatus(anyLong()))
        .thenReturn(PizzaOrder.builder().orderStatus(IN_PROCESS).build());

    var actual = orderFacade.retrieveOrderStatus(1L);
    assertThat(actual).isNotNull().isEqualTo(IN_PROCESS);
  }
}
