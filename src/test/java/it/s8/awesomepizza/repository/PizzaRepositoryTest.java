package it.s8.awesomepizza.repository;

import static org.assertj.core.api.Assertions.assertThat;

import it.s8.awesomepizza.config.JpaConfig;
import it.s8.awesomepizza.helper.TestHelper;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;

@DataJpaTest
@Import(JpaConfig.class)
class PizzaRepositoryTest {

  @Autowired PizzaRepository pizzaRepository;

  @BeforeEach
  void setupData() {
    pizzaRepository.saveAll(TestHelper.getPizzaListStub());
  }

  @AfterEach
  void cleanData() {
    pizzaRepository.deleteAll();
  }

  @Test
  void givenAnExistingPizzaShouldBeFoundAndReturnSuccessfully() {

    var actual = pizzaRepository.findByNameIn(List.of("Margherita"));
    assertThat(actual).isNotEmpty().hasSize(1);
    assertThat(actual.getFirst().getPrice()).isEqualTo(new BigDecimal("10.00"));
    assertThat(actual.getFirst().getIngredients()).hasSize(3);
  }

  @Test
  void givenAnNonExistingPizzaShouldBeFoundAndReturnEmpty() {
    var actual = pizzaRepository.findByNameIn(List.of("Gourmet"));
    assertThat(actual).isEmpty();
  }

  @Test
  void findByIsAvailableTrue() {
    var availablePizza = pizzaRepository.findByIsAvailableTrue();
    assertThat(availablePizza).isNotEmpty().hasSize(2);
    assertThat(availablePizza.getFirst()).extracting("name").isEqualTo("Margherita");
  }
}
