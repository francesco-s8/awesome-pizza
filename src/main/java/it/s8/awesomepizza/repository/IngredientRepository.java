package it.s8.awesomepizza.repository;

import it.s8.awesomepizza.entity.Ingredient;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface IngredientRepository extends JpaRepository<Ingredient, Long> {

  List<Ingredient> findByNameIn(List<String> names);

  List<Ingredient> findByNameInAndIsAvailableTrue(List<String> names);

  Optional<Ingredient> findByName(String name);

  List<Ingredient> findByIsAvailableTrue();

  @Query("select i from Ingredient i where i.name = ?1 and i.isAvailable = true")
  Optional<Ingredient> findByNameAndIsAvailableTrue(String name);
}
