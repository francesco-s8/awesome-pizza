package it.s8.awesomepizza.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import it.s8.awesomepizza.entity.PizzaOrder;
import it.s8.awesomepizza.repository.PizzaOrderRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openapitools.model.PizzaOrderStatus;

@ExtendWith(MockitoExtension.class)
class PizzaOrderServiceImplTest {

  @InjectMocks private PizzaOrderServiceImpl orderService;

  @Mock private PizzaOrderRepository pizzaOrderRepository;

  @Test
  void givenAValidOrderIdShouldReturnTheOrder() {

    var order =
        PizzaOrder.builder()
            .pizzaList(List.of())
            .orderStatus(PizzaOrderStatus.OrderStatusEnum.IN_PROCESS.getValue())
            .username("test")
            .build();

    when(pizzaOrderRepository.findById(anyLong())).thenReturn(Optional.of(order));
    var actual = orderService.getOrderStatus(anyLong());

    assertThat(actual).isNotNull();
    assertThat(actual.getId()).isEqualTo(order.getId());
  }

  @Test
  void givenAnInvalidOrderIdShouldThrowException() {

    when(pizzaOrderRepository.findById(anyLong())).thenReturn(Optional.empty());
    assertThatThrownBy(() -> orderService.getOrderStatus(2L))
        .isExactlyInstanceOf(EntityNotFoundException.class)
        .hasMessageContaining("not found");
  }
}
