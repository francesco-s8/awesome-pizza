package it.s8.awesomepizza.repository;

import static org.assertj.core.api.Assertions.assertThat;

import it.s8.awesomepizza.config.JpaConfig;
import it.s8.awesomepizza.helper.TestHelper;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;

@DataJpaTest
@Import(JpaConfig.class)
class IngredientRepositoryTest {

  @Autowired IngredientRepository ingredientRepository;

  @BeforeEach
  void initData() {

    var ingredients = TestHelper.getIngredientListStub();
    ingredientRepository.saveAll(ingredients);
  }

  @AfterEach
  void restoreData() {
    ingredientRepository.deleteAll();
  }

  @Test
  void givenAlistOfIngredientsShouldBeFound() {
    var toBeFound = List.of("Olives", "Basil");
    var foundIngredients = ingredientRepository.findByNameIn(toBeFound);
    assertThat(foundIngredients).hasSize(2);
  }

  @Test
  void findAvailableIngredientsByName() {
    var basil = ingredientRepository.findByName("Basil").orElseThrow();
    basil.setIsAvailable(false);
    ingredientRepository.save(basil);

    var found = ingredientRepository.findByNameInAndIsAvailableTrue(
        List.of("Tomato", "Basil", "Cinnamon"));

    assertThat(found).extracting("name").containsExactly("Tomato");
  }

  @Test
  void givenANonInListIngredientShouldBeReturnEmptyList() {
    assertThat(ingredientRepository.findByName("Cinnamon")).isEmpty();
  }

  @Test
  void retrieveAllIngredientsShouldBeSameSizeOfTheDbData() {

    assertThat(ingredientRepository.findByIsAvailableTrue())
        .hasSize(TestHelper.getIngredientListStub().size());
  }

  @Test
  void findIngredientByNameAndIfItAvailable() {

    assertThat(ingredientRepository.findByNameAndIsAvailableTrue("Basil"))
        .isNotEmpty()
        .get()
        .extracting("name")
        .isEqualTo("Basil");
  }

  @Test
  void findIngredientByNameAndIfItAvailableFalseShouldBeEmpty() {
    var found = ingredientRepository.findByName("Basil");
    assertThat(found).isNotEmpty();
    found.get().setIsAvailable(Boolean.FALSE);
    ingredientRepository.save(found.get());
    assertThat(ingredientRepository.findByNameAndIsAvailableTrue("Basil")).isEmpty();
  }
}
