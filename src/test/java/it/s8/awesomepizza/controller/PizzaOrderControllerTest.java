package it.s8.awesomepizza.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.json.JsonMapper;
import it.s8.awesomepizza.dto.OrderDto;
import it.s8.awesomepizza.exception.AwesomePizzaException;
import it.s8.awesomepizza.service.impl.PizzaOrderFacadeImpl;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.openapitools.model.PizzaOrderRequest;
import org.openapitools.model.PizzaOrderResponse;
import org.openapitools.model.PizzaOrderStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@Slf4j
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

  @Test
  void givenAValidOrderRequestShouldCreateNewOrder() throws Exception {
    var orderRequest =
        PizzaOrderRequest.builder().customerName("John Doe").pizzas(List.of("Margherita")).build();

    when(pizzaOrderFacade.processOrder(any(PizzaOrderRequest.class))).thenReturn(new OrderDto(1L));

    var actual =
        mockMvc
            .perform(
                MockMvcRequestBuilders.post("/api/awesome-pizza/orders")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(jsonMapper.writeValueAsString(orderRequest)))
            .andExpect(status().isOk())
            .andReturn();

    assertThat(actual.getResponse().getContentAsString())
        .isEqualTo(jsonMapper.writeValueAsString(PizzaOrderResponse.builder().orderId(1L).build()));
  }

  @Test
  void whenProcessOrderThrowsAwesomePizzaExceptionShouldReturn500() throws Exception {
    var orderRequest =
        PizzaOrderRequest.builder().customerName("Jane Doe").pizzas(List.of("Pepperoni")).build();

    when(pizzaOrderFacade.processOrder(any(PizzaOrderRequest.class)))
        .thenThrow(new AwesomePizzaException("Some pizzas not found"));

    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/api/awesome-pizza/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMapper.writeValueAsString(orderRequest)))
        .andExpect(status().isInternalServerError());
  }

  @Test
  void whenProcessOrderThrowsEntityNotFoundExceptionShouldReturn404() throws Exception {
    var orderRequest =
        PizzaOrderRequest.builder()
            .customerName("Bob Smith")
            .pizzas(List.of("Margherita", "Quattro Formaggi"))
            .build();

    when(pizzaOrderFacade.processOrder(any(PizzaOrderRequest.class)))
        .thenThrow(new EntityNotFoundException("Pizza order entity not found"));

    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/api/awesome-pizza/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMapper.writeValueAsString(orderRequest)))
        .andExpect(status().isNotFound());
  }

  @Test
  void whenCustomerNameIsEmptyShouldReturn400() throws Exception {
    var orderRequest =
        PizzaOrderRequest.builder().customerName("").pizzas(List.of("Margherita")).build();

    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/api/awesome-pizza/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMapper.writeValueAsString(orderRequest)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void whenCustomerNameIsMissingShouldReturn400() throws Exception {
    var orderRequest = "{\"pizzas\": [\"Margherita\"]}";

    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/api/awesome-pizza/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(orderRequest))
        .andExpect(status().isBadRequest());
  }

  @Test
  void whenPizzasArrayIsEmptyShouldReturn400() throws Exception {
    var orderRequest =
        PizzaOrderRequest.builder().customerName("John Doe").pizzas(List.of()).build();

    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/api/awesome-pizza/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMapper.writeValueAsString(orderRequest)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void whenPizzasArrayIsMissingShouldReturn400() throws Exception {
    var orderRequest = "{\"customerName\": \"John Doe\"}";

    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/api/awesome-pizza/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(orderRequest))
        .andExpect(status().isBadRequest());
  }

  @Test
  void whenOrderIdIsInvalidFormatShouldReturn400() throws Exception {
    mockMvc
        .perform(
            MockMvcRequestBuilders.get("/api/awesome-pizza/orders/invalid-id")
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest());
  }
}
