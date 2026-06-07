package it.s8.awesomepizza.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

import it.s8.awesomepizza.config.JpaConfig;
import it.s8.awesomepizza.entity.Pizza;
import it.s8.awesomepizza.entity.PizzaOrder;
import it.s8.awesomepizza.enums.OrderStatus;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;

@Slf4j
@DataJpaTest
@Import(JpaConfig.class)
class PizzaOrderRepositoryTest {

  @Autowired PizzaOrderRepository pizzaOrderRepository;

  @BeforeEach
  void setup() {

    var order =
        PizzaOrder.builder()
            .username("ME")
            .orderStatus(OrderStatus.IN_PROCESS.name())
            .pizzas(
                List.of(
                    Pizza.builder()
                        .name("Margherita")
                        .description("'La classica con pomodoro, mozzarella e basilico'")
                        .build(),
                    Pizza.builder()
                        .name("Marinara")
                        .description("'Pomodoro, aglio e origano'")
                        .build()))
            .build();

    var saved = pizzaOrderRepository.save(order);
    log.info("Saved order: {}", saved);
  }

  @Test
  void getOrderById() {
    var order = pizzaOrderRepository.findById(1L);
    assertTrue(order.isPresent());
    assertThat(order.get().getUsername()).isEqualTo("ME");
    assertThat(order.get().getPizzas()).hasSize(2);
  }

  @Test
  void findAllOrders() {
    var orders = pizzaOrderRepository.findAll();
    assertThat(orders).hasSize(1);
    assertThat(orders.getFirst().getUsername()).isEqualTo("ME");
  }
}
