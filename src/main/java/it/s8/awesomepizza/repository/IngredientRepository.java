package it.s8.awesomepizza.repository;

import it.s8.awesomepizza.entity.Ingredient;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IngredientRepository extends JpaRepository<Ingredient, Long> {

  List<Ingredient> findByNameIn(List<String> names);
}
