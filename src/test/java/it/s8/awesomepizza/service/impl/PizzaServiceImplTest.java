package it.s8.awesomepizza.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import it.s8.awesomepizza.dto.PizzaDtoInfo;
import it.s8.awesomepizza.entity.Ingredient;
import it.s8.awesomepizza.entity.Pizza;
import it.s8.awesomepizza.exception.AwesomePizzaException;
import it.s8.awesomepizza.helper.TestHelper;
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
import org.openapitools.model.PizzaBaseDto;
import org.openapitools.model.PizzaDto;

@Slf4j
@ExtendWith(MockitoExtension.class)
class PizzaServiceImplTest {

  private static List<String> input;

  @InjectMocks PizzaServiceImpl pizzaService;

  @Mock PizzaRepository pizzaRepository;

  @Captor private ArgumentCaptor<Pizza> pizzaArgumentCaptor;

  @BeforeAll
  static void setUp() {
    input = TestHelper.getPizzaListStub().stream().map(Pizza::getName).toList();
  }

  @Test
  void givenAListOfPizzasShouldReturnSuccessfully() {
    var pizzas = TestHelper.getPizzaListStub();
    pizzas.forEach(pizza -> pizza.setIsAvailable(Boolean.TRUE));
    when(pizzaRepository.findByNameIn(input)).thenReturn(pizzas);

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
  void givenAPizzaWithAnUnavailableIngredientShouldRejectTheOrder() {
    var pizzaNames = List.of("Margherita");
    var pizzas = TestHelper.getPizzaListStub().subList(0, 1);
    pizzas.getFirst().getIngredients().getFirst().setIsAvailable(Boolean.FALSE);
    when(pizzaRepository.findByNameIn(pizzaNames)).thenReturn(pizzas);

    assertThatThrownBy(() -> pizzaService.getPizzas(pizzaNames))
        .isExactlyInstanceOf(AwesomePizzaException.class)
        .hasMessageContaining("unavailable ingredients");
  }

  @Test
  void givenAnOrderContainingAnUnavailablePizzaShouldRejectTheOrder() {
    var pizzas = TestHelper.getPizzaListStub();
    when(pizzaRepository.findByNameIn(input)).thenReturn(pizzas);

    assertThatThrownBy(() -> pizzaService.getPizzas(input))
        .isExactlyInstanceOf(AwesomePizzaException.class)
        .hasMessageContaining("Some pizzas are not available");
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
  void addPizzaReusesIngredientsAlreadyInTheCatalog() {

    var ingredientsStub =
        List.of(
            Ingredient.builder().id(1L).name("Tomato").build(),
            Ingredient.builder().id(2L).name("Olives").build(),
            Ingredient.builder().id(3L).name("Mushrooms").build());
    var pizzaDto =
        PizzaDto.builder()
            .name("Olive pizza")
            .price(BigDecimal.ONE)
            .description("Tomato, olives and mushrooms")
            .ingredients(List.of("Tomato", "Mushrooms", "Olives"))
            .build();

    when(pizzaRepository.save(any(Pizza.class))).thenAnswer(i -> i.getArgument(0));

    var actual = pizzaService.savePizza(new PizzaDtoInfo(pizzaDto, ingredientsStub));
    assertThat(actual).isNotNull();
    assertThat(actual.getName()).isNotEmpty().isEqualTo("Olive pizza");
    assertThat(actual.getIngredients()).isNotEmpty().hasSize(3);

    verify(pizzaRepository).save(pizzaArgumentCaptor.capture());

    assertThat(pizzaArgumentCaptor.getValue().getIngredients())
        .extracting(Ingredient::getName)
        .containsExactlyInAnyOrder("Tomato", "Mushrooms", "Olives");
  }

  @Test
  void updatePizzaCreatesMissingIngredients() {
    var pizza = TestHelper.getPizzaListStub().getFirst();
    var update = PizzaBaseDto.builder().price(BigDecimal.valueOf(2)).build();
    when(pizzaRepository.findById(1L)).thenReturn(Optional.of(pizza));
    when(pizzaRepository.save(any(Pizza.class))).thenAnswer(i -> i.getArgument(0));
    var updatedPizza = pizzaService.updatePizza(1L, update);
    assertThat(updatedPizza).isNotNull();
  }

  @Test
  void updatePizza() {

    var pizzaInfo =
        PizzaBaseDto.builder().price(BigDecimal.valueOf(10L)).description("Margherita NEW").build();
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
  void updatePizzaWithNoChangedFieldsShouldNotSave() {
    var pizza = TestHelper.getPizzaListStub().getFirst();
    var unchanged =
        PizzaBaseDto.builder()
            .description(pizza.getDescription())
            .price(pizza.getPrice())
            .build();
    when(pizzaRepository.findById(1L)).thenReturn(Optional.of(pizza));

    var actual = pizzaService.updatePizza(1L, unchanged);

    assertThat(actual).isSameAs(pizza);
    verify(pizzaRepository, never()).save(any());
  }

  @Test
  void updatePizzaShouldChangeOnlyDescription() {
    var pizza = Pizza.builder()
        .id(1L)
        .name("Margherita")
        .description("Old")
        .price(new BigDecimal("10.00"))
        .build();
    var update = PizzaBaseDto.builder()
        .description("New")
        .price(new BigDecimal("10.00"))
        .build();
    when(pizzaRepository.findById(1L)).thenReturn(Optional.of(pizza));
    when(pizzaRepository.save(any(Pizza.class))).thenAnswer(invocation -> invocation.getArgument(0));

    var actual = pizzaService.updatePizza(1L, update);

    assertThat(actual.getDescription()).isEqualTo("New");
    assertThat(actual.getPrice()).isEqualByComparingTo("10.00");
  }

  @Test
  void updatePizzaShouldChangeOnlyPrice() {
    var pizza = Pizza.builder()
        .id(1L)
        .name("Margherita")
        .description("Margherita")
        .price(new BigDecimal("10.00"))
        .build();
    var update = PizzaBaseDto.builder()
        .description("Margherita")
        .price(new BigDecimal("12.50"))
        .build();
    when(pizzaRepository.findById(1L)).thenReturn(Optional.of(pizza));
    when(pizzaRepository.save(any(Pizza.class))).thenAnswer(invocation -> invocation.getArgument(0));

    var actual = pizzaService.updatePizza(1L, update);

    assertThat(actual.getDescription()).isEqualTo("Margherita");
    assertThat(actual.getPrice()).isEqualByComparingTo("12.50");
  }

  @Test
  void givenAnInvalidPizzaIdShouldBeThrownAEntityNotFoundException() {
    var pizzaInfo = PizzaBaseDto.builder().description(null).price(BigDecimal.ONE).build();
    when(pizzaRepository.findById(anyLong())).thenReturn(Optional.empty());
    assertThatThrownBy(() -> pizzaService.updatePizza(1L, pizzaInfo))
        .isExactlyInstanceOf(EntityNotFoundException.class);
    verify(pizzaRepository, times(0)).save(any());
  }

  @Test
  void givenNUllDescriptionButDifferentPriceRecordShouldBeUpdated() {
    var pizzaInfo = PizzaBaseDto.builder().description(null).price(BigDecimal.valueOf(5)).build();
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
  void getAvailablePizzasWithIngredientShouldDelegateToRepository() {
    var pizzas = List.of(TestHelper.getPizzaListStub().getFirst());
    when(pizzaRepository.findByIngredients_NameAndIsAvailableTrue("Basil")).thenReturn(pizzas);

    assertThat(pizzaService.getAvailablePizzasWithIngredient("Basil")).isSameAs(pizzas);
  }

  @Test
  void getPizzasWithIngredientShouldDelegateToRepository() {
    var pizzas = List.of(TestHelper.getPizzaListStub().getFirst());
    when(pizzaRepository.findByIngredients_Name("Basil")).thenReturn(pizzas);

    assertThat(pizzaService.getPizzasWithIngredient("Basil")).isSameAs(pizzas);
  }

  @Test
  void checkIfPizzaExistsShouldReturnRepositoryResult() {
    var pizza = TestHelper.getPizzaListStub().getFirst();
    var pizzaDto = PizzaDto.builder().name("Margherita").build();
    when(pizzaRepository.findByNameIgnoreCase("Margherita")).thenReturn(Optional.of(pizza));

    assertThat(pizzaService.checkIfPizzaExists(pizzaDto)).contains(pizza);
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

  @Test
  void givenAValidPizzaIdShouldBeSetToBeEnabled() {
    var pizza = TestHelper.getPizzaListStub().getFirst();
    pizza.setIsAvailable(Boolean.FALSE);
    when(pizzaRepository.findById(pizza.getId())).thenReturn(Optional.of(pizza));

    pizzaService.enablePizza(pizza.getId());

    verify(pizzaRepository).save(pizzaArgumentCaptor.capture());
    assertThat(pizzaArgumentCaptor.getValue().getIsAvailable()).isEqualTo(Boolean.TRUE);
  }

  @Test
  void enablingPizzaWithAnInvalidIdShouldThrowNotFound() {
    when(pizzaRepository.findById(1L)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> pizzaService.enablePizza(1L))
        .isExactlyInstanceOf(EntityNotFoundException.class);
  }
}
