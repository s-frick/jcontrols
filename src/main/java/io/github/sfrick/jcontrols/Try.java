// MIT License
//
// Copyright (c) 2023 Sebastian Frick
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in all
// copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
// SOFTWARE.
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
  static Try<Void> ofRunnable(CheckedRunnable runnable) {
    Objects.requireNonNull(runnable);
    try {
      runnable.run();
      return new Success<Void>(null);
    } catch (Throwable t) {
      return new Failure<>(t);
    }
  }

  static <A> Try<A> of(Function0<A> work) {
    Objects.requireNonNull(work);
    try {
      return new Success<A>(work.apply());
    } catch (Throwable t) {
      return new Failure<>(t);
    }
  }

  static <A extends AutoCloseable> WithResouce1<A> withResource(Function0<A> resource) {
    return new WithResouce1<>(resource);
  }

  static <A1 extends AutoCloseable, A2 extends AutoCloseable> WithResouce2<A1, A2> withResource(Function0<A1> resource1,
      Function0<A2> resource2) {
    return new WithResouce2<>(resource1, resource2);
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

  record WithResouce1<A1 extends AutoCloseable>(Function0<A1> resource) {
    public <A2 extends AutoCloseable> WithResouce2<A1, A2> withResource(Function0<A2> resource2) {
      return new WithResouce2<A1, A2>(resource, resource2);
    }

    public <B> Try<B> of(Function1<? super A1, ? extends B> work) {
      return Try.of(() -> {
        try (A1 a = resource.apply()) {
          return work.apply(a);
        }
      });
    }

    public Try<Void> ofConsumer(CheckedConsumer1<A1> work) {
      return Try.ofRunnable(() -> {
        try (A1 a = resource.apply()) {
          work.apply(a);
        }
      });
    }
  }

  record WithResouce2<A1 extends AutoCloseable, A2 extends AutoCloseable>(Function0<A1> resource1,
      Function0<A2> resource2) {
    public <A3 extends AutoCloseable> WithResouce3<A1, A2, A3> withResource(Function0<A3> resource3) {
      return new WithResouce3<A1, A2, A3>(resource1, resource2, resource3);
    }

    public <B> Try<B> of(Function2<? super A1, ? super A2, ? extends B> work) {
      return Try.of(() -> {
        try (A1 a1 = resource1.apply(); A2 a2 = resource2.apply()) {
          return work.apply(a1, a2);
        }
      });
    }

    public Try<Void> ofConsumer(CheckedConsumer2<A1, A2> work) {
      return Try.ofRunnable(() -> {
        try (A1 a1 = resource1.apply(); A2 a2 = resource2.apply()) {
          work.apply(a1, a2);
        }
      });
    }
  }

  record WithResouce3<A1 extends AutoCloseable, A2 extends AutoCloseable, A3 extends AutoCloseable>(
      Function0<A1> resource1, Function0<A2> resource2, Function0<A3> resource3) {
    public <A4 extends AutoCloseable> WithResouce4<A1, A2, A3, A4> withResource(Function0<A4> resource4) {
      return new WithResouce4<>(resource1, resource2, resource3, resource4);
    }

    public <B> Try<B> of(Function3<? super A1, ? super A2, ? super A3, ? extends B> work) {
      return Try.of(() -> {
        try (A1 a1 = resource1.apply(); A2 a2 = resource2.apply(); A3 a3 = resource3.apply()) {
          return work.apply(a1, a2, a3);
        }
      });
    }

    public Try<Void> ofConsumer(CheckedConsumer3<A1, A2, A3> work) {
      return Try.ofRunnable(() -> {
        try (A1 a1 = resource1.apply(); A2 a2 = resource2.apply(); A3 a3 = resource3.apply()) {
          work.apply(a1, a2, a3);
        }
      });
    }
  }

  record WithResouce4<A1 extends AutoCloseable, A2 extends AutoCloseable, A3 extends AutoCloseable, A4 extends AutoCloseable>(
      Function0<A1> resource1, Function0<A2> resource2, Function0<A3> resource3, Function0<A4> resource4) {
    public <A5 extends AutoCloseable> WithResouce5<A1, A2, A3, A4, A5> withResource(Function0<A5> resource5) {
      return new WithResouce5<>(resource1, resource2, resource3, resource4, resource5);
    }

    public <B> Try<B> of(Function4<? super A1, ? super A2, ? super A3, ? super A4, ? extends B> work) {
      return Try.of(() -> {
        try (A1 a1 = resource1.apply();
            A2 a2 = resource2.apply();
            A3 a3 = resource3.apply();
            A4 a4 = resource4.apply()) {
          return work.apply(a1, a2, a3, a4);
        }
      });
    }

    public Try<Void> ofConsumer(CheckedConsumer4<A1, A2, A3, A4> work) {
      return Try.ofRunnable(() -> {
        try (A1 a1 = resource1.apply();
            A2 a2 = resource2.apply();
            A3 a3 = resource3.apply();
            A4 a4 = resource4.apply()) {
          work.apply(a1, a2, a3, a4);
        }
      });
    }
  }

  record WithResouce5<A1 extends AutoCloseable, A2 extends AutoCloseable, A3 extends AutoCloseable, A4 extends AutoCloseable, A5 extends AutoCloseable>(
      Function0<A1> resource1, Function0<A2> resource2, Function0<A3> resource3, Function0<A4> resource4,
      Function0<A5> resource5) {
    public <B> Try<B> of(Function5<? super A1, ? super A2, ? super A3, ? super A4, ? super A5, ? extends B> work) {
      return Try.of(() -> {
        try (A1 a1 = resource1.apply();
            A2 a2 = resource2.apply();
            A3 a3 = resource3.apply();
            A4 a4 = resource4.apply();
            A5 a5 = resource5.apply()) {
          return work.apply(a1, a2, a3, a4, a5);
        }
      });
    }

    public Try<Void> ofConsumer(CheckedConsumer5<A1, A2, A3, A4, A5> work) {
      return Try.ofRunnable(() -> {
        try (A1 a1 = resource1.apply();
            A2 a2 = resource2.apply();
            A3 a3 = resource3.apply();
            A4 a4 = resource4.apply();
            A5 a5 = resource5.apply()) {
          work.apply(a1, a2, a3, a4, a5);
        }
      });
    }
  }
}
