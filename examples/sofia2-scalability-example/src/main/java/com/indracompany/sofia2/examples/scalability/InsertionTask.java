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
package com.indracompany.sofia2.examples.scalability;

import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.indracompany.sofia2.examples.scalability.msgs.Injector;
import com.indracompany.sofia2.examples.scalability.msgs.InjectorStatus;

import lombok.extern.slf4j.Slf4j;

@Component
@Scope("prototype")
@Slf4j
public class InsertionTask implements Runnable{
	
	private final Object lock = new Object();
	
	private final Client client;
	private final String ontology;
	private final String data;
	private final Injector injector;
	private final Date start;
	private final int delay;
	private final long periodLimit = 5000l;
	private final ConcurrentHashMap<Injector, InjectorStatus> statues;
	
	private volatile boolean stop = false;
	private volatile int sent = 0;
	private volatile int sentPeriod = 0;
	private volatile int errors = 0;
	private volatile int errorsPeriod = 0;
	private volatile long timespent = 0;
	private volatile long timespentPeriod = 0;
	private volatile Date startPeriod;
		
	public InsertionTask(Client client, String ontology, String data, Injector injector, int delay, ConcurrentHashMap<Injector, InjectorStatus> statues) {
		this.client = client;
		this.ontology = ontology;
		this.data = data;
		this.injector = injector;
		this.delay = delay;
		start = new Date();
		startPeriod = start;
		this.statues = statues;
	}
	
	@Override
	public void run() {
		while(!stop) {
			synchronized (lock) {
				try {		
					sent++;
					sentPeriod++;
					Date ini = new Date();
					client.insertInstance(ontology, data);
					Date end = new Date();
					long time = end.getTime() - ini.getTime();
					timespent = timespent + time;
					timespentPeriod = timespentPeriod + time;
					
					updateStatus();
					
					//automatic stop after 10 minutes
					if ( 600000 < (end.getTime() - this.start.getTime()) ) {
						this.stop = true;
					}
					
				} catch (Exception e) {
					log.error("Error inserting data", e);
					errors++;
					errorsPeriod++;
				}
			}
			try {
				Thread.sleep(delay);
			} catch (InterruptedException e) {
				log.error("Error sleeping task");
			}
		}
		client.disconnect();
		
	}
	
	public void stop() {
		synchronized(lock) {
			this.stop = true;
		}
	}
	
	public boolean isStopped() {
		synchronized(lock) {
			return stop;
		}
	}
	
	private float getThroughput() {
		float time = timespent / 1000.0f;
		return (sent-errors) / time;
	}
	
	private long runningTime(Date now) {
		long time = now.getTime() - start.getTime();
		return time;
	}
	
	private void updateStatus() {
		Date now = new Date();
		long period = now.getTime() - startPeriod.getTime();
		if (periodLimit < period) {
			//push new status
			float time = timespentPeriod / 1000f;
			float throughputPeriod = (sentPeriod - errorsPeriod) / time;
			
			InjectorStatus status = new InjectorStatus(injector.getInjector(), sent, errors, getThroughput(), runningTime(now), throughputPeriod, client.getProtocol());
			statues.put(this.injector, status);
			
			//start a new Period
			startPeriod = new Date();
			sentPeriod = 0;
			errorsPeriod = 0;
			timespentPeriod = 0;
		} 	
	}
	
}
