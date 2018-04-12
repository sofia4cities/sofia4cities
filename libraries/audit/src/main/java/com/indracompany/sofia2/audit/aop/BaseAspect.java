/**
 * Copyright Indra Sistemas, S.A.
 * 2013-2018 SPAIN
 *
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
package com.indracompany.sofia2.audit.aop;

import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;

import com.indracompany.sofia2.commons.audit.producer.EventProducer;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BaseAspect {

	private static ConcurrentHashMap<String, MethodStats> methodStats = new ConcurrentHashMap<String, MethodStats>();
	private static long statLogFrequency = 10;
	private static long methodWarningThreshold = 1000;
	
	@Autowired
	protected EventProducer eventProducer;

	public static Method getMethod(JoinPoint joinPoint) {

		MethodSignature signature = (MethodSignature) joinPoint.getSignature();
		Method method = signature.getMethod();
		return method;
	}

	public static String getClassName(JoinPoint joinPoint) {

		String method = joinPoint.getTarget().getClass().getName();
		return method;
	}

	public void updateStats(String className, String methodName, long elapsedTime) {
		MethodStats stats = methodStats.get(methodName);

		if (stats == null) {
			stats = new MethodStats(className, methodName);
			methodStats.put(methodName, stats);
		}
		stats.count++;
		stats.totalTime += elapsedTime;
		if (elapsedTime > stats.maxTime) {
			stats.maxTime = elapsedTime;
		}

		if (elapsedTime > methodWarningThreshold) {
			log.warn("method warning: Class : " + className + " Method: " + methodName + "(), cnt = " + stats.count
					+ ", lastTime = " + elapsedTime + ", maxTime = " + stats.maxTime);
		}

		if (stats.count % statLogFrequency == 0) {
			long avgTime = stats.totalTime / stats.count;
			long runningAvg = (stats.totalTime - stats.lastTotalTime) / statLogFrequency;
			log.info(" Class : " + className + " Method : " + methodName + "(), cnt = " + stats.count + ", lastTime = "
					+ elapsedTime + ", avgTime = " + avgTime + ", runningAvg = " + runningAvg + ", maxTime = "
					+ stats.maxTime);

			// reset the last total time
			stats.lastTotalTime = stats.totalTime;
		}

		// System.out.println("method debug: " + methodName + "(), cnt = " + stats.count
		// + ", lastTime = " + elapsedTime + ", maxTime = " + stats.maxTime);
	}
	
	protected Object getTheObject(JoinPoint joinPoint, Class T) {
		Object obj = null;
		if (joinPoint.getArgs() != null) {
			int size = joinPoint.getArgs().length;
			if (size > 0) {
				Object[] obs = joinPoint.getArgs();
				for (Object object : obs) {
					if (T.isInstance(object))
						obj = object;
				}
			}
		}
		return obj;
	}

	class MethodStats {
		public String methodName;
		public String className;
		public long count;
		public long totalTime;
		public long lastTotalTime;
		public long maxTime;

		public MethodStats(String className, String methodName) {
			this.className = className;
			this.methodName = methodName;
		}
	}

}