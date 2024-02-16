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
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public sealed interface Either<E, A> permits Either.Failure, Either.Success {

  /**
   * Constructs an Either from a success value.
   * 
   * @param <E>   The failure type of the constructed Either
   * @param <A>   The success type of the constructed Either
   * @param value the success value from which the Either will be consutructed.
   * @return
   */
  static <E, A> Either<E, A> success(A value) {
    return new Success<>(value);
  }

  /**
   * Constructs an Either from a failure value.
   * 
   * @param <E>     The failure type of the constructed Either
   * @param <A>     The success type of the constructed Either
   * @param failure the failure value from which the Either will be consutructed.
   * @return
   */
  static <E, A> Either<E, A> failure(E failure) {
    return new Failure<>(failure);
  }

  /**
   * Constructs an Either from an Optional.
   * 
   * @param <F>          The failure type of the constructed Either
   * @param <B>          The success type of the constructed Either
   * @param optional     - the Optional from which the Either will be constructed
   * @param errorMessage - the failure from which the failure side of the Either
   *                     will be constructed.
   * @return Either.Success if the optional has a value, otherwise if the optional
   *         is empty returns Either.Failure with the provided failure.
   * @throws NullPointerException - if one of the params are null
   */
  static <F, B> Either<F, B> fromOptional(Optional<B> optional, F errorMessage) {
    Objects.requireNonNull(optional);
    Objects.requireNonNull(errorMessage);
    if (optional.isEmpty()) {
      return new Failure<>(errorMessage);
    } else {
      return new Success<>(optional.get());
    }
  }

  /**
   * If a success value is present, apply the mapping function f to it.
   * 
   * @param <F> - The failure type of the result of the mapping function
   * @param <B> - The success type of the result of the mapping function
   * @param f   - a mapping function to apply to the success value, if present
   * @return an Either describing the result of applying a mapping function to the
   *         success value of this Either, if a success value is present,
   *         otherwise the origin casted Either
   * @throws NullPointerException - if the mapping function is null
   */
  <F, B> Either<F, B> map(Function<? super A, ? extends B> f);

  /**
   * If a failure value is present, apply the mapping function f to it.
   * 
   * @param <F> - The failure type of the result of the mapping function
   * @param f   - a mapping function to apply to the failure value, if present
   * @return an Either describing the result of applying a mapping function to the
   *         failure value of this Either, if a failure value is present,
   *         otherwise the origin casted Either
   * @throws NullPointerException - if the mapping function is null
   */
  <F> Either<F, A> mapF(Function<? super E, ? extends F> f);

  /**
   * Alias of {@link #mapF(Function)}
   * 
   * @param <F> - The failure type of the result of the mapping function
   * @param f   - a mapping function to apply to the failure value, if present
   * @return an Either describing the result of applying a mapping function to the
   *         failure value of this Either, if a failure value is present,
   *         otherwise the origin casted Either
   * @throws NullPointerException - if the mapping function is null
   */
  default <F> Either<F, A> mapFailure(Function<? super E, ? extends F> f) {
    return mapF(f);
  }

  /**
   * Converts this Either to an Optional.
   * If this Either has a success value, an non-empty Optional containing the
   * success value. Otherwise returns an empty Optional.
   * 
   * @return a Optional containing the success value or Optional empty
   */
  Optional<A> toOptional();

  /**
   * @return true if this Either is a failure value, otherwise true
   */
  boolean isFailure();

  /**
   * @return true if this Either is a success value, otherwise true
   */
  boolean isSuccess();

  /**
   * Consumes the success value of this Either with the provided {@link Consumer},
   * if an success value is present, otherwise no Operation.
   * 
   * @param consumer
   */
  void ifPresent(Consumer<? super A> consumer);

  /**
   * @param other another Either
   * @return other Either, if this Either contains a failure value. Otherwise if
   *         this Either.
   */
  Either<E, A> or(Either<E, A> other);

  /**
   * @param other a {@link Supplier} that returns an Either
   * @return other Either, if this Either contains a failure value. Otherwise if
   *         this Either.
   */
  Either<E, A> or(Supplier<? extends Either<E, A>> other);

  /**
   * Unwraps the success value of this Either, if it's present. Otherwise the
   * provided default.
   * 
   * @param other
   * @return Unwraps the success value of this Either, if it's present. Otherwise
   *         the provided default.
   */
  A getOrDefault(A other);

  /**
   * If a success value is present, apply the mapping function f to it. Returns
   * the resulting Either without wrapping it in another Either, if the success
   * value is present, otherwise the origin Either.
   * 
   * @param <F> - The failure type of the result of the mapping function
   * @param <B> - The success type of the result of the mapping function
   * @param f   - a mapping function to apply to the success value, if present
   * @return an Either describing the result of applying a mapping function to the
   *         success value of this Either, if a success value is present,
   *         otherwise the origin casted Either
   * @throws NullPointerException - if the mapping function is null
   */
  <F, B> Either<F, B> flatMap(Function<? super A, ? extends Either<F, B>> f);

  record Failure<E, A>(E value) implements Either<E, A> {

    @Override
    @SuppressWarnings("unchecked")
    public <F, B> Either<F, B> flatMap(Function<? super A, ? extends Either<F, B>> f) {
      Objects.requireNonNull(f, "Mapper function is null.");
      return (Either<F, B>) this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <F, B> Either<F, B> map(Function<? super A, ? extends B> f) {
      Objects.requireNonNull(f, "Mapper function is null.");
      return (Either<F, B>) this;
    }

    @Override
    public A getOrDefault(A other) {
      return other;
    }

    @Override
    public void ifPresent(Consumer<? super A> consumer) {
      // noOp
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
    public <F> Either<F, A> mapF(Function<? super E, ? extends F> f) {
      Objects.requireNonNull(f, "Mapper function is null.");
      return new Failure<>(f.apply(value));
    }

    @Override
    public Either<E, A> or(Either<E, A> other) {
      return other;
    }

    @Override
    public Either<E, A> or(Supplier<? extends Either<E, A>> other) {
      return other.get();
    }

    @Override
    public Optional<A> toOptional() {
      return Optional.empty();
    }

  }

  record Success<E, A>(A value) implements Either<E, A> {

    @Override
    public <F, B> Either<F, B> flatMap(Function<? super A, ? extends Either<F, B>> f) {
      Objects.requireNonNull(f, "Mapper function is null.");
      return f.apply(value);
    }

    @Override
    public <F, B> Either<F, B> map(Function<? super A, ? extends B> f) {
      Objects.requireNonNull(f, "Mapper function is null.");
      return new Success<>(f.apply(value));
    }

    @Override
    public A getOrDefault(A other) {
      return value;
    }

    @Override
    public void ifPresent(Consumer<? super A> consumer) {
      consumer.accept(value);
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
    @SuppressWarnings("unchecked")
    public <F> Either<F, A> mapF(Function<? super E, ? extends F> f) {
      return (Either<F, A>) this;
    }

    @Override
    public Either<E, A> or(Either<E, A> other) {
      return this;
    }

    @Override
    public Either<E, A> or(Supplier<? extends Either<E, A>> other) {
      return this;
    }

    @Override
    public Optional<A> toOptional() {
      return Optional.of(value);
    }
  }
}
