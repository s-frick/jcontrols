package io.github.sfrick.jcontrols;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import io.github.sfrick.jcontrols.Try.Failure;
import io.github.sfrick.jcontrols.Try.Success;

public class TryTest {
 

  void runnable() throws Exception {
    //noOp
  }
  void runnableThrowCheckedEx() throws Exception {
    throw new Exception();
  }
  String function0ThrowCheckedEx() throws Exception {
    throw new Exception();
  }
  String function0ReturnsSuccess() throws Exception {
    return "success";
  }
  void consumer0ReturnsSuccess() throws Exception {
    // noop
  }

  class ResourceDummy implements AutoCloseable{
    private int called = 0;

    @Override
    public void close() throws Exception {
      called += 1;
    }

    public int isClosed() {
      return called;
    }
  }

  @Test
  @SuppressWarnings({ "resource" }) 
  void shouldClose1Resource() {
    var spy = new ResourceDummy();
    boolean actual = Try.withResource(() -> spy)
      .of(r -> function0ReturnsSuccess())
      .isSuccess();

    assertThat(actual).isTrue();
    assertThat(spy.isClosed()).isEqualTo(1);
  }

  @Test
  @SuppressWarnings({ "resource" }) 
  void shouldClose2Resource() {
    var spy1 = new ResourceDummy();
    var spy2 = new ResourceDummy();
    boolean actual = Try
      .withResource(() -> spy1)
      .withResource(() -> spy2)
      .ofConsumer((r1, r2) -> function0ReturnsSuccess())
      .isSuccess();

    assertThat(actual).isTrue();
    assertThat(spy1.isClosed()).isEqualTo(1);
    assertThat(spy2.isClosed()).isEqualTo(1);
  }

  @Test
  @SuppressWarnings({ "resource" }) 
  void shouldClose3Resource() {
    var spy1 = new ResourceDummy();
    var spy2 = new ResourceDummy();
    var spy3 = new ResourceDummy();
    boolean actual = Try
      .withResource(() -> spy1)
      .withResource(() -> spy2)
      .withResource(() -> spy3)
      .of((r1, r2, r3) -> function0ReturnsSuccess())
      .isSuccess();

    assertThat(actual).isTrue();
    assertThat(spy1.isClosed()).isEqualTo(1);
    assertThat(spy2.isClosed()).isEqualTo(1);
    assertThat(spy3.isClosed()).isEqualTo(1);
  }

  @Test
  @SuppressWarnings({ "resource" }) 
  void shouldClose4Resource() {
    var spy1 = new ResourceDummy();
    var spy2 = new ResourceDummy();
    var spy3 = new ResourceDummy();
    var spy4 = new ResourceDummy();
    boolean actual = Try
      .withResource(() -> spy1)
      .withResource(() -> spy2)
      .withResource(() -> spy3)
      .withResource(() -> spy4)
      .of((r1, r2, r3, r4) -> function0ReturnsSuccess())
      .isSuccess();

    assertThat(actual).isTrue();
    assertThat(spy1.isClosed()).isEqualTo(1);
    assertThat(spy2.isClosed()).isEqualTo(1);
    assertThat(spy3.isClosed()).isEqualTo(1);
    assertThat(spy4.isClosed()).isEqualTo(1);
  }

  @Test
  @SuppressWarnings({ "resource" }) 
  void shouldClose5Resource() {
    var spy1 = new ResourceDummy();
    var spy2 = new ResourceDummy();
    var spy3 = new ResourceDummy();
    var spy4 = new ResourceDummy();
    var spy5 = new ResourceDummy();
    boolean actual = Try
      .withResource(() -> spy1)
      .withResource(() -> spy2)
      .withResource(() -> spy3)
      .withResource(() -> spy4)
      .withResource(() -> spy5)
      .of((r1, r2, r3, r4, r5) -> function0ReturnsSuccess())
      .isSuccess();

    assertThat(actual).isTrue();
    assertThat(spy1.isClosed()).isEqualTo(1);
    assertThat(spy2.isClosed()).isEqualTo(1);
    assertThat(spy3.isClosed()).isEqualTo(1);
    assertThat(spy4.isClosed()).isEqualTo(1);
    assertThat(spy5.isClosed()).isEqualTo(1);
  }


  @Test
  @SuppressWarnings({ "resource" }) 
  void shouldClose2ResourceNoMatterWhat() {
    var spy1 = new ResourceDummy();
    var spy2 = new ResourceDummy();
    boolean actual = Try
      .withResource(() -> spy1)
      .withResource(() -> spy2)
      .ofConsumer((r1, r2) -> function0ThrowCheckedEx())
      .isFailure();

    assertThat(actual).isTrue();
    assertThat(spy1.isClosed()).isEqualTo(1);
    assertThat(spy2.isClosed()).isEqualTo(1);
  }

  @Test
  @SuppressWarnings({ "resource" }) 
  void shouldClose3ResourceNoMatterWhat() {
    var spy1 = new ResourceDummy();
    var spy2 = new ResourceDummy();
    var spy3 = new ResourceDummy();
    boolean actual = Try
      .withResource(() -> spy1)
      .withResource(() -> spy2)
      .withResource(() -> spy3)
      .of((r1, r2, r3) -> function0ThrowCheckedEx())
      .isFailure();

    assertThat(actual).isTrue();
    assertThat(spy1.isClosed()).isEqualTo(1);
    assertThat(spy2.isClosed()).isEqualTo(1);
    assertThat(spy3.isClosed()).isEqualTo(1);
  }

  @Test
  @SuppressWarnings({ "resource" }) 
  void shouldClose4ResourceNoMatterWhat() {
    var spy1 = new ResourceDummy();
    var spy2 = new ResourceDummy();
    var spy3 = new ResourceDummy();
    var spy4 = new ResourceDummy();
    boolean actual = Try
      .withResource(() -> spy1)
      .withResource(() -> spy2)
      .withResource(() -> spy3)
      .withResource(() -> spy4)
      .of((r1, r2, r3, r4) -> function0ThrowCheckedEx())
      .isFailure();

    assertThat(actual).isTrue();
    assertThat(spy1.isClosed()).isEqualTo(1);
    assertThat(spy2.isClosed()).isEqualTo(1);
    assertThat(spy3.isClosed()).isEqualTo(1);
    assertThat(spy4.isClosed()).isEqualTo(1);
  }

  @Test
  @SuppressWarnings({ "resource" }) 
  void shouldClose5ResourceNoMatterWhat() {
    var spy1 = new ResourceDummy();
    var spy2 = new ResourceDummy();
    var spy3 = new ResourceDummy();
    var spy4 = new ResourceDummy();
    var spy5 = new ResourceDummy();
    boolean actual = Try
      .withResource(() -> spy1)
      .withResource(() -> spy2)
      .withResource(() -> spy3)
      .withResource(() -> spy4)
      .withResource(() -> spy5)
      .of((r1, r2, r3, r4, r5) -> function0ThrowCheckedEx())
      .isFailure();

    assertThat(actual).isTrue();
    assertThat(spy1.isClosed()).isEqualTo(1);
    assertThat(spy2.isClosed()).isEqualTo(1);
    assertThat(spy3.isClosed()).isEqualTo(1);
    assertThat(spy4.isClosed()).isEqualTo(1);
    assertThat(spy5.isClosed()).isEqualTo(1);
  }

  @Test
  @SuppressWarnings({ "resource" }) 
  void shouldReturnSuccessAndClose1ResourceWhenCheckedRunnableThrowsCheckedException() {
    var spy1 = new ResourceDummy();
    boolean actual = Try
      .withResource(() -> spy1)
      .ofConsumer((r1) -> runnable()).isSuccess();

    assertThat(actual).isTrue();
    assertThat(spy1.isClosed()).isEqualTo(1);
  }

  @Test
  @SuppressWarnings({ "resource" }) 
  void shouldReturnSuccessAndClose2ResourceWhenCheckedRunnableThrowsCheckedException() {
    var spy1 = new ResourceDummy();
    var spy2 = new ResourceDummy();
    boolean actual = Try
      .withResource(() -> spy1)
      .withResource(() -> spy2)
      .ofConsumer((r1,r2) -> runnable()).isSuccess();

    assertThat(actual).isTrue();
    assertThat(spy1.isClosed()).isEqualTo(1);
    assertThat(spy2.isClosed()).isEqualTo(1);
  }

  @Test
  @SuppressWarnings({ "resource" }) 
  void shouldReturnSuccessAndClose3ResourceWhenCheckedRunnableThrowsCheckedException() {
    var spy1 = new ResourceDummy();
    var spy2 = new ResourceDummy();
    var spy3 = new ResourceDummy();
    boolean actual = Try
      .withResource(() -> spy1)
      .withResource(() -> spy2)
      .withResource(() -> spy3)
      .ofConsumer((r1, r2, r3) -> runnable()).isSuccess();

    assertThat(actual).isTrue();
    assertThat(spy1.isClosed()).isEqualTo(1);
    assertThat(spy2.isClosed()).isEqualTo(1);
    assertThat(spy3.isClosed()).isEqualTo(1);
  }

  @Test
  @SuppressWarnings({ "resource" }) 
  void shouldReturnSuccessAndClose4ResourceWhenCheckedRunnableThrowsCheckedException() {
    var spy1 = new ResourceDummy();
    var spy2 = new ResourceDummy();
    var spy3 = new ResourceDummy();
    var spy4 = new ResourceDummy();
    boolean actual = Try
      .withResource(() -> spy1)
      .withResource(() -> spy2)
      .withResource(() -> spy3)
      .withResource(() -> spy4)
      .ofConsumer((r1, r2, r3, r4) -> runnable()).isSuccess();

    assertThat(actual).isTrue();
    assertThat(spy1.isClosed()).isEqualTo(1);
    assertThat(spy2.isClosed()).isEqualTo(1);
    assertThat(spy3.isClosed()).isEqualTo(1);
    assertThat(spy4.isClosed()).isEqualTo(1);
  }

  @Test
  @SuppressWarnings({ "resource" }) 
  void shouldReturnSuccessAndClose5ResourceWhenCheckedRunnableThrowsCheckedException() {
    var spy1 = new ResourceDummy();
    var spy2 = new ResourceDummy();
    var spy3 = new ResourceDummy();
    var spy4 = new ResourceDummy();
    var spy5 = new ResourceDummy();
    boolean actual = Try
      .withResource(() -> spy1)
      .withResource(() -> spy2)
      .withResource(() -> spy3)
      .withResource(() -> spy4)
      .withResource(() -> spy5)
      .ofConsumer((r1, r2, r3, r4, r5) -> runnable()).isSuccess();

    assertThat(actual).isTrue();
    assertThat(spy1.isClosed()).isEqualTo(1);
    assertThat(spy2.isClosed()).isEqualTo(1);
    assertThat(spy3.isClosed()).isEqualTo(1);
    assertThat(spy4.isClosed()).isEqualTo(1);
    assertThat(spy5.isClosed()).isEqualTo(1);
  }
  @Test
  @SuppressWarnings({ "resource" }) 
  void shouldReturnFailureAndClose1ResourceWhenCheckedRunnableThrowsCheckedException() {
    var spy1 = new ResourceDummy();
    boolean actual = Try
      .withResource(() -> spy1)
      .ofConsumer((r1) -> runnableThrowCheckedEx()).isFailure();

    assertThat(actual).isTrue();
    assertThat(spy1.isClosed()).isEqualTo(1);
  }

  @Test
  @SuppressWarnings({ "resource" }) 
  void shouldReturnFailureAndClose2ResourceWhenCheckedRunnableThrowsCheckedException() {
    var spy1 = new ResourceDummy();
    var spy2 = new ResourceDummy();
    boolean actual = Try
      .withResource(() -> spy1)
      .withResource(() -> spy2)
      .ofConsumer((r1,r2) -> runnableThrowCheckedEx()).isFailure();

    assertThat(actual).isTrue();
    assertThat(spy1.isClosed()).isEqualTo(1);
    assertThat(spy2.isClosed()).isEqualTo(1);
  }

  @Test
  @SuppressWarnings({ "resource" }) 
  void shouldReturnFailureAndClose3ResourceWhenCheckedRunnableThrowsCheckedException() {
    var spy1 = new ResourceDummy();
    var spy2 = new ResourceDummy();
    var spy3 = new ResourceDummy();
    boolean actual = Try
      .withResource(() -> spy1)
      .withResource(() -> spy2)
      .withResource(() -> spy3)
      .ofConsumer((r1,r2,r3) -> runnableThrowCheckedEx()).isFailure();

    assertThat(actual).isTrue();
    assertThat(spy1.isClosed()).isEqualTo(1);
    assertThat(spy2.isClosed()).isEqualTo(1);
    assertThat(spy3.isClosed()).isEqualTo(1);
  }

  @Test
  @SuppressWarnings({ "resource" }) 
  void shouldReturnFailureAndClose4ResourceWhenCheckedRunnableThrowsCheckedException() {
    var spy1 = new ResourceDummy();
    var spy2 = new ResourceDummy();
    var spy3 = new ResourceDummy();
    var spy4 = new ResourceDummy();
    boolean actual = Try
      .withResource(() -> spy1)
      .withResource(() -> spy2)
      .withResource(() -> spy3)
      .withResource(() -> spy4)
      .ofConsumer((r1, r2, r3, r4 ) -> runnableThrowCheckedEx()).isFailure();

    assertThat(actual).isTrue();
    assertThat(spy1.isClosed()).isEqualTo(1);
    assertThat(spy2.isClosed()).isEqualTo(1);
    assertThat(spy3.isClosed()).isEqualTo(1);
    assertThat(spy4.isClosed()).isEqualTo(1);
  }

  @Test
  @SuppressWarnings({ "resource" }) 
  void shouldReturnFailureAndClose5ResourceWhenCheckedRunnableThrowsCheckedException() {
    var spy1 = new ResourceDummy();
    var spy2 = new ResourceDummy();
    var spy3 = new ResourceDummy();
    var spy4 = new ResourceDummy();
    var spy5 = new ResourceDummy();
    boolean actual = Try
      .withResource(() -> spy1)
      .withResource(() -> spy2)
      .withResource(() -> spy3)
      .withResource(() -> spy4)
      .withResource(() -> spy5)
      .ofConsumer((r1,r2,r3,r4,r5) -> runnableThrowCheckedEx()).isFailure();

    assertThat(actual).isTrue();
    assertThat(spy1.isClosed()).isEqualTo(1);
    assertThat(spy2.isClosed()).isEqualTo(1);
    assertThat(spy3.isClosed()).isEqualTo(1);
    assertThat(spy4.isClosed()).isEqualTo(1);
    assertThat(spy5.isClosed()).isEqualTo(1);
  }
  @Test
  void shouldReturnFailureWhenCheckedRunnableThrowsCheckedException() {
    boolean actual = Try.ofRunnable(() -> runnableThrowCheckedEx()).isFailure();

    assertThat(actual).isTrue();
  }

  @Test
  void shouldReturnFailureWhenFunction0ThrowsCheckedException() {
    boolean actual = Try.of(() -> function0ThrowCheckedEx()).isFailure();

    assertThat(actual).isTrue();
  }

  @Test
  void shouldReturnSuccessWhenFunction0IsCalled() {
    Try<String> actual = Try.of(() -> function0ReturnsSuccess());

    assertThat(actual.isSuccess()).isTrue();
    assertThat(actual.orElse(() -> "did not work")).isEqualTo("success");
  }

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
    Try<String> origin = Try.of(() -> function0ThrowCheckedEx());

    Try<String> actual = origin.map(String::toUpperCase);

    assertThat(actual.orElse("FAILURE")).isEqualTo("FAILURE");
  }

  @Test
  void positivePredicateShouldReturnFailure() {
    Try<String> origin = Try.of(() -> function0ReturnsSuccess());

    Try<String> actual = origin.filter(s -> !s.isEmpty(), () -> new Exception("boom!"));

    assertThat(actual.orElse("FAILURE")).isEqualTo("success");
  }

  @Test
  void failingPredicateShouldReturnFailure() {
    Try<String> origin = Try.of(() -> function0ReturnsSuccess());

    Try<String> actual = origin.filter(s -> s.isEmpty(), () -> new Exception("boom!"));

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
    Try<String> origin = Try.of(() -> function0ThrowCheckedEx());

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
