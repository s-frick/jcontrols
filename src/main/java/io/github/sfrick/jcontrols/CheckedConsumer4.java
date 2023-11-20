package io.github.sfrick.jcontrols;

@FunctionalInterface
public interface CheckedConsumer4<A1,A2,A3,A4> {
  
  void apply(A1 a1, A2 a2, A3 a3, A4 a4) throws Throwable;

}

