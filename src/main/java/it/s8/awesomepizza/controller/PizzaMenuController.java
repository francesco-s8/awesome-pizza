package it.s8.awesomepizza.controller;

import it.s8.awesomepizza.exception.InvalidInputException;
import it.s8.awesomepizza.mapper.PizzaListToPizzaDtoListMapper;
import it.s8.awesomepizza.mapper.PizzaToPizzaDtoMapper;
import it.s8.awesomepizza.service.PizzaServiceFacade;
import it.s8.awesomepizza.utils.PizzaInfoValidator;
import java.net.URI;
import lombok.extern.slf4j.Slf4j;
import org.openapitools.model.PizzaBaseDto;
import org.openapitools.model.PizzaDto;
import org.openapitools.model.PizzaMenu;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class PizzaMenuController implements MenuApi {

  private final PizzaServiceFacade pizzaServiceFacade;
  private final PizzaListToPizzaDtoListMapper pizzaListToPizzaDtoListMapper;
  private final PizzaToPizzaDtoMapper pizzaToPizzaDtoMapper;

  public PizzaMenuController(
      PizzaServiceFacade pizzaServiceFacade,
      PizzaListToPizzaDtoListMapper pizzaListToPizzaDtoListMapper,
      PizzaToPizzaDtoMapper pizzaToPizzaDtoMapper) {
    this.pizzaServiceFacade = pizzaServiceFacade;

    this.pizzaListToPizzaDtoListMapper = pizzaListToPizzaDtoListMapper;
    this.pizzaToPizzaDtoMapper = pizzaToPizzaDtoMapper;
  }

  @Override
  public ResponseEntity<PizzaDto> _addPizzaToMenu(PizzaDto pizzaDto) {
    var isValid = PizzaInfoValidator.isValidPizzaInfo(pizzaDto);
    if (isValid) {
      var newPizza = pizzaServiceFacade.addNewPizza(pizzaDto);
      return ResponseEntity.created(URI.create(PATH_ADD_PIZZA_TO_MENU + newPizza.getId()))
          .body(pizzaToPizzaDtoMapper.apply(newPizza));
    } else {
      throw new InvalidInputException("Pizza info are invalid " + pizzaDto);
    }
  }

  @Override
  public ResponseEntity<Void> _disablePizzaAvailability(Long pizzaId) {

    pizzaServiceFacade.disablePizza(pizzaId);
    return ResponseEntity.noContent().build();
  }

  @Override
  public ResponseEntity<PizzaMenu> _getMenu() {
    var pizzas = pizzaServiceFacade.pizzaMenu();
    return ResponseEntity.ok(
        PizzaMenu.builder().menu(pizzaListToPizzaDtoListMapper.apply(pizzas)).build());
  }

  @Override
  public ResponseEntity<PizzaDto> _pizzaDetails(Long pizzaId) {
    var mapped = pizzaToPizzaDtoMapper.apply(pizzaServiceFacade.pizzaDetails(pizzaId));
    return ResponseEntity.ok(mapped);
  }

  @Override
  public ResponseEntity<PizzaDto> _updatePizzaInMenu(Long pizzaId, PizzaBaseDto pizzaBaseDto) {
    var isPriceValid = PizzaInfoValidator.isPizzaPriceValid(pizzaBaseDto.getPrice());
    if (isPriceValid) {
      var updatedPizza = pizzaServiceFacade.updatePizza(pizzaId, pizzaBaseDto);
      var response=pizzaToPizzaDtoMapper.apply(updatedPizza);
      return ResponseEntity.ok(response);
    } else {
      throw new InvalidInputException("Pizza info are invalid " + pizzaBaseDto);
    }
  }
}
