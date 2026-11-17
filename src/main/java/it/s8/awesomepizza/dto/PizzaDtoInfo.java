package it.s8.awesomepizza.dto;

import it.s8.awesomepizza.entity.Ingredient;
import java.util.List;
import org.openapitools.model.PizzaDto;

public record PizzaDtoInfo(PizzaDto pizzaDto, List<Ingredient> ingredients) {}
