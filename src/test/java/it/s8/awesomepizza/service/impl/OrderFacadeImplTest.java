package it.s8.awesomepizza.service.impl;

import it.s8.awesomepizza.dto.OrderRequest;
import it.s8.awesomepizza.entity.Pizza;
import it.s8.awesomepizza.entity.PizzaOrder;
import it.s8.awesomepizza.exception.PizzaOrderException;
import it.s8.awesomepizza.service.PizzaOrderService;
import it.s8.awesomepizza.service.PizzaQueueService;
import it.s8.awesomepizza.service.PizzaService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderFacadeImplTest {

    @InjectMocks
    OrderFacadeImpl orderFacade;

    @Mock
    PizzaService pizzaService;
    @Mock
    PizzaQueueService pizzaQueueService;
    @Mock
    PizzaOrderService pizzaOrderService;

    @Test
    void givenAValidOrderRequestShouldReturnTheOrderId() {

        var pizza =
                Pizza.builder().id(1L).name("Margherita").description("Margherita").version(0).build();

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
                .thenThrow(new PizzaOrderException("Some pizzas not found"));
        assertThatThrownBy(() -> orderFacade.processOrder(new OrderRequest("test", specialPizza)))
                .hasMessageContaining("Some pizzas not found");
    }
}
