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
	
	private static final Object lock1 = new Object();

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
			synchronized (lock1) {
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