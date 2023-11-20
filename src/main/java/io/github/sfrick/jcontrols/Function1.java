package io.github.sfrick.jcontrols;

@FunctionalInterface
public interface Function1<T1, R> {
  
  R apply(T1 t1) throws Throwable;

}
