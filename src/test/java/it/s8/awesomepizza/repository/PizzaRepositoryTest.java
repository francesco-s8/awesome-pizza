package it.s8.awesomepizza.repository;

import static org.assertj.core.api.Assertions.assertThat;

import it.s8.awesomepizza.config.JpaConfig;
import it.s8.awesomepizza.entity.Ingredient;
import it.s8.awesomepizza.entity.Pizza;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;

@DataJpaTest
@Import(JpaConfig.class)
class PizzaRepositoryTest {

  private final List<Pizza> pizzaList =
      List.of(
          Pizza.builder()
              .name("Margherita")
              .description("'La classica con pomodoro, mozzarella e basilico'")
              .price(new BigDecimal("7.00"))
              .ingredients(
                  List.of(
                      Ingredient.builder().name("Pomodoro").build(),
                      Ingredient.builder().name("Mozzarella").build(),
                      Ingredient.builder().name("Basilico").build()))
              .isAvailable(Boolean.TRUE)
              .build(),
          Pizza.builder()
              .name("Marinara")
              .description("'Pomodoro, aglio e origano'")
              .price(new BigDecimal("5.00"))
              .ingredients(
                  List.of(
                      Ingredient.builder().name("Pomodoro").build(),
                      Ingredient.builder().name("Aglio").build(),
                      Ingredient.builder().name("Origano").build()))
              .isAvailable(Boolean.FALSE)
              .build());

  @Autowired PizzaRepository pizzaRepository;

  @BeforeEach
  void setupData() {
    pizzaRepository.saveAll(pizzaList);
  }

  @AfterEach
  void cleanData() {
    pizzaRepository.deleteAll();
  }

  @Test
  void givenAnExistingPizzaShouldBeFoundAndReturnSuccessfully() {

    var actual = pizzaRepository.findByNameIn(List.of("Margherita"));
    assertThat(actual).isNotEmpty().hasSize(1);
    assertThat(actual.getFirst().getPrice()).isEqualTo(new BigDecimal("7.00"));
    assertThat(actual.getFirst().getIngredients()).hasSize(3);
  }

  @Test
  void givenAnNonExistingPizzaShouldBeFoundAndReturnEmpty() {
    var actual = pizzaRepository.findByNameIn(List.of("Gourmet"));
    assertThat(actual).isEmpty();
  }

  @Test
  void findByIsAvailableTrue() {
    pizzaRepository.saveAll(pizzaList);
    var availablePizza = pizzaRepository.findByIsAvailableTrue();
    assertThat(availablePizza).isNotEmpty().hasSize(1);
    assertThat(availablePizza.getFirst()).extracting("name").isEqualTo("Margherita");
  }
}
