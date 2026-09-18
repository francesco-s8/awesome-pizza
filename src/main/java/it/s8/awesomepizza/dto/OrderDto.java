package it.s8.awesomepizza.dto;

public record OrderDto(long orderId) {

  @Override
  public long orderId() {
    if (orderId < 0) {
      throw new IllegalArgumentException("Param orderId must be positive");
    }
    return orderId;
  }
}
