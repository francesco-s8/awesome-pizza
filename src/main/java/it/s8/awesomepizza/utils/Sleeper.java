package it.s8.awesomepizza.utils;

@FunctionalInterface
public interface Sleeper {
  void sleep(long millis) throws InterruptedException;
}
