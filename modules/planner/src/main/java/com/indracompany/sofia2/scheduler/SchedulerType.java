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
package com.indracompany.sofia2.scheduler;

import com.indracompany.sofia2.scheduler.scheduler.instance.SchedulerNames;

public enum SchedulerType {

	Twitter(SchedulerNames.TWITTER_SCHEDULER_NAME), Script(SchedulerNames.SCRIPT_SCHEDULER_NAME), Simulation(
			SchedulerNames.SIMULATION_SCHEDULER_NAME);

	private String schedulerName;

	private SchedulerType(String schedulerName) {
		this.schedulerName = schedulerName;
	}

	public String getSchedulerName() {

		return schedulerName;
	}

	// Add fromString method to convert string to enum
	public static SchedulerType fromString(String input) {
		for (SchedulerType schedulerType : SchedulerType.values()) {
			if (schedulerType.schedulerName.equals(input)) {
				return schedulerType;
			}
		}
		return null;
	}

}
