package com.indracompany.sofia2.examples.scalability.msgs;

import lombok.Getter;

public class InjectorStatus {

	@Getter private int id;
	@Getter private int sent;
	@Getter private int errors;
	@Getter private float throughput;
	@Getter private long timeAlive;
	@Getter private float throughputPeriod;
	@Getter private String protocol;
	
	public InjectorStatus(int id, int sent, int errors, float throughput, long timeAlive, float throughputPeriod, String protocol){
		this.id = id;
		this.sent = sent;
		this.errors = errors;
		this.throughput = throughput;
		this.timeAlive = timeAlive;
		this.throughputPeriod = throughputPeriod;
		this.protocol = protocol;
	}
}
