package com.indracompany.sofia2.scheduler.library;

import java.util.Map;
import java.util.TreeMap;

public enum BasicSchedulerType implements SchedulerType{
	
	Twitter("twitterScheduler"), Script ("scriptScheduler");
	
	private String schedulerName;
	
	private static Map<String, SchedulerType> map = new TreeMap < String, SchedulerType > ();
	
	private BasicSchedulerType (String schedulerName) {
		this.schedulerName = schedulerName;
	}
	
	static {
	    for (SchedulerType schedulerType : values()) {
	      map.put(schedulerType.getSchedulerName(), schedulerType);
	    }
	}

	@Override
	public String getSchedulerName() {
		return schedulerName;
	}
	
	public static void addNewSchedulerType(SchedulerType schedulerType) {
	    if (!map.containsKey(schedulerType.getSchedulerName())) {
	      map.put(schedulerType.getSchedulerName(), schedulerType);
	    }
	}
	
	public static SchedulerType schedulerTypeFor(String schedulerName) {
		return map.get(schedulerName);
	}

}
