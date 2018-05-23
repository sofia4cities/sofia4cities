package com.indracompany.sofia2.examples.scalability.msgs;

import java.util.Date;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.indracompany.sofia2.examples.scalability.Client;

import lombok.extern.slf4j.Slf4j;

@Component
@Scope("prototype")
@Slf4j
public class InsertionTask implements Runnable{
	
	private final Object lock = new Object();
	
	private final Client client;
	private final String ontology;
	private final String data;
	private final int injector;
	private final Date start;
	
	private volatile boolean stop = false;
	private volatile int sent = 0;
	private volatile int sentPeriod = 0;
	private volatile int errors = 0;
	private volatile int errorsPeriod = 0;
	private volatile long timespent;
	private volatile long timespentPeriod = 0;
	
	
	
	
	public InsertionTask(Client client, String ontology, String data, int injector) {
		this.client = client;
		this.ontology = ontology;
		this.data = data;
		this.injector = injector;
		start = new Date();
	}
	
	@Override
	public void run() {
		while(!stop) {
			synchronized (lock) {
				try {		
					sent++;
					Date ini = new Date();
					client.insertInstance(ontology, data);
					Date end = new Date();
					long time = end.getTime() - ini.getTime();
					timespent = timespent + time;											
				} catch (Exception e) {
					log.error("Error inserting data", e);
					errors++;
				}
			}
			try {
				Thread.sleep(100);
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
	
	private float getThroughput() {
		float time = timespent / 1000.0f;
		return (sent-errors) / time;
	}
	
	private long runningTime(Date now) {
		long time = now.getTime() - start.getTime();
		return time;
	}
	
	public InjectorStatus getStatus() {
		long timeSpentStatus;
		Date now = new Date();
		InjectorStatus status;
		synchronized (lock) {
			timeSpentStatus = timespent - timespentPeriod;
			float time = timeSpentStatus / 1000f;
			int sentThisPeriod = sent - sentPeriod;
			int errorThisPeriod = errors - errorsPeriod;
			float throughputPeriod = (sentThisPeriod - errorThisPeriod) / time;
			status = new InjectorStatus(injector, sent, errors, getThroughput(), runningTime(now), throughputPeriod, client.getProtocol());
			sentPeriod = sent;
			errorsPeriod = errors;
			timespentPeriod = timespent;
		}
		return status;
	}
	
}
