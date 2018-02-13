package com.indracompany.sofia2.api.rest.apilimit;

public interface ApiRateLimiter {

	public boolean tryAcquire();
}
