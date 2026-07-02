package it.s8.awesomepizza.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import it.s8.awesomepizza.entity.Pizza;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;

class PizzaListToPizzaDtoListMapperTest {

  private final PizzaListToPizzaDtoListMapper pizzaListToPizzaDtoListMapper =
      new PizzaListToPizzaDtoListMapper();

  @Test
  void givenAPizzaListShouldBeConvertedProperly() {

    var margherita =
        Pizza.builder()
            .id(1L)
            .name("Margherita")
            .price(new BigDecimal("8.00"))
            .description("La classica con pomodoro, mozzarella e basilico")
            .build();
    var marinara =
        Pizza.builder()
            .id(2L)
            .name("Marinara")
            .price(new BigDecimal("7.00"))
            .description("Pomodoro, aglio e origano")
            .build();
    var input = List.of(marinara, margherita);

    var actual = pizzaListToPizzaDtoListMapper.apply(input);
    assertThat(actual).hasSize(2);
  }

  @Test
  void givenAnEmptyListShouldReturnEmptyArray() {
    var actual = pizzaListToPizzaDtoListMapper.apply(List.of());
    assertThat(actual).isEmpty();
  }
}
