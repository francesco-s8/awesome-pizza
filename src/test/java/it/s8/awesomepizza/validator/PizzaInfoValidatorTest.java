package it.s8.awesomepizza.validator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import it.s8.awesomepizza.exception.InvalidInputException;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.openapitools.model.PizzaDto;

class PizzaInfoValidatorTest {

  private final PizzaInfoValidator pizzaInfoValidator = new PizzaInfoValidator();

  private static PizzaDto getPizza() {
    return PizzaDto.builder()
        .price(new BigDecimal("10.00"))
        .name("a pizza")
        .description("A delicious pizza")
        .build();
  }

  @Test
  void givenAValidPizzaPriceShouldReturnTrue() {
    var pizza = getPizza();

    var actual = pizzaInfoValidator.isPizzaPriceValid(pizza.getPrice());
    assertThat(actual).isTrue();
  }

  @Test
  void givenANegativePriceShouldThrowAnException() {

    var price = BigDecimal.valueOf(-10);
    assertThatThrownBy(() -> pizzaInfoValidator.isPizzaPriceValid(price))
        .isInstanceOf(InvalidInputException.class)
        .hasMessageContaining("Pizza price can't be zero or less");
  }

  @Test
  void givenAZeroPriceShouldThrowAnException() {

    assertThatThrownBy(() -> pizzaInfoValidator.isPizzaPriceValid(BigDecimal.ZERO))
        .isInstanceOf(InvalidInputException.class)
        .hasMessageContaining("Pizza price can't be zero or less");
  }

  @Test
  void isValidPizzaInfo() {

    var newPizza = PizzaDto.builder().build();
    assertThatThrownBy(() -> pizzaInfoValidator.isValidPizzaInfo(newPizza))
        .isExactlyInstanceOf(InvalidInputException.class);
  }

  @Test
  void isValidPizzaName() {
    var newPizza = PizzaDto.builder().description("dummy").price(BigDecimal.ONE).build();
    assertThatThrownBy(() -> pizzaInfoValidator.isValidPizzaInfo(newPizza))
        .isExactlyInstanceOf(InvalidInputException.class);
  }

  @Test
  void givenAValidPizzaShouldReturnTrue() {
    assertThat(pizzaInfoValidator.isValidPizzaInfo(getPizza())).isTrue();
  }
}
