package it.s8.awesomepizza.utils;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.openapitools.model.PizzaDto;

class PizzaInfoValidatorTest {

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

    var actual = PizzaInfoValidator.isPizzaPriceValid(pizza.getPrice());
    assertThat(actual).isTrue();
  }

  @Test
  void givenANegativePriceShouldThrowAnException() {

    var price = BigDecimal.valueOf(-10);
    assertThat(PizzaInfoValidator.isPizzaPriceValid(price)).isFalse();
  }

  @Test
  void givenAZeroPriceShouldThrowAnException() {

    assertThat(PizzaInfoValidator.isPizzaPriceValid(BigDecimal.ZERO)).isFalse();
  }

  @Test
  void isPizzaInfoValid() {
    var stub = Instancio.create(PizzaDto.class);
    assertThat(PizzaInfoValidator.isValidPizzaInfo(stub)).isTrue();
  }

  @Test
  void isValidPizzaInfo() {

    var newPizza = PizzaDto.builder().build();
    assertThat(PizzaInfoValidator.isValidPizzaInfo(newPizza)).isFalse();
  }

  @Test
  void isValidPizzaName() {
    var newPizza = PizzaDto.builder().description("dummy").price(BigDecimal.ONE).build();
    assertThat(PizzaInfoValidator.isValidPizzaInfo(newPizza)).isFalse();
  }
}
