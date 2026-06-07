package it.s8.awesomepizza.service.impl;

import it.s8.awesomepizza.entity.Pizza;
import it.s8.awesomepizza.exception.PizzaOrderException;
import it.s8.awesomepizza.repository.PizzaRepository;
import it.s8.awesomepizza.service.PizzaService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PizzaServiceImpl implements PizzaService {

    private final PizzaRepository pizzaRepository;

    public PizzaServiceImpl(PizzaRepository pizzaRepository) {
        this.pizzaRepository = pizzaRepository;
    }

    @Override
    public List<Pizza> getPizzas(List<String> pizzaNames) {
        var pizzas = pizzaRepository.findByNameIn(pizzaNames);
        if (pizzas.size() != pizzaNames.size()) {
            throw new PizzaOrderException("Some pizzas not found");
        }
        return pizzas;
    }
}
