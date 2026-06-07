package it.s8.awesomepizza.repository;

import it.s8.awesomepizza.config.JpaConfig;
import it.s8.awesomepizza.entity.Pizza;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.List;

@DataJpaTest
@Import(JpaConfig.class)
class PizzaRepositoryTest {

    static List<Pizza> pizzaList;
    @Autowired
    PizzaRepository pizzaRepository;

    @BeforeAll
    static void setup() {

        pizzaList =
                List.of(
                        Pizza.builder()
                                .name("Margherita")
                                .description("'La classica con pomodoro, mozzarella e basilico'")
                                .build(),
                        Pizza.builder().name("Marinara").description("'Pomodoro, aglio e origano'").build());
    }

    @Test
    void givenAnExistingPizzaShouldBeFoundAndReturnSuccessfully() {

        pizzaRepository.saveAll(pizzaList);
        var actual = pizzaRepository.findByNameIn(List.of("Margherita"));
        assertThat(actual).isNotEmpty().hasSize(1);
    }

    @Test
    void givenAnNonExistingPizzaShouldBeFoundAndReturnEmpty() {
        var actual = pizzaRepository.findByNameIn(List.of("Gourmet"));
        assertThat(actual).isEmpty();
    }
}
