package it.s8.awesomepizza.validator;

import it.s8.awesomepizza.exception.AwesomePizzaException;
import org.junit.jupiter.api.Test;
import org.openapitools.model.PizzaDto;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PizzaInfoValidatorTest {

  private final PizzaInfoValidator pizzaInfoValidator = new PizzaInfoValidator();

  @Test
  void givenAnInvalidDtoShouldThrowAndException() {

    var pizza = PizzaDto.builder().build();
    assertThatThrownBy(() -> pizzaInfoValidator.isValid(pizza))
        .isInstanceOf(AwesomePizzaException.class)
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
  void givenANegativePriceShouldThrowAnException(){
    var pizza =
            PizzaDto.builder()
                    .price(BigDecimal.valueOf(-10L))
                    .name("a pizza")
                    .description("A delicious pizza")
                    .build();

    assertThatThrownBy(() -> pizzaInfoValidator.isValid(pizza))
            .isInstanceOf(AwesomePizzaException.class)
            .hasMessageContaining("Pizza price cannot be negative");

  }
}
