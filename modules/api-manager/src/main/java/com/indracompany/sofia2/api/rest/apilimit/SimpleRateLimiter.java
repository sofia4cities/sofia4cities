/**
 * Copyright Indra Sistemas, S.A.
 * 2013-2018 SPAIN
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *      http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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