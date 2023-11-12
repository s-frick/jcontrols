package io.github.sfrick.jcontrols;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public sealed interface Try<A> permits Try.Failure, Try.Success {
  /**
   * @param <A>
   * @param value
   * @return
   */
  static <A> Try<A> success(A value) {
    return new Success<>(value);
  }

  
  /**
   * @param <A>
   * @param cause
   * @return
   */
  static <A> Try<A> failure(Throwable cause) {
    return new Failure<>(cause);
  }

  /**
   * @param runnable
   * @return
   */
  static Try<Void> run(CheckedRunnable runnable){
    Objects.requireNonNull(runnable);
    try {
      runnable.run();
      return new Success<Void>(null);
    } catch(Throwable t) {
      return new Failure<>(t);
    }
  }

  static <A> Try<A> of(Function0<A> work){
    Objects.requireNonNull(work);
    try {
      return new Success<A>(work.apply());
    } catch(Throwable t) {
      return new Failure<>(t);
    }
  }

  /**
   * @param other
   * @return
   */
  Try<A> or(Try<A> other);

  /**
   * @param other
   * @return
   */
  Try<A> or(Supplier<Try<A>> other);

  /**
   * @param other
   * @return
   */
  A orElse(A other);

  /**
   * @param other
   * @return
   */
  A orElse(Supplier<A> other);

  /**
   * @return
   */
  boolean isFailure();

  /**
   * @return
   */
  boolean isSuccess(); 

  /**
   * @return
   */
  Either<Throwable, A> toEither();

  /**
   * @return
   */
  Optional<A> toOptional();

  /**
   * @param <B>
   * @param f
   * @return
   */
  default <B> Try<B> map(Function<? super A, ? extends B> f) {
    return this.flatMap(x -> success(f.apply(x)));
  }

  /**
   * @param <B>
   * @param f
   * @return
   */
  <B> Try<B> flatMap(Function<? super A, ? extends Try<B>> f);

  Try<A> filter(Predicate<A> predicate, Supplier<? extends Throwable> throwable);

  record Success<T>(T value) implements Try<T> {
    @Override
    public <B> Try<B> flatMap(Function<? super T, ? extends Try<B>> f) {
      Objects.requireNonNull(f);
      try {
        return f.apply(value());
      } catch (Throwable cause) {
        return new Failure<>(cause);
      }
    }

    @Override
    public boolean isFailure() {
      return false;
    }

    @Override
    public boolean isSuccess() {
      return true;
    }

    @Override
    public Either<Throwable, T> toEither() {
      return Either.success(value());
    }

    @Override
    public Optional<T> toOptional() {
      return Optional.of(value());
    }

    @Override
    public Try<T> or(Try<T> other) {
      return this;
    }

    @Override
    public Try<T> or(Supplier<Try<T>> other) {
      return this;
    }

    @Override
    public T orElse(T other) {
      return this.value();
    }

    @Override
    public T orElse(Supplier<T> other) {
      return this.value();
    }

    @Override
    public Try<T> filter(Predicate<T> predicate, Supplier<? extends Throwable> throwable) {
      Objects.requireNonNull(predicate);
      Objects.requireNonNull(throwable);
      return predicate.test(this.value()) ? this : Try.failure(throwable.get());
    }
  }

  @SuppressWarnings("unchecked")
  record Failure<T>(Throwable cause) implements Try<T> {
    @Override
    public <B> Try<B> flatMap(Function<? super T, ? extends Try<B>> f) {
      Objects.requireNonNull(f);
      return (Try<B>) this;
    }

    @Override
    public boolean isFailure() {
      return true;
    }

    @Override
    public boolean isSuccess() {
      return false;
    }

    @Override
    public Either<Throwable, T> toEither() {
      return (Either<Throwable, T>) Either.failure(cause());
    }

    @Override
    public Optional<T> toOptional() {
      return Optional.empty();
    }

    @Override
    public Try<T> or(Try<T> other) {
      Objects.requireNonNull(other);
      return other;
    }

    @Override
    public Try<T> or(Supplier<Try<T>> other) {
      Objects.requireNonNull(other);
      return other.get();
    }

    @Override
    public T orElse(T other) {
      Objects.requireNonNull(other);
      return other;
    }

    @Override
    public T orElse(Supplier<T> other) {
      return other.get();
    }

    @Override
    public Try<T> filter(Predicate<T> predicate, Supplier<? extends Throwable> throwable) {
      Objects.requireNonNull(predicate);
      Objects.requireNonNull(throwable);
      return Try.failure(throwable.get());
    }
  }
}
