package com.indracompany.sofia2.api.rest.apilimit;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class SimpleRateLimiter implements ApiRateLimiter {
    private Semaphore semaphore;
    private int maxPermits;
    private TimeUnit timePeriod;
    private String id;
    private ScheduledExecutorService scheduler;

    public static ApiRateLimiter create(int permits, TimeUnit timePeriod) {
        SimpleRateLimiter limiter = new SimpleRateLimiter("",permits, timePeriod);
        limiter.schedulePermitReplenishment();
        return limiter;
    }
    
    public static SimpleRateLimiter create(String id, int permits, TimeUnit timePeriod) {
        SimpleRateLimiter limiter = new SimpleRateLimiter(id, permits, timePeriod);
        limiter.schedulePermitReplenishment();
        return limiter;
    }

    private SimpleRateLimiter(String id, int permits, TimeUnit timePeriod) {
        this.semaphore = new Semaphore(permits);
        this.maxPermits = permits;
        this.timePeriod = timePeriod;
        this.id=id;
    }

    public boolean tryAcquire() {
        return semaphore.tryAcquire();
    }

    public void stop() {
        scheduler.shutdownNow();
    }

    public void schedulePermitReplenishment() {
        scheduler = Executors.newScheduledThreadPool(1);
        scheduler.schedule(() -> {
            semaphore.release(maxPermits - semaphore.availablePermits());
        }, 1, timePeriod);

    }
}