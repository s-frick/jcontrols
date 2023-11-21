package io.github.sfrick.jcontrols;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.OutputStream;

import org.junit.jupiter.api.Test;

public class TryExampleTest {


  private InputStream input() {
    return this.getClass().getResourceAsStream("heroes.json");
  } 

  private OutputStream outputThrows() throws FileNotFoundException {
    throw new FileNotFoundException("heroes_out.json");
  } 

  private void transformHeroes(InputStream i, OutputStream o) {
    // doStuff
  }

  private Hero findHeroByName(InputStream i, String name) throws Throwable {
    return new Hero(name);
  }

  private Try<Human> tryTransformHulk(Hero hulk) {
    return Try.of(() -> transformHulk(hulk));
  }

  private Human transformHulk(Hero hulk) throws Throwable {
    return new Human(hulk.name());
  }

  @Test
  void tryWith2ResourcesAndConsumer() {
    Try<Void> actual = Try
      .withResource(this::input)
      .withResource(this::outputThrows)
      .ofConsumer(this::transformHeroes);

    Try
      .withResource(this::input)
      .of((r) -> findHeroByName(r, "Hulk"))
      .flatMap(this::tryTransformHulk);

    assertThat(actual.isFailure()).isTrue();
    assertThat(actual).isInstanceOf(Try.Failure.class);
    assertThat(( (Try.Failure<Void>)actual ).cause()).isInstanceOf(FileNotFoundException.class);
  }
  
  record Hero(String name) {

  }
  record Human(String name) {

  }
}
