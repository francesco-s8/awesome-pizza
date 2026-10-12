package it.s8.awesomepizza.validator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import it.s8.awesomepizza.exception.AwesomePizzaException;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.openapitools.model.PizzaDto;

class PizzaInfoValidatorTest {

  private final PizzaInfoValidator pizzaInfoValidator = new PizzaInfoValidator();

  @Test
  void givenAValidPrizzaPriceShouldReturnTrue() {
    var pizza =
        PizzaDto.builder()
            .price(BigDecimal.valueOf(10L))
            .name("a pizza")
            .description("A delicious pizza")
            .build();

    var actual = pizzaInfoValidator.isPizzaPriceValid(pizza.getPrice());
    assertThat(actual).isTrue();
  }

  @Test
  void givenANegativePriceShouldThrowAnException() {

    var price = BigDecimal.valueOf(-10);
    assertThatThrownBy(() -> pizzaInfoValidator.isPizzaPriceValid(price))
        .isInstanceOf(AwesomePizzaException.class)
        .hasMessageContaining("Pizza price can't be less then zero");
  }

  @Test
  void givenAZeroPriceShouldThrowAnException() {

    assertThatThrownBy(() -> pizzaInfoValidator.isPizzaPriceValid(BigDecimal.ZERO))
        .isInstanceOf(AwesomePizzaException.class)
        .hasMessageContaining("Pizza price can't be zero or less then");
  }
}
