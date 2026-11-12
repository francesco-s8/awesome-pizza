package it.s8.awesomepizza.repository;

import it.s8.awesomepizza.entity.Pizza;
import java.util.List;
import java.util.Optional;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PizzaRepository extends JpaRepository<Pizza, Long> {

  List<Pizza> findByNameIn(List<String> names);

  @Override
  @EntityGraph(attributePaths = "ingredients")
  @NonNull Optional<Pizza> findById(@NonNull Long id);

  @EntityGraph(attributePaths = "ingredients")
  @Query("select p from Pizza p where p.isAvailable = true")
  List<Pizza> findByIsAvailableTrue();
}
