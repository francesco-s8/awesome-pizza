package it.s8.awesomepizza.utils;

import lombok.experimental.UtilityClass;

@UtilityClass
public class RabbitConstants {
  public static final String DEAD_LETTER_QUEUE = "dl";
  public static final String ORDERS_QUEUE = "orders";
  public static final String ORDER_EVENTS_STREAM = "order-events";
  public static final String DEAD_LETTER_EXCHANGE = "dead-letter-exchange";
  public static final String ORDERS_EXCHANGE = "orders-exchange";
  public static final String ORDER_EVENTS_EXCHANGE = "order-events-exchange";
}
