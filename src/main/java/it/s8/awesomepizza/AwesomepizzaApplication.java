package it.s8.awesomepizza;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableRabbit
@SpringBootApplication
public class AwesomepizzaApplication {

    static void main(String[] args) {
        SpringApplication.run(AwesomepizzaApplication.class, args);
    }
}
