package it.s8.awesomepizza.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import it.s8.awesomepizza.entity.Pizza;
import it.s8.awesomepizza.helper.TestHelper;
import java.util.List;
import org.junit.jupiter.api.Test;

class PizzaListToPizzaDtoListMapperTest {

  private final PizzaListToPizzaDtoListMapper pizzaListToPizzaDtoListMapper =
      new PizzaListToPizzaDtoListMapper();

  @Test
  void givenAPizzaListShouldBeConvertedProperly() {



    var actual = pizzaListToPizzaDtoListMapper.apply(TestHelper.getPizzaListStub());
    assertThat(actual).isNotEmpty().hasSize(3);
  }

  @Test
  void givenAnEmptyListShouldReturnEmptyArray() {
    var actual = pizzaListToPizzaDtoListMapper.apply(List.of());
    assertThat(actual).isEmpty();
  }

  @Test
  void givenANullPizza() {
    var pizza = Pizza.builder().build();
    var actual = pizzaListToPizzaDtoListMapper.apply(List.of(pizza));
    assertThat(actual).isNotEmpty().hasSize(1);
    assertThat(actual.getFirst().getName()).isNull();
    assertThat(actual.getFirst().getDescription()).isNull();
    assertThat(actual.getFirst().getPrice()).isNull();
  }

  @Test
  void givenANullPizzaListShouldBeThrownANPE() {
    assertThatThrownBy(() -> pizzaListToPizzaDtoListMapper.apply(null))
        .isExactlyInstanceOf(NullPointerException.class);
  }
}
