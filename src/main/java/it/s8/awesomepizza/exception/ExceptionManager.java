package it.s8.awesomepizza.exception;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ExceptionManager {

  @ExceptionHandler(DataAccessException.class)
  public ResponseEntity<ProblemDetail> handleDataAccessException(DataAccessException ex) {

    log.error("DataAccessException occurred ", ex);
    return exceptionResponseObject(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected data access error occurred");
  }

  @ExceptionHandler(AwesomePizzaException.class)
  public ResponseEntity<ProblemDetail> handlePizzaOrderException(AwesomePizzaException ex) {

    log.error("AwesomePizzaException occurred ", ex);
    return exceptionResponseObject(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
  }

  @ExceptionHandler(AmqpException.class)
  public ResponseEntity<ProblemDetail> handleAmqpException(AmqpException ex) {

    log.error("AmqpException occurred ", ex);
    return exceptionResponseObject(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected messaging error occurred");
  }

  @ExceptionHandler(EntityNotFoundException.class)
  public ResponseEntity<ProblemDetail> orderNotFound(
      EntityNotFoundException entityNotFoundException) {

    log.warn("EntityNotFoundException occurred ", entityNotFoundException);
    return exceptionResponseObject(HttpStatus.NOT_FOUND, entityNotFoundException.getMessage());
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ProblemDetail> badInputRequest(MethodArgumentNotValidException ex) {
    log.warn("Bad input request ", ex);
    ex.getBindingResult().getAllErrors().forEach(error -> log.warn("Validation error: {}", error));
    return exceptionResponseObject(
        HttpStatus.BAD_REQUEST, "Check the request body, some fields are not valid");
  }

  @ExceptionHandler(OrderNotReadyToBePaidException.class)
  public ResponseEntity<ProblemDetail> handleOrderNotReadyException(
      OrderNotReadyToBePaidException ex) {
    log.warn("OrderNotReadyToBePaidException occurred ", ex);
    return exceptionResponseObject(HttpStatus.UNPROCESSABLE_CONTENT, ex.getMessage());
  }

  @ExceptionHandler(InvalidInputException.class)
  public ResponseEntity<ProblemDetail> invalidInput(InvalidInputException invalidInputException) {
    log.error("Input not valid due to", invalidInputException);
    return exceptionResponseObject(HttpStatus.BAD_REQUEST, invalidInputException.getMessage());
  }

  @ExceptionHandler(AwesomePizzaOperationNotSupportedException.class)
  public ResponseEntity<ProblemDetail> operationNotSupported(
      AwesomePizzaOperationNotSupportedException awesomePizzaOperationNotSupportedException) {
    log.warn(
        "Operation not possible due to {}",
        awesomePizzaOperationNotSupportedException.getMessage());
    return exceptionResponseObject(
        HttpStatus.UNPROCESSABLE_CONTENT,
        awesomePizzaOperationNotSupportedException.getMessage());
  }

  @ExceptionHandler(NullPointerException.class)
  public ResponseEntity<ProblemDetail> npe(NullPointerException nullPointerException) {
    log.error("An NPE occurred, investigate ", nullPointerException);
    return exceptionResponseObject(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected server error occurred");
  }

  private ResponseEntity<ProblemDetail> exceptionResponseObject(HttpStatus status, String detail) {
    return ResponseEntity.status(status).body(ProblemDetail.forStatusAndDetail(status, detail));
  }
}
