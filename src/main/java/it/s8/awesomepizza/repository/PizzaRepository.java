package it.s8.awesomepizza.repository;

import it.s8.awesomepizza.entity.Pizza;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PizzaRepository extends JpaRepository<Pizza, Long> {

    List<Pizza> findByNameIn(List<String> names);
}
