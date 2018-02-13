package com.indracompany.sofia2.api.rest.apilimit;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import javax.annotation.PreDestroy;

import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class RateLimitingService {

	private Map<String, ApiRateLimiter> limiters = new ConcurrentHashMap<>();

	private int permits=5;
	private TimeUnit timePeriod = TimeUnit.SECONDS;

	public boolean processRateLimit(String clientId, int permits) {
		if (clientId == null) {
			return true;
		}
		ApiRateLimiter limiter = getRateLimiter(clientId, permits);
		boolean allowRequest = limiter.tryAcquire();
		return allowRequest;
	}

	private ApiRateLimiter getRateLimiter(String clientId, int permits) {
		if (limiters.containsKey(clientId)) {
			return limiters.get(clientId);
		} else {
			synchronized (clientId.intern()) {
				if (limiters.containsKey(clientId)) {
					return limiters.get(clientId);
				}

				ApiRateLimiter rateLimiter = createRateLimiter(clientId,permits);
				limiters.put(clientId, rateLimiter);
				return rateLimiter;
			}
		}
	}

	private ApiRateLimiter createRateLimiter(String clientId, int attempts) {
		if (attempts<=0) {
			return GuavaRateLimiter.create(clientId, attempts, timePeriod);
		}
		else 
			return GuavaRateLimiter.create(clientId, permits, timePeriod);
		
	}

}