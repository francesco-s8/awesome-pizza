package it.s8.awesomepizza.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import it.s8.awesomepizza.exception.PizzaOrderAlreadyDeliveredException;
import it.s8.awesomepizza.service.impl.PizzaOrderFacadeImpl;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@WebMvcTest(controllers = PizzaOrderController.class)
class PizzaOrderControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockitoBean PizzaOrderFacadeImpl pizzaOrderFacade;

  @Test
  void givenAValidOrderIdShouldReturnTheCurrentOrderStatus() throws Exception {

    when(pizzaOrderFacade.retrieveOrderStatus(anyLong())).thenReturn("IN_PROCESS");

    var actual =
        mockMvc
            .perform(
                MockMvcRequestBuilders.get("/api/awesome-pizza/order/1")
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn();
    assertThat(actual.getResponse().getContentAsString()).isEqualTo("IN_PROCESS");
  }

  @Test
  void givenAnInvalidOrderIdShouldThrowAPizzaOrderAlreadyDeliveredException() throws Exception {

    when(pizzaOrderFacade.retrieveOrderStatus(anyLong()))
        .thenThrow(new PizzaOrderAlreadyDeliveredException("Order 1 is already delivered"));

    var actual =
        mockMvc
            .perform(
                MockMvcRequestBuilders.get("/api/awesome-pizza/order/1")
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn();
    assertThat(actual.getResponse().getContentAsString()).isEqualTo("Order 1 is already delivered");
  }

  @Test
  void givenAnInvalidOrderIdShouldThrowAnEntityNotFoundException() throws Exception {

    when(pizzaOrderFacade.retrieveOrderStatus(20L))
        .thenThrow(new EntityNotFoundException("Order 20 not found"));
    mockMvc
        .perform(
            MockMvcRequestBuilders.get("/api/awesome-pizza/order/20")
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound());
  }
}
