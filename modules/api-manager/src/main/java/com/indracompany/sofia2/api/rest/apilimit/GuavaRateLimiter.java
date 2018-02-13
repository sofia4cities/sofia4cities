package com.indracompany.sofia2.api.rest.apilimit;

import java.util.concurrent.TimeUnit;

import com.google.common.util.concurrent.RateLimiter;

public class GuavaRateLimiter implements ApiRateLimiter {
	private RateLimiter rateLimiter;
    private String id;
    private int permits;

    public static ApiRateLimiter create(String id, int permits, TimeUnit timePeriod) {
    	ApiRateLimiter limiter = new GuavaRateLimiter(id,permits, timePeriod);
        return limiter;
    }

    private GuavaRateLimiter(String id, int permits, TimeUnit timePeriod) {
    	rateLimiter = RateLimiter.create(permits);
        this.id=id;
        this.permits=permits;
    }

    public boolean tryAcquire() {
        return rateLimiter.tryAcquire(permits);
    }

	public RateLimiter getRateLimiter() {
		return rateLimiter;
	}

	public void setRateLimiter(RateLimiter rateLimiter) {
		this.rateLimiter = rateLimiter;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

   
    
    
}