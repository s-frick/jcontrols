package de.sfrick.jmonad;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

public sealed interface Try<A> permits Try.Failure, Try.Success {
  static <A> Try<A> success(A value) {
    return new Success<>(value);
  }

  static <A> Try<A> failure(Throwable cause) {
    return new Failure<>(cause);
  }

  static <A> Try<A> of(Supplier<A> supplier) {
    Objects.requireNonNull(supplier);
    try {
      return new Success<>(supplier.get());
    } catch (Throwable cause) {
      return new Failure<>(cause);
    }
  }

  Try<A> or(Try<A> other);

  Try<A> or(Supplier<Try<A>> other);

  A orElse(A other);

  A orElse(Supplier<A> other);

  boolean isFailure();

  boolean isSuccess(); 

  Either<Throwable, A> toEither();

  Optional<A> toOptional();

  default <B> Try<B> map(Function<? super A, ? extends B> f) {
    return this.flatMap(x -> success(f.apply(x)));
  }

  <B> Try<B> flatMap(Function<? super A, ? extends Try<B>> f);

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
  }
}
