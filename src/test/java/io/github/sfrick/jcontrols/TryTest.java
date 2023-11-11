package io.github.sfrick.jcontrols;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import io.github.sfrick.jcontrols.Try.Failure;
import io.github.sfrick.jcontrols.Try.Success;

public class TryTest {
 
  @Test
  void shouldReturnFailure() {
    var ex = new RuntimeException("failure");
    Try<String> actual = Try.failure(ex);

    assertThat(actual).isInstanceOf(Failure.class);
    assertThat(((Failure<String>)actual).cause()).isEqualTo(ex);
  }

  @Test
  void shouldReturnSuccess() {
    Try<String> actual = Try.success("success");

    assertThat(actual).isInstanceOf(Success.class);
    assertThat(((Success<String>)actual).value()).isEqualTo("success");
  }

  @Test
  void shouldMapAPresentSuccessValue() {
    Try<String> origin = Try.success("success");

    Try<String> actual = origin.map(String::toUpperCase);

    assertThat(actual.orElse("FAILURE")).isEqualTo("SUCCESS");
  }

  @Test
  void shouldNotMapAFailureValue() {
    var ex = new RuntimeException("failure");
    Try<String> origin = Try.failure(ex);

    Try<String> actual = origin.map(String::toUpperCase);

    assertThat(actual.orElse("FAILURE")).isEqualTo("FAILURE");
  }


  Try<String> toSplit(String s) {
    var ex = new RuntimeException("failure");
    String[] split = s.split("&");
    return split.length > 1 ? Try.success(split[1]) : Try.failure(ex);
  }

  @Test
  void shouldFlatMapAPresentSuccessValue() {
    Try<String> origin = Try.success("suc&cess");

    Try<String> actual = origin.flatMap(this::toSplit);

    assertThat(actual.orElse("FAILURE")).isEqualTo("cess");
  }

  @Test
  void shouldNotFlatMapAFailureValue() {
    var ex = new RuntimeException("failure");
    Try<String> origin = Try.failure(ex);

    Try<String> actual = origin.flatMap(this::toSplit);

    assertThat(actual.orElse("FAILURE")).isEqualTo("FAILURE");
  }

  @Test
  void shouldReturnOtherTry() {
    var ex = new RuntimeException("fai&lure");
    Try<String> origin = Try.failure(ex);
    Try<String> anotherTry = Try.success("success");

    assertThat(origin.or(anotherTry).orElse("default")).isEqualTo("success");
  }
}
