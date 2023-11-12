package io.github.sfrick.jcontrols;

@FunctionalInterface
public interface CheckedRunnable {
  
  void run() throws Throwable;

}
