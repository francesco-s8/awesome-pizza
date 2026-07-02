package it.s8.awesomepizza.repository;

import static org.assertj.core.api.Assertions.assertThat;

import it.s8.awesomepizza.config.JpaConfig;
import it.s8.awesomepizza.entity.Pizza;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;

@DataJpaTest
@Import(JpaConfig.class)
class PizzaRepositoryTest {

  static List<Pizza> pizzaList;
  @Autowired PizzaRepository pizzaRepository;

  @BeforeAll
  static void setup() {

    pizzaList =
        List.of(
            Pizza.builder()
                .name("Margherita")
                .description("'La classica con pomodoro, mozzarella e basilico'")
                .price(7.00F)
                .build(),
            Pizza.builder()
                .name("Marinara")
                .description("'Pomodoro, aglio e origano'")
                .price(5.00F)
                .build());
  }

  @Test
  void givenAnExistingPizzaShouldBeFoundAndReturnSuccessfully() {

    pizzaRepository.saveAll(pizzaList);
    var actual = pizzaRepository.findByNameIn(List.of("Margherita"));
    assertThat(actual).isNotEmpty().hasSize(1);
  }

  @Test
  void givenAnNonExistingPizzaShouldBeFoundAndReturnEmpty() {
    var actual = pizzaRepository.findByNameIn(List.of("Gourmet"));
    assertThat(actual).isEmpty();
  }
}
