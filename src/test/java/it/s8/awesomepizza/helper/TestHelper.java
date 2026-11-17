package it.s8.awesomepizza.helper;

import it.s8.awesomepizza.entity.Ingredient;
import it.s8.awesomepizza.entity.Pizza;
import it.s8.awesomepizza.entity.PizzaOrder;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import lombok.experimental.UtilityClass;
import org.junit.jupiter.params.provider.Arguments;
import org.openapitools.model.PizzaDto;
import org.openapitools.model.PizzaOrderStatus;

@UtilityClass
public class TestHelper {

  public PizzaOrder getPizzaOrderStub() {

    return PizzaOrder.builder()
        .id(1L)
        .username("alice")
        .pizzaList(
            List.of(
                Pizza.builder()
                    .description("Margherita")
                    .name("Margherita")
                    .ingredients(standardIngredientsByPizzaName("Margherita"))
                    .price(new BigDecimal("10.00"))
                    .build(),
                Pizza.builder()
                    .description("Capricciosa")
                    .name("Capricciosa")
                    .ingredients(standardIngredientsByPizzaName("Capricciosa"))
                    .price(new BigDecimal("11.50"))
                    .build(),
                Pizza.builder()
                    .description("Marinara")
                    .name("Marinara")
                    .ingredients(standardIngredientsByPizzaName("Marinara"))
                    .price(new BigDecimal("11.50"))
                    .build()))
        .build();
  }

  public List<Pizza> getPizzaListStub() {
    return List.of(
        Pizza.builder()
            .description("Margherita")
            .name("Margherita")
            .ingredients(standardIngredientsByPizzaName("Margherita"))
            .price(new BigDecimal("10.00"))
            .isAvailable(Boolean.TRUE)
            .build(),
        Pizza.builder()
            .description("Capricciosa")
            .name("Capricciosa")
            .ingredients(standardIngredientsByPizzaName("Capricciosa"))
            .price(new BigDecimal("11.50"))
            .isAvailable(Boolean.FALSE)
            .build(),
        Pizza.builder()
            .description("Marinara")
            .name("Marinara")
            .ingredients(standardIngredientsByPizzaName("Marinara"))
            .price(new BigDecimal("9.50"))
            .isAvailable(Boolean.TRUE)
            .build());
  }

  public List<PizzaDto> getPizzaDtoListStub() {
    return getPizzaListStub().stream()
        .map(pizza ->
            PizzaDto.builder()
                .name(pizza.getName())
                .description(pizza.getDescription())
                .price(pizza.getPrice())
                .ingredients(pizza.getIngredients().stream().map(Ingredient::getName).toList())
                .build())
        .toList();
  }

  public List<Ingredient> getIngredientListStub() {
    return List.of(
        Ingredient.builder().name("Tomato").isAvailable(Boolean.TRUE).build(),
        Ingredient.builder().name("Mozzarella").isAvailable(Boolean.TRUE).build(),
        Ingredient.builder().name("Basil").isAvailable(Boolean.TRUE).build(),
        Ingredient.builder().name("Ham").isAvailable(Boolean.TRUE).build(),
        Ingredient.builder().name("Mushrooms").isAvailable(Boolean.TRUE).build(),
        Ingredient.builder().name("Olives").isAvailable(Boolean.TRUE).build(),
        Ingredient.builder().name("Garlic").isAvailable(Boolean.TRUE).build(),
        Ingredient.builder().name("Olive Oil").isAvailable(Boolean.TRUE).build());
  }

  public Optional<Ingredient> getIngredientByName(String name) {

    return getIngredientListStub().stream()
        .filter(ingredient -> ingredient.getName().equals(name))
        .findFirst();
  }

  public List<Ingredient> standardIngredientsByPizzaName(String pizzaName) {

    return switch (pizzaName) {
      case "Margherita" ->
          List.of(
              Ingredient.builder().name("Tomato").build(),
              Ingredient.builder().name("Mozzarella").build(),
              Ingredient.builder().name("Basil").build());
      case "Capricciosa" ->
          List.of(
              Ingredient.builder().name("Tomato").build(),
              Ingredient.builder().name("Mozzarella").build(),
              Ingredient.builder().name("Ham").build(),
              Ingredient.builder().name("Mushrooms").build(),
              Ingredient.builder().name("Olives").build());
      case "Marinara" ->
          List.of(
              Ingredient.builder().name("Tomato").build(),
              Ingredient.builder().name("Garlic").build(),
              Ingredient.builder().name("Olive Oil").build());
      default -> List.of();
    };
  }

  public Stream<Arguments> checkOrderStatus() {
    return Stream.of(
        Arguments.of(PizzaOrderStatus.OrderStatusEnum.PAID_AND_DELIVERED.getValue(), false),
        Arguments.of(PizzaOrderStatus.OrderStatusEnum.TO_BE_PAID.getValue(), false),
        Arguments.of(PizzaOrderStatus.OrderStatusEnum.CANCELLED.getValue(), false),
        Arguments.of(PizzaOrderStatus.OrderStatusEnum.IN_PROCESS.getValue(), true));
  }
}
