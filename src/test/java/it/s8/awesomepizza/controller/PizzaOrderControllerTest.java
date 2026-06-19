package it.s8.awesomepizza.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.json.JsonMapper;
import it.s8.awesomepizza.service.impl.PizzaOrderFacadeImpl;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.openapitools.model.PizzaOrderStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@WebMvcTest(controllers = PizzaOrderController.class)
class PizzaOrderControllerTest {

  @Autowired private MockMvc mockMvc;

  private final JsonMapper jsonMapper = new JsonMapper();

  @MockitoBean PizzaOrderFacadeImpl pizzaOrderFacade;

  @Test
  void givenAValidOrderIdShouldReturnTheCurrentOrderStatus() throws Exception {

    when(pizzaOrderFacade.retrieveOrderStatus(anyLong()))
        .thenReturn(PizzaOrderStatus.OrderStatusEnum.IN_PROCESS.getValue());

    var actual =
        mockMvc
            .perform(
                MockMvcRequestBuilders.get("/api/awesome-pizza/orders/1")
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn();
    assertThat(actual.getResponse().getContentAsString())
        .isEqualTo(
            jsonMapper.writeValueAsString(
                PizzaOrderStatus.builder()
                    .orderStatus(PizzaOrderStatus.OrderStatusEnum.IN_PROCESS)
                    .build()));
  }

  @Test
  void givenAnInvalidOrderIdShouldThrowAPizzaOrderAlreadyDeliveredException() throws Exception {

    when(pizzaOrderFacade.retrieveOrderStatus(anyLong()))
        .thenReturn(PizzaOrderStatus.OrderStatusEnum.ALREADY_DELIVERED.getValue());

    var actual =
        mockMvc
            .perform(
                MockMvcRequestBuilders.get("/api/awesome-pizza/orders/10")
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn();
    assertThat(actual.getResponse().getContentAsString())
        .isEqualTo(
            jsonMapper.writeValueAsString(
                PizzaOrderStatus.builder()
                    .orderStatus(PizzaOrderStatus.OrderStatusEnum.ALREADY_DELIVERED)
                    .build()));
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
