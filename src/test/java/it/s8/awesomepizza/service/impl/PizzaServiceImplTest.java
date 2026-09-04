package it.s8.awesomepizza.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import it.s8.awesomepizza.entity.Pizza;
import it.s8.awesomepizza.exception.AwesomePizzaException;
import it.s8.awesomepizza.repository.PizzaRepository;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PizzaServiceImplTest {

  @InjectMocks PizzaServiceImpl pizzaService;

  @Mock PizzaRepository pizzaRepository;

  @Test
  void givenAListOfPizzasShouldReturnSuccessfully() {

    var firstPizza =
        Pizza.builder().id(1L).name("Margherita").description("Margherita").version(0).build();
    var secondPizza =
        Pizza.builder().id(1L).name("Marinara").description("Marinara").version(0).build();

    var input = List.of("Margherita", "Marinara");

    when(pizzaRepository.findByNameIn(input)).thenReturn(List.of(firstPizza, secondPizza));

    var actual = pizzaService.getPizzas(input);
    assertThat(actual).isNotEmpty().hasSize(2);
  }

  @Test
  void givenANotExistingPizzaShouldRaiseAnException() {
    var input = List.of("Gourmet");
    when(pizzaRepository.findByNameIn(input)).thenReturn(List.of());

    assertThatThrownBy(() -> pizzaService.getPizzas(input))
        .isExactlyInstanceOf(AwesomePizzaException.class)
        .hasMessageContaining("Some pizzas not found");
  }
}
