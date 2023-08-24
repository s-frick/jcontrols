package de.sfrick.jmonad;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import de.sfrick.jmonad.Either.Failure;
import de.sfrick.jmonad.Either.Success;

public class EitherTest {
 
  @Test
  void shouldReturnFailure() {
    Either<String, Object> actual = Either.failure("failure");

    assertThat(actual).isInstanceOf(Failure.class);
    assertThat(((Failure<String, Object>)actual).value()).isEqualTo("failure");
  }

  @Test
  void shouldReturnSuccess() {
    Either<Object, String> actual = Either.success("success");

    assertThat(actual).isInstanceOf(Success.class);
    assertThat(((Success<Object, String>)actual).value()).isEqualTo("success");
  }

  @Test
  void shouldMapAPresentSuccessValue() {
    Either<Object, String> origin = Either.success("success");

    Either<Object, String> actual = origin.map(String::toUpperCase);

    assertThat(actual.getOrDefault("FAILURE")).isEqualTo("SUCCESS");
  }
  
  @Test
  void shouldNotMapAFailureValue() {
    Either<String, String> origin = Either.failure("failure");

    Either<String, String> actual = origin.map(String::toUpperCase);

    assertThat(actual.getOrDefault("FAILURE")).isEqualTo("FAILURE");
  }

  @Test
  void shouldMapAFailureValue() {
    Either<String, String> origin = Either.failure("failure");

    Either<String, String> actual = origin.mapLeft(String::toUpperCase);

    assertThat(actual).isInstanceOf(Failure.class);
    assertThat(((Failure<String, String>)actual).value()).isEqualTo("FAILURE");
  }


  Either<String, String> toSplit(String s) {
    String[] split = s.split("&");
    return split.length > 1 ? Either.success(split[1]) : Either.failure("failure");
  }

  @Test
  void shouldFlatMapAPresentSuccessValue() {
    Either<String, String> origin = Either.success("suc&cess");

    Either<String, String> actual = origin.flatMap(this::toSplit);

    assertThat(actual.getOrDefault("FAILURE")).isEqualTo("cess");
  }
  
  @Test
  void shouldNotFlatMapAFailureValue() {
    Either<String, String> origin = Either.failure("fai&lure");

    Either<String, String> actual = origin.flatMap(this::toSplit);

    assertThat(actual.getOrDefault("FAILURE")).isEqualTo("FAILURE");
  }

}
