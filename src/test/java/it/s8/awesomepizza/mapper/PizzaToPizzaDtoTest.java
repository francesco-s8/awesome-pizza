package it.s8.awesomepizza.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import it.s8.awesomepizza.entity.Pizza;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class PizzaToPizzaDtoTest {

  private final PizzaToPizzaDtoMapper pizzaToPizzaDtoMapper = new PizzaToPizzaDtoMapper();

  @Test
  void givenAValidInputShouldBeTransformProperly() {

    var pizza =
        Pizza.builder().name("A pizza").description(" A pizza").price(BigDecimal.ONE).build();

    assertThat(pizzaToPizzaDtoMapper.apply(pizza))
        .isNotNull()
        .extracting("price")
        .isEqualTo(BigDecimal.ONE);
  }

  @Test
    void givenANegativePizzaPriceShouldBeRaisedAnIllegalArgumentException() {
        var pizza = Pizza.builder().name("A pizza").description(" A pizza").price(BigDecimal.valueOf(-1)).build();

            assertThatThrownBy(() -> pizzaToPizzaDtoMapper.apply(pizza))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Price cannot be negative");

    }
}
