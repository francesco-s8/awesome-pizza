package it.s8.awesomepizza.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tools.jackson.databind.json.JsonMapper;

@EnableRabbit
@Configuration
public class RabbitConfig {

  private static final String DEAD_LETTER_QUEUE = "dl";
  private static final String ORDERS_QUEUE = "orders";

  @Bean
  public Queue orders() {
    return QueueBuilder.durable(ORDERS_QUEUE)
        .quorum()
        .deadLetterExchange("**")
        .deadLetterRoutingKey(DEAD_LETTER_QUEUE)
        .build();
  }

  @Bean
  public MessageConverter jsonMessageConverter() {
    return new JacksonJsonMessageConverter(new JsonMapper());
  }

  @Bean
  public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
    var rabbitTemplate = new RabbitTemplate(connectionFactory);
    rabbitTemplate.setMessageConverter(jsonMessageConverter());
    return rabbitTemplate;
  }

  @Bean
  Queue deadLetterQueue() {
    return QueueBuilder.durable(DEAD_LETTER_QUEUE).quorum().build();
  }
}
