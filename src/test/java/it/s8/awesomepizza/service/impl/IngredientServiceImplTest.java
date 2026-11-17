package it.s8.awesomepizza.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import it.s8.awesomepizza.entity.Ingredient;
import it.s8.awesomepizza.exception.InvalidInputException;
import it.s8.awesomepizza.helper.TestHelper;
import it.s8.awesomepizza.repository.IngredientRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class IngredientServiceImplTest {

  @InjectMocks IngredientServiceImpl ingredientService;
  @Mock IngredientRepository ingredientRepository;

  @Captor ArgumentCaptor<Ingredient> ingredientArgumentCaptor;

  @Test
  void addIngredient() {
    when(ingredientRepository.findByName("Truffle")).thenReturn(Optional.empty());
    when(ingredientRepository.save(any(Ingredient.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    var ingredient = ingredientService.addIngredient("Truffle");

    assertThat(ingredient.getName()).isEqualTo("Truffle");
    assertThat(ingredient.getIsAvailable()).isTrue();
    verify(ingredientRepository).save(ingredientArgumentCaptor.capture());
    assertThat(ingredientArgumentCaptor.getValue().getIsAvailable()).isTrue();
  }

  @Test
  void addIngredientRejectsExistingIngredient() {
    when(ingredientRepository.findByName("Basil"))
        .thenReturn(TestHelper.getIngredientByName("Basil"));

    assertThatThrownBy(() -> ingredientService.addIngredient("Basil"))
        .isExactlyInstanceOf(InvalidInputException.class);
    verify(ingredientRepository, never()).save(any(Ingredient.class));
  }

  @Test
  void retrieveAllAvailableIngredients() {

    when(ingredientRepository.findByIsAvailableTrue())
        .thenReturn(TestHelper.getIngredientListStub());
    assertThat(ingredientService.retrieveAllAvailableIngredients())
        .hasSameSizeAs(TestHelper.getIngredientListStub());
  }

  @Test
  void retrieveAvailableIngredientsByNames() {
    var names = List.of("Tomato", "Basil");
    var availableIngredients = List.of(TestHelper.getIngredientByName("Tomato").orElseThrow());
    when(ingredientRepository.findByNameInAndIsAvailableTrue(names))
        .thenReturn(availableIngredients);

    assertThat(ingredientService.retrieveAvailableIngredients(names))
        .containsExactlyElementsOf(availableIngredients);
  }

  @Test
  void disableIngredient() {
    var ingredientName = "Basil";
    when(ingredientRepository.findByNameAndIsAvailableTrue(ingredientName))
        .thenReturn(TestHelper.getIngredientByName(ingredientName));
    ingredientService.disableIngredient(ingredientName);

    verify(ingredientRepository).save(ingredientArgumentCaptor.capture());
    assertThat(ingredientArgumentCaptor.getValue()).isNotNull();
    assertThat(ingredientArgumentCaptor.getValue().getIsAvailable()).isFalse();
  }
  @Test
  void givenAnIngredientToBeDisabledButAlreadyIsShouldBeThrownAnException(){

    when(ingredientRepository.findByNameAndIsAvailableTrue("Basil")).thenReturn(Optional.empty());

    assertThatThrownBy(()->ingredientService.disableIngredient("Basil")).isExactlyInstanceOf(EntityNotFoundException.class);

  }
}
