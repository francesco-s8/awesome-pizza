package it.s8.awesomepizza.service.impl;

import it.s8.awesomepizza.OrderStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.util.List;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PizzaQueueServiceImplTest {

    @InjectMocks
    PizzaQueueServiceImpl pizzaQueueService;
    @Mock
    RabbitTemplate rabbitTemplate;

    @Test
    void sendOrder_sendsMessageAndPersistsOrder_whenValidRequest() {

        var orderEntity =
                it.s8.awesomepizza.entity.PizzaOrder.builder()
                        .id(1L)
                        .username("ME")
                        .orderStatus(OrderStatus.IN_PROCESS.name())
                        .pizzas(List.of())
                        .build();
        pizzaQueueService.sendOrder(orderEntity.getId());

        verify(rabbitTemplate, times(1)).convertAndSend("orders", orderEntity.getId());
    }
}
