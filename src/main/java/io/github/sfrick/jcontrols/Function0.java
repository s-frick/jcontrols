package io.github.sfrick.jcontrols;

@FunctionalInterface
public interface Function0<R> {
  
  R apply() throws Throwable;

}
