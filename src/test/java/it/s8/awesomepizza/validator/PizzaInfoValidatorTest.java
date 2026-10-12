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
  void givenAnInvalidDtoShouldThrowAndException() {

    var pizza = PizzaDto.builder().build();
    assertThatThrownBy(() -> pizzaInfoValidator.isValid(pizza))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Pizza info not provided");
  }

  @Test
  void givenAValidInputShouldReturnTrue() {
    var pizza =
        PizzaDto.builder()
            .price(BigDecimal.valueOf(10L))
            .name("a pizza")
            .description("A delicious pizza")
            .build();
    assertThat(pizzaInfoValidator.isValid(pizza)).isTrue();
  }

  @Test
  void givenANegativePriceShouldThrowAnException() {
    var pizza =
        PizzaDto.builder()
            .price(BigDecimal.valueOf(-10L))
            .name("a pizza")
            .description("A delicious pizza")
            .build();

    assertThatThrownBy(() -> pizzaInfoValidator.isValid(pizza))
        .isInstanceOf(AwesomePizzaException.class)
        .hasMessageContaining("Pizza price is null or less then zero");
  }
  @Test
  void givenAnullPriceAnExceptionShouldBeRaised(){
    var pizza =
            PizzaDto.builder()
                    .name("a pizza")
                    .description("A delicious pizza")
                    .build();
    assertThatThrownBy(() -> pizzaInfoValidator.isValid(pizza))
            .isInstanceOf(AwesomePizzaException.class)
            .hasMessageContaining("Pizza price is null or less then zero");
  }
}
