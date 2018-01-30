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
