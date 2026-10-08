package it.s8.awesomepizza.controller;

import it.s8.awesomepizza.mapper.PizzaListToPizzaDtoListMapper;
import it.s8.awesomepizza.mapper.PizzaToPizzaDto;
import it.s8.awesomepizza.service.PizzaService;
import it.s8.awesomepizza.validator.PizzaInfoValidator;
import java.net.URI;
import lombok.extern.slf4j.Slf4j;
import org.openapitools.model.PizzaDto;
import org.openapitools.model.PizzaMenu;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class PizzaMenuController implements MenuApi {

  private final PizzaService pizzaService;
  private final PizzaInfoValidator pizzaInfoValidator;
  private final PizzaListToPizzaDtoListMapper pizzaListToPizzaDtoListMapper;
  private final PizzaToPizzaDto pizzaToPizzaDto;

  public PizzaMenuController(
      PizzaService pizzaService,
      PizzaInfoValidator pizzaInfoValidator,
      PizzaListToPizzaDtoListMapper pizzaListToPizzaDtoListMapper,
      PizzaToPizzaDto pizzaToPizzaDto) {
    this.pizzaService = pizzaService;
    this.pizzaInfoValidator = pizzaInfoValidator;
    this.pizzaListToPizzaDtoListMapper = pizzaListToPizzaDtoListMapper;
    this.pizzaToPizzaDto = pizzaToPizzaDto;
  }

  @Override
  public ResponseEntity<PizzaDto> _addPizzaToMenu(PizzaDto pizzaDto) {
    var newPizza = pizzaService.addPizza(pizzaDto);
    return ResponseEntity.created(URI.create("/menu/" + newPizza.getId()))
        .body(
            PizzaDto.builder()
                .price(newPizza.getPrice())
                .description(newPizza.getDescription())
                .name(newPizza.getName())
                .build());
  }

  @Override
  public ResponseEntity<PizzaMenu> _getMenu() {
    var pizzas = pizzaService.getAvailablePizzas();
    return ResponseEntity.ok(
        PizzaMenu.builder().menu(pizzaListToPizzaDtoListMapper.apply(pizzas)).build());
  }

  @Override
  public ResponseEntity<PizzaDto> _pizzaDetails(Long pizzaId) {
    return ResponseEntity.ok(pizzaToPizzaDto.apply(pizzaService.getPizzaDetails(pizzaId)));
  }

  @Override
  public ResponseEntity<PizzaDto> _updatePizzaInMenu(Long pizzaId, PizzaDto pizzaDto) {
    if (pizzaInfoValidator.isValid(pizzaDto)) {
      log.info("Updating pizza with id {} and info {}", pizzaId, pizzaDto);
    }
    var updatedPizza = pizzaService.updatePizza(pizzaId, pizzaDto);
    return ResponseEntity.ok(
        PizzaDto.builder()
            .price(updatedPizza.getPrice())
            .description(updatedPizza.getDescription())
            .name(updatedPizza.getName())
            .build());
  }
}
