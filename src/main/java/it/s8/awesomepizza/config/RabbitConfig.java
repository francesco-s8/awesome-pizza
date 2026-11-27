package it.s8.awesomepizza.config;

import it.s8.awesomepizza.utils.RabbitConstants;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.rabbitmq.autoconfigure.SimpleRabbitListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tools.jackson.databind.json.JsonMapper;

@EnableRabbit
@Configuration
public class RabbitConfig {

  @Bean
  Queue orders() {
    return QueueBuilder.durable(RabbitConstants.ORDERS_QUEUE)
        .quorum()
        .deadLetterExchange(RabbitConstants.DEAD_LETTER_EXCHANGE)
        .deadLetterRoutingKey(RabbitConstants.DEAD_LETTER_QUEUE)
        .build();
  }

  @Bean
  MessageConverter jsonMessageConverter() {
    return new JacksonJsonMessageConverter(new JsonMapper());
  }

  @Bean
  RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
    var rabbitTemplate = new RabbitTemplate(connectionFactory);
    rabbitTemplate.setMessageConverter(jsonMessageConverter());
    return rabbitTemplate;
  }

  @Bean
  Queue deadLetterQueue() {
    return QueueBuilder.durable(RabbitConstants.DEAD_LETTER_QUEUE).quorum().build();
  }

  @Bean
  DirectExchange ordersExchange() {
    return new DirectExchange(RabbitConstants.ORDERS_EXCHANGE);
  }

  @Bean
  DirectExchange deadLetterExchange() {
    return new DirectExchange(RabbitConstants.DEAD_LETTER_EXCHANGE);
  }

  @Bean
  Binding ordersBinding(Queue orders, DirectExchange ordersExchange) {
    return BindingBuilder.bind(orders).to(ordersExchange).with(RabbitConstants.ORDERS_QUEUE);
  }

  @Bean
  Binding deadLetterBinding(Queue deadLetterQueue, DirectExchange deadLetterExchange) {
    return BindingBuilder.bind(deadLetterQueue)
        .to(deadLetterExchange)
        .with(RabbitConstants.DEAD_LETTER_QUEUE);
  }

  @Bean(destroyMethod = "close")
  ExecutorService pizzaOrderListenerVirtualThreadExecutor() {
    return Executors.newVirtualThreadPerTaskExecutor();
  }

  @Bean
  SimpleRabbitListenerContainerFactory pizzaOrderListenerContainerFactory(
      SimpleRabbitListenerContainerFactoryConfigurer configurer,
      ConnectionFactory connectionFactory,
      ExecutorService pizzaOrderListenerVirtualThreadExecutor) {
    var factory = new SimpleRabbitListenerContainerFactory();
    factory.setAcknowledgeMode(AcknowledgeMode.MANUAL);
    configurer.configure(factory, connectionFactory);
    factory.setTaskExecutor(pizzaOrderListenerVirtualThreadExecutor);
    return factory;
  }
}
