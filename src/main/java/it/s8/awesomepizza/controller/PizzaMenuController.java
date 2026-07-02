package it.s8.awesomepizza.controller;

import it.s8.awesomepizza.mapper.PizzaListToPizzaDtoListMapper;
import it.s8.awesomepizza.service.PizzaService;
import lombok.extern.slf4j.Slf4j;
import org.openapitools.model.PizzaMenu;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class PizzaMenuController implements MenuApi {

  private final PizzaService pizzaService;

  private final PizzaListToPizzaDtoListMapper pizzaListToPizzaDtoListMapper;

  public PizzaMenuController(
      PizzaService pizzaService, PizzaListToPizzaDtoListMapper pizzaListToPizzaDtoListMapper) {
    this.pizzaService = pizzaService;
    this.pizzaListToPizzaDtoListMapper = pizzaListToPizzaDtoListMapper;
  }

  @Override
  public ResponseEntity<PizzaMenu> _getMenu() {
    var pizzas = pizzaService.getAvailablePizzas();
    return ResponseEntity.ok(
        PizzaMenu.builder().menu(pizzaListToPizzaDtoListMapper.apply(pizzas)).build());
  }
}
