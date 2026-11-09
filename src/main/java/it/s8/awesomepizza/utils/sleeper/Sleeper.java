package it.s8.awesomepizza.utils.sleeper;



public interface Sleeper {
  default void sleep(long millis) throws InterruptedException {
    Thread.sleep(millis);
  }
}
