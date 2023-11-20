package io.github.sfrick.jcontrols;

@FunctionalInterface
public interface CheckedConsumer2<A1,A2> {
  
  void apply(A1 a1, A2 a2) throws Throwable;

}
