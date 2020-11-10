package com.browserstack.run_parallel_test;

import org.junit.runners.Parameterized;
import org.junit.runners.model.RunnerScheduler;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Parallelizedios extends Parameterized {

  private static class ThreadPoolScheduler implements RunnerScheduler {
    private ExecutorService executor; 

    public ThreadPoolScheduler() {
      String threads = System.getProperty("junit.parallel.threads", "16");
      int numThreads = Integer.parseInt(threads);
      executor = Executors.newFixedThreadPool(numThreads);
    }

    @Override
    public void finished() {
      executor.shutdown();
      try {
        executor.awaitTermination(10, TimeUnit.MINUTES);
      } catch (InterruptedException exc) {
        throw new RuntimeException(exc);
      }
    }

    @Override
    public void schedule(Runnable childStatement) {
      executor.submit(childStatement);
    }
  }

  public Parallelizedios(Class klass) throws Throwable {
    super(klass);
    setScheduler(new ThreadPoolScheduler());
  }
}