package it.s8.awesomepizza.utils;

import static org.assertj.core.api.Assertions.assertThat;

import it.s8.awesomepizza.helper.TestHelper;
import java.math.BigDecimal;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.openapitools.model.PizzaDto;

class PizzaInfoValidatorTest {


  @Test
  void givenAValidPizzaPriceShouldReturnTrue() {
    var pizza = TestHelper.getPizzaListStub().getFirst();

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

}
