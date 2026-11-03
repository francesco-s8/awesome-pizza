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
    var actual = pizzaToPizzaDtoMapper.apply(pizza);
    assertThat(actual).isNotNull().extracting("price").isEqualTo(BigDecimal.ONE);
    assertThat(actual.getPrice()).isGreaterThan(BigDecimal.ZERO);
  }

  @Test
  void givenANegativePizzaPriceShouldBeRaisedAnIllegalArgumentException() {
    var pizza =
        Pizza.builder()
            .name("A pizza")
            .description(" A pizza")
            .price(BigDecimal.valueOf(0))
            .build();

    assertThatThrownBy(() -> pizzaToPizzaDtoMapper.apply(pizza))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Price cannot be negative or zero");
  }
  @Test
  void givenAPizzaWithoutPizzaNameShouldBeRaisedAnIllegalArgumentException() {
    var pizza =
        Pizza.builder()
            .name(null)
            .description(" A pizza")
            .price(BigDecimal.valueOf(1))
            .build();

    assertThatThrownBy(() -> pizzaToPizzaDtoMapper.apply(pizza))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Name cannot be blank");
  }
  @Test
  void givenAPizzaWithoutPizzaDescriptionShouldBeRaisedAnIllegalArgumentException() {
    var pizza =
            Pizza.builder()
                    .name("A pizza")
                    .description(null)
                    .price(BigDecimal.valueOf(1))
                    .build();

    assertThatThrownBy(() -> pizzaToPizzaDtoMapper.apply(pizza))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Description cannot be blank");
  }
}
