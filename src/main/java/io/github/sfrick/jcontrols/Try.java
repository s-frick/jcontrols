package io.github.sfrick.jcontrols;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

//TODO: add javadoc
public sealed interface Try<A> permits Try.Failure, Try.Success {
  /**
   * @param <A>
   * @param value
   * @return {@link Try}
   */
  static <A> Try<A> success(A value) {
    return new Success<>(value);
  }

  
  /**
   * @param <A>
   * @param cause
   * @return {@link Try}
   */
  static <A> Try<A> failure(Throwable cause) {
    return new Failure<>(cause);
  }

  /**
   * @param runnable
   * @return {@link Try}
   */
  static Try<Void> ofRunnable(CheckedRunnable runnable){
    Objects.requireNonNull(runnable);
    try {
      runnable.run();
      return new Success<Void>(null);
    } catch(Throwable t) {
      return new Failure<>(t);
    }
  }

  /**
   * @param <A>
   * @param work that may fail with an {@link Throwable}
   * @return {@link Try}
   */
  static <A> Try<A> of(Function0<A> work){
    Objects.requireNonNull(work);
    try {
      return new Success<A>(work.apply());
    } catch(Throwable t) {
      return new Failure<>(t);
    }
  }

  /**
   * @param <A>
   * @param resource
   * @return {@link Try}
   */
  static <A extends AutoCloseable> WithResource1<A> withResource(Function0<A> resource) {
    return new WithResource1<>(resource);
  }

  /**
   * @param other
   * @return {@link Try}
   */
  Try<A> or(Try<A> other);

  /**
   * @param other
   * @return {@link Try}
   */
  Try<A> or(Supplier<Try<A>> other);

  /**
   * @param other
   * @return either value of {@link Try} or other
   */
  A orElse(A other);

  /**
   * @param other
   * @return either value of {@link Try} or value from {@link Supplier}
   */
  A orElse(Supplier<A> other);

  /**
   * @return true if {@link Try} is {@link Failure}
   */
  boolean isFailure();

  /**
   * @return true if {@link Try} is {@link Success}
   */
  boolean isSuccess(); 

  /**
   * @return {@link Either} transformed from this {@link Try}
   */
  Either<Throwable, A> toEither();

  /**
   * @return {@link Optional} transformed from this {@link Try}
   */
  Optional<A> toOptional();

  /**
   * @param <B>
   * @param f
   * @return {@link Try}
   */
  default <B> Try<B> map(Function<? super A, ? extends B> f) {
    return this.flatMap(x -> success(f.apply(x)));
  }

  /**
   * @param <B>
   * @param f
   * @return {@link Try}
   */
  <B> Try<B> flatMap(Function<? super A, ? extends Try<B>> f);

  /**
   * @param predicate
   * @param throwable
   * @return a filtered {@link Try}
   */
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
  
  record WithResource1<A1 extends AutoCloseable>(Function0<A1> resource) {
    /**
     * @param <A2> type param for resource1
     * @param resource2
     * @return {@link WithResource2}
     */
    public <A2 extends AutoCloseable> WithResource2<A1, A2> withResource(Function0<A2> resource2) {
      return new WithResource2<A1, A2>(resource, resource2);
    }
    /**
     * Constructs from WithResources builder a {@link Try}
     * @param <B> type of returned value from work
     * @param work that may fail with an {@link Throwable}
     * @return {@link Try}
     */
    public <B> Try<B> of(Function1<? super A1, ? extends B> work) {
      return Try.of(() -> {
        try(A1 a = resource.apply()){
          return work.apply(a);
        }
      });
    }

    /**
     * Constructs from WithResources builder a {@link Try}
     * @param work that may fail with an {@link Throwable}
     * @return {@link Try}
     */
    public Try<Void> ofConsumer(CheckedConsumer1<A1> work) {
      return Try.ofRunnable(() -> {
        try(A1 a = resource.apply()){
          work.apply(a);
        }
      });
    }
  }

  record WithResource2<A1 extends AutoCloseable, A2 extends AutoCloseable>(Function0<A1> resource1, Function0<A2> resource2) {
    /**
     * @param <A3> type param for resource3
     * @param resource3
     * @return {@link WithResource3}
     */
    public <A3 extends AutoCloseable> WithResource3<A1, A2, A3> withResource(Function0<A3> resource3) {
      return new WithResource3<A1, A2, A3>(resource1, resource2, resource3);
    }
    /**
     * Constructs from WithResources builder a {@link Try}
     * @param <B> type of returned value from work
     * @param work that may fail with an {@link Throwable}
     * @return {@link Try}
     */
    public <B>Try<B> of(Function2<? super A1, ? super A2, ? extends B> work) {
      return Try.of(() -> {
        try(A1 a1 = resource1.apply(); A2 a2 = resource2.apply()){
          return work.apply(a1, a2);
        }
      });
    }
    /**
     * Constructs from WithResources builder a {@link Try}
     * @param work that may fail with an {@link Throwable}
     * @return {@link Try}
     */
    public Try<Void> ofConsumer(CheckedConsumer2<A1, A2> work) {
      return Try.ofRunnable(() -> {
        try(A1 a1 = resource1.apply(); A2 a2 = resource2.apply()){
          work.apply(a1, a2);
        }
      });
    }
  }

  record WithResource3<A1 extends AutoCloseable, A2 extends AutoCloseable, A3 extends AutoCloseable>(Function0<A1> resource1, Function0<A2> resource2, Function0<A3> resource3) {
    /**
     * Adds a resource
     * @param <A4> type param for resource3
     * @param resource4
     * @return {@link WithResource4}
     */
    public <A4 extends AutoCloseable> WithResource4<A1, A2, A3, A4> withResource(Function0<A4> resource4) {
      return new WithResource4<>(resource1, resource2, resource3, resource4);
    }
    /**
     * Constructs from WithResources builder a {@link Try}
     * @param <B> type param for resource2
     * @param work that may fail with an {@link Throwable}
     * @return {@link Try}
     */
    public <B>Try<B> of(Function3<? super A1, ? super A2, ? super A3, ? extends B> work) {
      return Try.of(() -> {
        try(A1 a1 = resource1.apply(); A2 a2 = resource2.apply(); A3 a3 = resource3.apply()){
          return work.apply(a1, a2, a3);
        }
      });
    }
    /**
     * Constructs from WithResources builder a {@link Try}
     * @param work that may fail with an {@link Throwable}
     * @return {@link Try}
     */
    public Try<Void> ofConsumer(CheckedConsumer3<A1, A2, A3> work) {
      return Try.ofRunnable(() -> {
        try(A1 a1 = resource1.apply(); A2 a2 = resource2.apply(); A3 a3 = resource3.apply()){
          work.apply(a1, a2, a3);
        }
      });
    }
  }

  record WithResource4<A1 extends AutoCloseable, A2 extends AutoCloseable, A3 extends AutoCloseable, A4 extends AutoCloseable>(Function0<A1> resource1, Function0<A2> resource2, Function0<A3> resource3, Function0<A4> resource4) {
    /**
     * Adds a resource
     * @param <A5> type param for resource3
     * @param resource5
     * @return {@link WithResource5}
     */
    public <A5 extends AutoCloseable> WithResource5<A1, A2, A3, A4, A5> withResource(Function0<A5> resource5) {
      return new WithResource5<>(resource1, resource2, resource3, resource4, resource5);
    }
    /**
     * Constructs from WithResources builder a {@link Try}
     * @param <B> type of returned value from work
     * @param work that may fail with an {@link Throwable}
     * @return {@link Try}
     */
    public <B>Try<B> of(Function4<? super A1, ? super A2, ? super A3, ? super A4, ? extends B> work) {
      return Try.of(() -> {
        try(A1 a1 = resource1.apply(); A2 a2 = resource2.apply(); A3 a3 = resource3.apply(); A4 a4 = resource4.apply()){
          return work.apply(a1, a2, a3, a4);
        }
      });
    }
    /**
     * Constructs from WithResources builder a {@link Try}
     * @param work that may fail with an {@link Throwable}
     * @return {@link Try}
     */
    public Try<Void> ofConsumer(CheckedConsumer4<A1, A2, A3, A4> work) {
      return Try.ofRunnable(() -> {
        try(A1 a1 = resource1.apply(); A2 a2 = resource2.apply(); A3 a3 = resource3.apply(); A4 a4 = resource4.apply()){
          work.apply(a1, a2, a3, a4);
        }
      });
    }
  }

  record WithResource5<A1 extends AutoCloseable, A2 extends AutoCloseable, A3 extends AutoCloseable, A4 extends AutoCloseable, A5 extends AutoCloseable>(Function0<A1> resource1, Function0<A2> resource2, Function0<A3> resource3, Function0<A4> resource4, Function0<A5> resource5) {
    /**
     * Constructs from WithResources builder a {@link Try}
     * @param <B> type of returned value from work
     * @param work that may fail with an {@link Throwable}
     * @return {@link Try}
     */
    public <B>Try<B> of(Function5<? super A1, ? super A2, ? super A3, ? super A4, ? super A5, ? extends B> work) {
      return Try.of(() -> {
        try(A1 a1 = resource1.apply(); A2 a2 = resource2.apply(); A3 a3 = resource3.apply(); A4 a4 = resource4.apply(); A5 a5 = resource5.apply()){
          return work.apply(a1, a2, a3, a4, a5);
        }
      });
    }
    /**
     * Constructs from WithResources builder a {@link Try}
     * @param work that may fail with an {@link Throwable}
     * @return {@link Try}
     */
    public Try<Void> ofConsumer(CheckedConsumer5<A1, A2, A3, A4, A5> work) {
      return Try.ofRunnable(() -> {
        try(A1 a1 = resource1.apply(); A2 a2 = resource2.apply(); A3 a3 = resource3.apply(); A4 a4 = resource4.apply(); A5 a5 = resource5.apply()){
          work.apply(a1, a2, a3, a4, a5);
        }
      });
    }
  }
}
