package it.s8.awesomepizza.service.impl;

import it.s8.awesomepizza.entity.Pizza;
import it.s8.awesomepizza.exception.AwesomePizzaException;
import it.s8.awesomepizza.repository.PizzaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openapitools.model.PizzaDto;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

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

  @Test
  void addPizza() {

    var pizza =
        Pizza.builder()
            .id(1L)
            .name("Margherita")
            .description("Margherita")
            .price(BigDecimal.ONE)
            .build();

    when(pizzaRepository.save(any(Pizza.class))).thenReturn(pizza);
    var actual =
        pizzaService.addPizza(
            PizzaDto.builder()
                .name("Margherita")
                .price(BigDecimal.ONE)
                .description("Margherita")
                .build());
    assertThat(actual).isNotNull().extracting("price").isEqualTo(BigDecimal.ONE);
  }

  @Test
  void updatePizza() {

    var pizzaInfo =
        PizzaDto.builder()
            .name("Margherita")
            .price(BigDecimal.valueOf(10L))
            .description("Margherita")
            .build();
    when(pizzaRepository.findById(1L))
        .thenReturn(
            java.util.Optional.of(
                Pizza.builder()
                    .id(1L)
                    .name("Margherita")
                    .description("Margherita")
                    .price(BigDecimal.ONE)
                    .build()));
    when(pizzaRepository.save(any(Pizza.class)))
        .thenReturn(
            Pizza.builder()
                .id(1L)
                .name("Margherita")
                .description("Margherita")
                .price(BigDecimal.valueOf(10L))
                .build());

    var actual = pizzaService.updatePizza(1L, pizzaInfo);
    assertThat(actual).isNotNull().extracting("price").isEqualTo(BigDecimal.valueOf(10L));
  }
}
