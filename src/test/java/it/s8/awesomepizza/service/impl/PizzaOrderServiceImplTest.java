package it.s8.awesomepizza.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

import it.s8.awesomepizza.entity.PizzaOrder;
import it.s8.awesomepizza.helper.TestHelper;
import it.s8.awesomepizza.repository.PizzaOrderRepository;
import jakarta.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openapitools.model.PizzaOrderStatus;

@ExtendWith(MockitoExtension.class)
class PizzaOrderServiceImplTest {

  @InjectMocks private PizzaOrderServiceImpl pizzaOrderService;

  @Mock private PizzaOrderRepository pizzaOrderRepository;

  @Captor ArgumentCaptor<PizzaOrder> orderCaptor;

  @Test
  void givenAValidOrderIdShouldReturnTheOrder() {

    var order =
        PizzaOrder.builder()
            .pizzaList(List.of())
            .orderStatus(PizzaOrderStatus.OrderStatusEnum.IN_PROCESS.getValue())
            .username("test")
            .build();

    when(pizzaOrderRepository.findById(anyLong())).thenReturn(Optional.of(order));
    var actual = pizzaOrderService.getOrder(anyLong());

    assertThat(actual).isNotNull();
    assertThat(actual.getId()).isEqualTo(order.getId());
  }

  @Test
  void givenAnInvalidOrderIdShouldThrowException() {

    when(pizzaOrderRepository.findById(anyLong())).thenReturn(Optional.empty());
    assertThatThrownBy(() -> pizzaOrderService.getOrder(2L))
        .isExactlyInstanceOf(EntityNotFoundException.class)
        .hasMessageContaining("not found");
  }

  @Test
  void givenAValidPIzzaOrderShouldReturnedProperly() {

    var order = TestHelper.getOrder();
    when(pizzaOrderRepository.save(order)).thenReturn(order);

    var orderSaved = pizzaOrderService.saveOrder(order);
    assertThat(orderSaved).isNotNull().extracting(PizzaOrder::getId).isEqualTo(order.getId());
  }

  @Test
  void prepareOrder() {

    var order = TestHelper.getOrder();

    when(pizzaOrderRepository.findById(anyLong())).thenReturn(Optional.of(order));

    pizzaOrderService.prepareOrder(order.getId());
    verify(pizzaOrderRepository).findById(order.getId());
    verify(pizzaOrderRepository).save(orderCaptor.capture());
    assertThat(orderCaptor).isNotNull();
    assertThat(orderCaptor.getValue().getTotal()).isEqualTo(BigDecimal.valueOf(2900));
    assertThat(orderCaptor.getValue().getOrderStatus())
        .isEqualTo(PizzaOrderStatus.OrderStatusEnum.READY_FOR_DELIVERY.getValue());
  }
}
