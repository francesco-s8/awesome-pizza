package it.s8.awesomepizza.event;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEvent;

@Getter
@Slf4j
public class OrderCreatedEvent extends ApplicationEvent {

  private final Long orderId;

  public OrderCreatedEvent(Object source, Long orderId) {
    log.info("OrderCreatedEvent created for orderId: {}", orderId);
    super(source);
    this.orderId = orderId;
  }

}
