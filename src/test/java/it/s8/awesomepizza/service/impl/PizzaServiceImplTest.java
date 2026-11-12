package it.s8.awesomepizza.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import it.s8.awesomepizza.entity.Ingredient;
import it.s8.awesomepizza.entity.Pizza;
import it.s8.awesomepizza.exception.AwesomePizzaException;
import it.s8.awesomepizza.helper.TestHelper;
import it.s8.awesomepizza.repository.IngredientRepository;
import it.s8.awesomepizza.repository.PizzaRepository;
import jakarta.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openapitools.model.PizzaDto;

@Slf4j
@ExtendWith(MockitoExtension.class)
class PizzaServiceImplTest {

  private static List<String> input;

  @InjectMocks PizzaServiceImpl pizzaService;

  @Mock PizzaRepository pizzaRepository;
  @Mock IngredientRepository ingredientRepository;

  @Captor private ArgumentCaptor<Pizza> pizzaArgumentCaptor;

  @BeforeAll
  static void setUp() {
    input = TestHelper.getPizzaListStub().stream().map(Pizza::getName).toList();
  }

  @Test
  void givenAListOfPizzasShouldReturnSuccessfully() {

    when(pizzaRepository.findByNameIn(input)).thenReturn(TestHelper.getPizzaListStub());

    var actual = pizzaService.getPizzas(input);
    assertThat(actual).isNotEmpty().hasSize(3);
  }

  @Test
  void givenAValidPizzaListButNoPizzaIsAvailable() {

    var pizzas = TestHelper.getPizzaListStub();
    pizzas.forEach(p -> p.setIsAvailable(Boolean.FALSE));
    when(pizzaRepository.findByNameIn(input)).thenReturn(pizzas);

    assertThatThrownBy(() -> pizzaService.getPizzas(input))
        .isExactlyInstanceOf(AwesomePizzaException.class)
        .hasMessageContaining("No pizzas available");
  }

  @Test
  void givenANotExistingPizzaShouldRaiseAnException() {

    var orderPizzaList = List.of("Speciale");
    when(pizzaRepository.findByNameIn(any())).thenReturn(List.of());
    assertThatThrownBy(() -> pizzaService.getPizzas(orderPizzaList))
        .isExactlyInstanceOf(AwesomePizzaException.class)
        .hasMessageContaining("Some pizzas not found");
  }

  @Test
  void addPizza() {

    String margherita = "Margherita";
    var pizzaStub =
        PizzaDto.builder()
            .name(margherita)
            .price(BigDecimal.ONE)
            .description(margherita)
            .ingredients(List.of("Tomato", "Mozzarella"))
            .build();

    when(pizzaRepository.save(any(Pizza.class))).thenAnswer(i -> i.getArgument(0));
    var actual = pizzaService.addPizza(pizzaStub);
    assertThat(actual).isNotNull();
    assertThat(actual.getName()).isNotEmpty().isEqualTo(margherita);
    assertThat(actual.getDescription()).isNotEmpty().isEqualTo(margherita);
    assertThat(actual.getPrice()).isEqualTo(BigDecimal.ONE);
    assertThat(actual.getIngredients())
        .extracting(Ingredient::getName)
        .containsExactly("Tomato", "Mozzarella");
  }

  @Test
  void updatePizzaReplacesTheExistingIngredientsList() {
    var oldIngredient = Ingredient.builder().id(1L).name("Pomodoro").build();
    var pizza =
        Pizza.builder()
            .id(1L)
            .name("Margherita")
            .description("Classic pizza")
            .price(BigDecimal.ONE)
            .ingredients(List.of(oldIngredient))
            .build();
    var mozzarella = Ingredient.builder().id(2L).name("Mozzarella").build();
    var basilico = Ingredient.builder().id(3L).name("Basilico").build();
    var update =
        PizzaDto.builder()
            .description("Classic pizza")
            .price(BigDecimal.valueOf(2))
            .ingredients(List.of("Mozzarella", "Basilico"))
            .build();
    when(pizzaRepository.findById(1L)).thenReturn(Optional.of(pizza));
    when(ingredientRepository.findByNameIn(List.of("Mozzarella", "Basilico")))
        .thenReturn(List.of(mozzarella, basilico));
    when(pizzaRepository.save(any(Pizza.class))).thenAnswer(i -> i.getArgument(0));

    var updatedPizza = pizzaService.updatePizza(1L, update);

    assertThat(updatedPizza.getIngredients())
        .extracting(Ingredient::getName)
        .containsExactly("Mozzarella", "Basilico");
    verify(pizzaRepository).save(pizza);
  }

  @Test
  void updatePizzaCreatesMissingIngredients() {
    var pizza = TestHelper.getPizzaListStub().getFirst();
    var newIngredient = Ingredient.builder().name("Basilico").build();
    var update =
        PizzaDto.builder().price(BigDecimal.valueOf(2)).ingredients(List.of("Basilico")).build();
    when(pizzaRepository.findById(1L)).thenReturn(Optional.of(pizza));
    when(ingredientRepository.findByNameIn(List.of("Basilico"))).thenReturn(List.of());
    when(ingredientRepository.saveAll(any())).thenReturn(List.of(newIngredient));
    when(pizzaRepository.save(any(Pizza.class))).thenAnswer(i -> i.getArgument(0));

    var updatedPizza = pizzaService.updatePizza(1L, update);

    assertThat(updatedPizza.getIngredients())
        .extracting(Ingredient::getName)
        .containsExactly("Basilico");
  }

  @Test
  void updatePizza() {

    var pizzaInfo =
        PizzaDto.builder()
            .name("Margherita")
            .price(BigDecimal.valueOf(10L))
            .description("Margherita NEW")
            .build();
    when(pizzaRepository.findById(1L))
        .thenReturn(
            Optional.of(
                Pizza.builder()
                    .id(1L)
                    .name("Margherita")
                    .description("Margherita")
                    .price(BigDecimal.ONE)
                    .build()));
    when(pizzaRepository.save(any(Pizza.class))).thenAnswer(i -> i.getArgument(0));

    var actual = pizzaService.updatePizza(1L, pizzaInfo);
    assertThat(actual).isNotNull();
    assertThat(actual.getDescription()).isNotEmpty().isEqualTo("Margherita NEW");
    assertThat(actual.getPrice()).isEqualTo(BigDecimal.valueOf(10L));
  }

  @Test
  void givenANullPizzaDescriptionAndSamePriceShouldNotUpdateRecord() {
    var pizza = TestHelper.getPizzaListStub().getFirst();
    var pizzaInfo =
        PizzaDto.builder()
            .description(null)
            .ingredients(pizza.getIngredients().stream().map(Ingredient::getName).toList())
            .price(BigDecimal.valueOf(10.00).setScale(2, RoundingMode.HALF_UP))
            .build();
    when(pizzaRepository.findById(any()))
        .thenReturn(Optional.of(TestHelper.getPizzaListStub().getFirst()));
    var actual = pizzaService.updatePizza(1L, pizzaInfo);
    assertThat(actual).isNotNull();
    verify(pizzaRepository, never()).save(any());
  }

  @Test
  void givenAnInvalidPizzaIdShouldBeThrownAEntityNotFoundException() {
    var pizzaInfo = PizzaDto.builder().description(null).price(BigDecimal.ONE).build();
    when(pizzaRepository.findById(anyLong())).thenReturn(Optional.empty());
    assertThatThrownBy(() -> pizzaService.updatePizza(1L, pizzaInfo))
        .isExactlyInstanceOf(EntityNotFoundException.class);
    verify(pizzaRepository, times(0)).save(any());
  }

  @Test
  void givenNUllDescriptionButDifferentPriceRecordShouldBeUpdated() {
    var pizzaInfo = PizzaDto.builder().description(null).price(BigDecimal.valueOf(5)).build();
    when(pizzaRepository.findById(any()))
        .thenReturn(Optional.of(TestHelper.getPizzaListStub().getFirst()));
    pizzaService.updatePizza(1L, pizzaInfo);
    verify(pizzaRepository, times(1)).save(any());
  }

  @Test
  void givenAValidPizzaIdShouldReturnThePizzaDetails() {

    when(pizzaRepository.findById(any()))
        .thenReturn(Optional.of(TestHelper.getPizzaListStub().getFirst()));
    var actual = pizzaService.getPizzaDetails(1L);
    assertThat(actual).isNotNull().extracting("name").isEqualTo("Margherita");
    assertThat(actual)
        .isNotNull()
        .extracting("price")
        .isEqualTo(BigDecimal.valueOf(10.00).setScale(2, RoundingMode.HALF_UP));
  }

  @Test
  void givenAnInValidPizzaIdShouldReturnThrownAnException() {

    when(pizzaRepository.findById(any())).thenReturn(Optional.empty());
    assertThatThrownBy(() -> pizzaService.getPizzaDetails(1L))
        .isExactlyInstanceOf(EntityNotFoundException.class);
  }

  @Test
  void getAvailablePizzas() {

    var pizza = TestHelper.getPizzaListStub();

    when(pizzaRepository.findByIsAvailableTrue()).thenReturn(List.of(pizza.getLast()));
    List<Pizza> actual = pizzaService.getAvailablePizzas();
    assertThat(actual).hasSize(1);
    assertThat(actual.getFirst().getName()).isEqualTo("Marinara");
  }

  @Test
  void getAvailablePizzasException() {

    var pizza = TestHelper.getPizzaListStub();
    pizza.forEach(p -> p.setIsAvailable(Boolean.FALSE));

    when(pizzaRepository.findByIsAvailableTrue()).thenReturn(List.of());
    assertThatThrownBy(() -> pizzaService.getAvailablePizzas())
        .isExactlyInstanceOf(EntityNotFoundException.class)
        .hasMessageContaining("No pizzas available");
  }

  @Test
  void givenAValidPizzaIdShouldBeSetToBeDisabled() {
    var pizza = TestHelper.getPizzaListStub().getFirst();
    when(pizzaRepository.findById(pizza.getId())).thenReturn(Optional.of(pizza));
    pizzaService.disablePizza(pizza.getId());
    verify(pizzaRepository, times(1)).save(pizzaArgumentCaptor.capture());
    assertThat(pizzaArgumentCaptor.getValue().getIsAvailable()).isEqualTo(Boolean.FALSE);
  }

  @Test
  void givenAnInValidPizzaIdShouldBeThrownAEntityNotFoundException() {
    when(pizzaRepository.findById(1L)).thenReturn(Optional.empty());
    assertThatThrownBy(() -> pizzaService.disablePizza(1L))
        .isExactlyInstanceOf(EntityNotFoundException.class);
  }
}
