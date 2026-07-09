package it.s8.awesomepizza.exception;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.dao.DataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ExceptionManager {

  @ExceptionHandler(DataAccessException.class)
  public ResponseEntity<Void> handleDataAccessException(DataAccessException ex) {

    log.error("DataAccessException occurred ", ex);
    return ResponseEntity.internalServerError().build();
  }

  @ExceptionHandler(AwesomePizzaException.class)
  public ResponseEntity<String> handlePizzaOrderException(AwesomePizzaException ex) {

    log.error("AwesomePizzaException occurred ", ex);
    return ResponseEntity.internalServerError().body(ex.getMessage());
  }

  @ExceptionHandler(AmqpException.class)
  public ResponseEntity<Void> handleAmqpException(AmqpException ex) {

    log.error("AmqpException occurred ", ex);
    return ResponseEntity.internalServerError().build();
  }

  @ExceptionHandler(EntityNotFoundException.class)
  public ResponseEntity<Void> orderNotFound(EntityNotFoundException entityNotFoundException) {

    log.warn("EntityNotFoundException occurred ", entityNotFoundException);
    return ResponseEntity.notFound().build();
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<String> badInputRequest(MethodArgumentNotValidException ex) {
    log.warn("Bad input request ", ex);
    ex.getBindingResult().getAllErrors().forEach(error -> log.warn("Validation error: {}", error));
    return ResponseEntity.badRequest().body("Check the request body, some fields are not valid");
  }
}
