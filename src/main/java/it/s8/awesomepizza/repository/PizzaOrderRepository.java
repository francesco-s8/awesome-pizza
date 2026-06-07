package it.s8.awesomepizza.repository;

import it.s8.awesomepizza.entity.PizzaOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PizzaOrderRepository extends JpaRepository<PizzaOrder, Long> {
}
