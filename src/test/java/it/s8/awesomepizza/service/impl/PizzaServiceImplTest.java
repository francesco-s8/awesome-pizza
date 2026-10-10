package it.s8.awesomepizza.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import it.s8.awesomepizza.entity.Pizza;
import it.s8.awesomepizza.exception.AwesomePizzaException;
import it.s8.awesomepizza.repository.PizzaRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openapitools.model.PizzaDto;

@Slf4j
@ExtendWith(MockitoExtension.class)
class PizzaServiceImplTest {

  @InjectMocks PizzaServiceImpl pizzaService;

  @Mock PizzaRepository pizzaRepository;

  @Test
  void givenAListOfPizzasShouldReturnSuccessfully() {

    var input = List.of("Margherita", "Marinara");

    when(pizzaRepository.findByNameIn(input)).thenReturn(getPizzasStub());

    var actual = pizzaService.getPizzas(input);
    assertThat(actual).isNotEmpty().hasSize(2);
  }

  private List<Pizza> getPizzasStub() {

    var firstPizza =
        Pizza.builder().id(1L).name("Margherita").price(BigDecimal.ONE).description("Margherita").version(0).build();
    var secondPizza =
        Pizza.builder().id(1L).name("Marinara").price(BigDecimal.valueOf(2L)).description("Marinara").version(0).build();

    return List.of(firstPizza, secondPizza);
  }

  @Test
  void givenANotExistingPizzaShouldRaiseAnException() {
    var pizzasName = List.of("Quattro stagioni");
    when(pizzaRepository.findByNameIn(any())).thenReturn(List.of());
    assertThatThrownBy(() -> pizzaService.getPizzas(pizzasName))
        .isExactlyInstanceOf(AwesomePizzaException.class)
        .hasMessageContaining("Some pizzas not found");
  }

  @Test
  void addPizza() {

    when(pizzaRepository.save(any(Pizza.class))).thenReturn(getPizzasStub().getFirst());
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
            Optional.of(
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

  @Test
  void givenAValidPizzaIdShouldReturnThePizzaDetails() {

    when(pizzaRepository.findById(any())).thenReturn(Optional.of(getPizzasStub().getFirst()));
    var actual = pizzaService.getPizzaDetails(1L);
    log.info("Actual pizza details: {}", actual);
    assertThat(actual).isNotNull().extracting("name").isEqualTo("Margherita");
    assertThat(actual).isNotNull().extracting("price").isEqualTo(BigDecimal.ONE);
  }
}
