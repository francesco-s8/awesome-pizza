package it.s8.awesomepizza.utils;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.openapitools.model.PizzaDto;

class PizzaInfoValidatorTest {


  @ParameterizedTest
  @MethodSource("validPrices")
  void givenAValidPizzaPriceShouldReturnTrue(BigDecimal price) {
    assertThat(PizzaInfoValidator.isPizzaPriceValid(price)).isTrue();
  }

  @ParameterizedTest
  @MethodSource("invalidPrices")
  void givenAnInvalidPizzaPriceShouldReturnFalse(BigDecimal price) {
    assertThat(PizzaInfoValidator.isPizzaPriceValid(price)).isFalse();
  }

  @Test
  void givenTheSmallestPositivePriceShouldReturnTrue() {
    assertThat(PizzaInfoValidator.isPizzaPriceValid(new BigDecimal("0.001"))).isTrue();
  }

  @Test
  void givenValidPizzaInfoShouldReturnTrue() {
    assertThat(PizzaInfoValidator.isValidPizzaInfo(validPizza())).isTrue();
  }

  @Test
  void givenNullPizzaShouldReturnFalse() {
    assertThat(PizzaInfoValidator.isValidPizzaInfo(null)).isFalse();
  }

  @ParameterizedTest
  @ValueSource(strings = {"", " ", "\t", "\n"})
  void givenAnEmptyNameShouldReturnFalse(String name) {
    var pizza = validPizza();
    pizza.setName(name);

    assertThat(PizzaInfoValidator.isValidPizzaInfo(pizza)).isFalse();
  }

  @Test
  void givenANullNameShouldReturnTrue() {
    var pizza = validPizza();
    pizza.setName(null);

    assertThat(PizzaInfoValidator.isValidPizzaInfo(pizza)).isTrue();
  }

  @Test
  void givenAValidNameShouldReturnTrue() {
    var pizza = validPizza();
    pizza.setName("  Margherita  ");

    assertThat(PizzaInfoValidator.isValidPizzaInfo(pizza)).isTrue();
  }

  @ParameterizedTest
  @ValueSource(strings = {"", " ", "\t", "\n"})
  void givenAnEmptyDescriptionShouldReturnFalse(String description) {
    var pizza = validPizza();
    pizza.setDescription(description);

    assertThat(PizzaInfoValidator.isValidPizzaInfo(pizza)).isFalse();
  }

  @Test
  void givenANullDescriptionShouldReturnTrue() {
    var pizza = validPizza();
    pizza.setDescription(null);

    assertThat(PizzaInfoValidator.isValidPizzaInfo(pizza)).isTrue();
  }

  @Test
  void givenAValidDescriptionShouldReturnTrue() {
    var pizza = validPizza();
    pizza.setDescription("  Tomato and mozzarella  ");

    assertThat(PizzaInfoValidator.isValidPizzaInfo(pizza)).isTrue();
  }

  @Test
  void givenEmptyIngredientsShouldReturnFalse() {
    var pizza = validPizza();
    pizza.setIngredients(List.of());

    assertThat(PizzaInfoValidator.isValidPizzaInfo(pizza)).isFalse();
  }

  @Test
  void givenNullIngredientsShouldReturnTrue() {
    var pizza = validPizza();
    pizza.setIngredients(null);

    assertThat(PizzaInfoValidator.isValidPizzaInfo(pizza)).isTrue();
  }

  @ParameterizedTest
  @MethodSource("invalidPrices")
  void givenInvalidPriceInPizzaShouldReturnFalse(BigDecimal price) {
    var pizza = validPizza();
    pizza.setPrice(price);

    assertThat(PizzaInfoValidator.isValidPizzaInfo(pizza)).isFalse();
  }

  private static PizzaDto validPizza() {
    return new PizzaDto()
        .name("Margherita")
        .description("Tomato and mozzarella")
        .price(new BigDecimal("10.50"))
        .ingredients(List.of("Tomato", "Mozzarella"));
  }

  private static Stream<BigDecimal> validPrices() {
    return Stream.of(new BigDecimal("0.01"), new BigDecimal("10.50"), new BigDecimal("999.99"));
  }

  private static Stream<BigDecimal> invalidPrices() {
    return Stream.of(null, BigDecimal.ZERO, new BigDecimal("-10"));
  }

}
