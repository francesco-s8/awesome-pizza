package it.s8.awesomepizza.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.json.JsonMapper;
import it.s8.awesomepizza.exception.AwesomePizzaOperationNotSupportedException;
import it.s8.awesomepizza.exception.OrderNotReadyToBePaidException;
import it.s8.awesomepizza.service.PizzaOrderPaymentFacade;
import jakarta.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.openapitools.model.PizzaOrderPaymentInfo;
import org.openapitools.model.PizzaOrderStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@Slf4j
@WebMvcTest(controllers = PizzaOrderPaymentController.class)
class PizzaOrderPaymentControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockitoBean PizzaOrderPaymentFacade pizzaOrderPaymentFacade;

  private final JsonMapper jsonMapper = new JsonMapper();

  private static PizzaOrderPaymentInfo getPaymentInfo() {
    return PizzaOrderPaymentInfo.builder()
        .total(BigDecimal.ONE)
        .paymentMethod(PizzaOrderPaymentInfo.PaymentMethodEnum.CREDIT_CARD)
        .build();
  }

  @Test
  void processPayment() throws Exception {

    var pizzaOrderPaid = getPaymentInfo();
    doNothing().when(pizzaOrderPaymentFacade).processPayment(1L, pizzaOrderPaid);

    mockMvc
        .perform(
            MockMvcRequestBuilders.put("/api/awesome-pizza/orders/1/payment")
                .content(jsonMapper.writeValueAsString(pizzaOrderPaid))
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNoContent())
        .andReturn();
  }

  @Test
  void invalidOrder() throws Exception {

    var paymentInfo = getPaymentInfo();

    doThrow(
            new OrderNotReadyToBePaidException(
                "Order 1 is not ready to be paid, cannot process payment"))
        .when(pizzaOrderPaymentFacade)
        .processPayment(1L, paymentInfo);

    mockMvc
        .perform(
            MockMvcRequestBuilders.put("/api/awesome-pizza/orders/1/payment")
                .content(jsonMapper.writeValueAsString(paymentInfo))
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isUnprocessableContent())
        .andReturn();
  }

  @Test
  void _logicalDeletePayment() throws Exception {

    doThrow(
            new AwesomePizzaOperationNotSupportedException(
                "Order "
                    + 1
                    + " cannot be cancelled its status is "
                    + PizzaOrderStatus.OrderStatusEnum.PAID_AND_DELIVERED.getValue()))
        .when(pizzaOrderPaymentFacade)
        .deletePayment(anyLong());

    mockMvc
        .perform(
            MockMvcRequestBuilders.delete("/api/awesome-pizza/orders/1/payment")
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isUnprocessableContent())
        .andReturn();
  }

  @Test
  void _logicalDeletePaymentException() throws Exception {

    doThrow(new EntityNotFoundException("Order not found with ID: " + 1))
        .when(pizzaOrderPaymentFacade)
        .deletePayment(anyLong());

    mockMvc
        .perform(
            MockMvcRequestBuilders.delete("/api/awesome-pizza/orders/1/payment")
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound())
        .andReturn();
  }

  @Test
  void logicalDeleteOK() throws Exception {
    doNothing().when(pizzaOrderPaymentFacade).deletePayment(anyLong());
    mockMvc
        .perform(
            MockMvcRequestBuilders.delete("/api/awesome-pizza/orders/1/payment")
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNoContent())
        .andReturn();
  }
}
