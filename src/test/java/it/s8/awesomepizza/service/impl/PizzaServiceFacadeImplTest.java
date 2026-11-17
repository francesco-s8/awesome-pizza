package it.s8.awesomepizza.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import it.s8.awesomepizza.dto.PizzaDtoInfo;
import it.s8.awesomepizza.exception.AwesomePizzaException;
import it.s8.awesomepizza.helper.TestHelper;
import it.s8.awesomepizza.repository.IngredientRepository;
import it.s8.awesomepizza.service.PizzaService;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openapitools.model.PizzaBaseDto;
import org.openapitools.model.PizzaDto;

@ExtendWith(MockitoExtension.class)
class PizzaServiceFacadeImplTest {

  @Mock private IngredientRepository ingredientRepository;

  @Mock private PizzaService pizzaService;

  @InjectMocks private PizzaServiceFacadeImpl pizzaServiceFacade;

  @Test
  void addNewPizzaLooksUpIngredientsAndSavesPizza() {
    var pizzaDto =
        PizzaDto.builder()
            .name("Margherita")
            .description("Tomato and mozzarella")
            .price(new BigDecimal("8.00"))
            .ingredients(List.of("Tomato", "Mozzarella"))
            .build();
    var ingredients = TestHelper.getIngredientListStub().subList(0, 2);
    var savedPizza = TestHelper.getPizzaListStub().getFirst();
    when(pizzaService.checkIfPizzaExists(pizzaDto)).thenReturn(Optional.empty());
    when(ingredientRepository.findByNameIn(pizzaDto.getIngredients())).thenReturn(ingredients);
    when(pizzaService.savePizza(any(PizzaDtoInfo.class))).thenReturn(savedPizza);

    var actual = pizzaServiceFacade.addNewPizza(pizzaDto);

    assertThat(actual).isSameAs(savedPizza);
    var pizzaInfoCaptor = ArgumentCaptor.forClass(PizzaDtoInfo.class);
    verify(pizzaService).savePizza(pizzaInfoCaptor.capture());
    assertThat(pizzaInfoCaptor.getValue().pizzaDto()).isSameAs(pizzaDto);
    assertThat(pizzaInfoCaptor.getValue().ingredients()).isEqualTo(ingredients);
  }

  @Test
  void addNewPizzaRejectsMissingIngredients() {
    var pizzaDto =
        PizzaDto.builder()
            .name("Margherita")
            .ingredients(List.of("Tomato", "Missing"))
            .build();
    when(pizzaService.checkIfPizzaExists(pizzaDto)).thenReturn(Optional.empty());
    when(ingredientRepository.findByNameIn(pizzaDto.getIngredients()))
        .thenReturn(TestHelper.getIngredientListStub().subList(0, 1));

    assertThatThrownBy(() -> pizzaServiceFacade.addNewPizza(pizzaDto))
        .isExactlyInstanceOf(AwesomePizzaException.class)
        .hasMessageContaining("Some ingredients");

    verify(pizzaService, never()).savePizza(any());
  }

  @Test
  void addNewPizzaRejectsUnavailableIngredients() {
    var pizzaDto =
        PizzaDto.builder().name("Margherita").ingredients(List.of("Tomato")).build();
    var unavailableIngredient = TestHelper.getIngredientListStub().getFirst();
    unavailableIngredient.setIsAvailable(false);
    when(pizzaService.checkIfPizzaExists(pizzaDto)).thenReturn(Optional.empty());
    when(ingredientRepository.findByNameIn(pizzaDto.getIngredients()))
        .thenReturn(List.of(unavailableIngredient));

    assertThatThrownBy(() -> pizzaServiceFacade.addNewPizza(pizzaDto))
        .isExactlyInstanceOf(AwesomePizzaException.class)
        .hasMessageContaining("Some ingredients are unavailable");

    verify(pizzaService, never()).savePizza(any());
  }

  @Test
  void addNewPizzaRejectsDuplicatePizza() {
    var pizzaDto = PizzaDto.builder().name("Margherita").ingredients(List.of("Tomato")).build();
    when(pizzaService.checkIfPizzaExists(pizzaDto))
        .thenReturn(Optional.of(TestHelper.getPizzaListStub().getFirst()));

    assertThatThrownBy(() -> pizzaServiceFacade.addNewPizza(pizzaDto))
        .isExactlyInstanceOf(AwesomePizzaException.class)
        .hasMessage("Pizza Margherita already exists");

    verifyNoInteractions(ingredientRepository);
    verify(pizzaService, never()).savePizza(any());
  }

  @Test
  void getIngredientsDeduplicatesNamesBeforeLookup() {
    var ingredients = TestHelper.getIngredientListStub().subList(0, 1);
    when(ingredientRepository.findByNameIn(List.of("Tomato"))).thenReturn(ingredients);

    var actual = pizzaServiceFacade.getIngredients(List.of("Tomato", "Tomato"));

    assertThat(actual).isEqualTo(ingredients);
    verify(ingredientRepository).findByNameIn(List.of("Tomato"));
  }

  @Test
  void getIngredientsRejectsMissingIngredients() {
    when(ingredientRepository.findByNameIn(List.of("Tomato", "Basil")))
        .thenReturn(TestHelper.getIngredientListStub().subList(0, 1));

    assertThatThrownBy(() -> pizzaServiceFacade.getIngredients(List.of("Tomato", "Basil")))
        .isExactlyInstanceOf(AwesomePizzaException.class)
        .hasMessageContaining("Some ingredients");
  }

  @Test
  void getIngredientsRejectsEmptyResult() {
    when(ingredientRepository.findByNameIn(List.of("Tomato"))).thenReturn(List.of());

    assertThatThrownBy(() -> pizzaServiceFacade.getIngredients(List.of("Tomato")))
        .isExactlyInstanceOf(AwesomePizzaException.class)
        .hasMessageContaining("Some ingredients");
  }

  @Test
  void disablePizzaDelegatesToPizzaService() {
    pizzaServiceFacade.disablePizza(42L);

    verify(pizzaService).disablePizza(42L);
  }

  @Test
  void pizzaMenuReturnsAvailablePizzasFromPizzaService() {
    var pizzas = TestHelper.getPizzaListStub();
    when(pizzaService.getAvailablePizzas()).thenReturn(pizzas);

    assertThat(pizzaServiceFacade.pizzaMenu()).isSameAs(pizzas);
  }

  @Test
  void pizzaDetailsReturnsPizzaFromPizzaService() {
    var pizza = TestHelper.getPizzaListStub().getFirst();
    when(pizzaService.getPizzaDetails(42L)).thenReturn(pizza);

    assertThat(pizzaServiceFacade.pizzaDetails(42L)).isSameAs(pizza);
  }

  @Test
  void updatePizzaDelegatesToPizzaService() {
    var pizza = TestHelper.getPizzaListStub().getFirst();
    var update = PizzaBaseDto.builder().price(new BigDecimal("9.00")).build();
    when(pizzaService.updatePizza(42L, update)).thenReturn(pizza);

    assertThat(pizzaServiceFacade.updatePizza(42L, update)).isSameAs(pizza);
  }
}
