package it.s8.awesomepizza.utils.sleeper;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;

class SleeperImplTest {

  final Sleeper sleeper = new SleeperImpl();


  @Test
  void shouldThrowInterruptedExceptionWhenInterrupted() {

    Thread.currentThread().interrupt();
    assertThatThrownBy( () -> sleeper.sleep(10L)).isExactlyInstanceOf(InterruptedException.class);
    assertThat(Thread.currentThread().isInterrupted()).isFalse();
  }
}
