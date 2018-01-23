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
package com.indra.sofia2.support.util.quartz;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.SchedulerException;
import org.quartz.SchedulerListener;
import org.quartz.Trigger;
import org.quartz.TriggerKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.indra.sofia2.support.util.quartz.Sofia2BatchSchedulerImpl.TRIGGER_SUFFIX;

class QuartzSchedulerListener implements SchedulerListener {

	private static final Logger logger = LoggerFactory.getLogger(QuartzSchedulerListener.class);

	private List<Sofia2BatchSchedulerListener> listeners;

	public QuartzSchedulerListener() {
		this.listeners = new CopyOnWriteArrayList<Sofia2BatchSchedulerListener>();
	}

	public void addListener(Sofia2BatchSchedulerListener listener) {
		logger.info("Adding listener. ListenerId = {}.", listener.getId());
		this.listeners.add(listener);
	}

	public void removeListener(Sofia2BatchSchedulerListener listener) {
		logger.info("Removing listener. ListenerId = {}.", listener.getId());
		this.listeners.remove(listener);
	}

	private String getJobId(Trigger trigger) {
		return getJobId(trigger.getKey());
	}

	private String getJobId(TriggerKey triggerKey) {
		return triggerKey.getName().replace(TRIGGER_SUFFIX, "");
	}

	@Override
	public void jobScheduled(Trigger trigger) {
		String jobId = getJobId(trigger);
		try {
			for (Sofia2BatchSchedulerListener listener : listeners) {
				logger.debug("Notifying job scheduled event. ListenerId = {}.", listener.getId());
				listener.onJobScheduled(jobId);
			}
		} catch (Throwable e) {
			logger.error("An exception has been raised.", e);
		}
	}

	@Override
	public void jobUnscheduled(TriggerKey triggerKey) {
		String jobId = getJobId(triggerKey);
		try {
			for (Sofia2BatchSchedulerListener listener : listeners) {
				logger.debug("Notifying job unscheduled event. ListenerId = {}.", listener.getId());
				listener.onJobUnscheduled(jobId);
			}
		} catch (Throwable e) {
			logger.error("An exception has been raised.", e);
		}
	}

	@Override
	public void triggerFinalized(Trigger trigger) {
		String jobId = getJobId(trigger);
		try {
			for (Sofia2BatchSchedulerListener listener : listeners) {
				logger.debug("Notifying trigger finalized event. ListenerId = {}.", listener.getId());
				listener.onTriggerFinalized(jobId);
			}
		} catch (Throwable e) {
			logger.error("An exception has been raised.", e);
		}
	}

	@Override
	public void triggerPaused(TriggerKey triggerKey) {
		String jobId = getJobId(triggerKey);
		try {
			for (Sofia2BatchSchedulerListener listener : listeners) {
				logger.debug("Notifying trigger paused event. ListenerId = {}.", listener.getId());
				listener.onTriggerPaused(jobId);
			}
		} catch (Throwable e) {
			logger.error("An exception has been raised.", e);
		}
	}

	@Override
	public void triggersPaused(String triggerGroup) {
		try {
			for (Sofia2BatchSchedulerListener listener : listeners) {
				logger.debug("Notifying triggers paused event. ListenerId = {}.", listener.getId());
				listener.onTriggersPaused();
			}
		} catch (Throwable e) {
			logger.error("An exception has been raised.", e);
		}
	}

	@Override
	public void triggerResumed(TriggerKey triggerKey) {
		String jobId = getJobId(triggerKey);
		try {
			for (Sofia2BatchSchedulerListener listener : listeners) {
				logger.debug("Notifying trigger resumed event. ListenerId = {}.", listener.getId());
				listener.onTriggerResumed(jobId);
			}
		} catch (Throwable e) {
			logger.error("An exception has been raised.", e);
		}
	}

	@Override
	public void triggersResumed(String triggerGroup) {
		try {
			for (Sofia2BatchSchedulerListener listener : listeners) {
				logger.debug("Notifying triggers resumed event. ListenerId = {}.", listener.getId());
				listener.onTriggersResumed();
			}
		} catch (Throwable e) {
			logger.error("An exception has been raised.", e);
		}
	}

	@Override
	public void jobAdded(JobDetail jobDetail) {
		try {
			for (Sofia2BatchSchedulerListener listener : listeners) {
				logger.debug("Notifying job added event. ListenerId = {}.", listener.getId());
				listener.onJobAdded(jobDetail.getKey().getName());
			}
		} catch (Throwable e) {
			logger.error("An exception has been raised.", e);
		}
	}

	@Override
	public void jobDeleted(JobKey jobKey) {
		try {
			for (Sofia2BatchSchedulerListener listener : listeners) {
				logger.debug("Notifying job deleted event. ListenerId = {}.", listener.getId());
				listener.onJobDeleted(jobKey.getName());
			}
		} catch (Throwable e) {
			logger.error("An exception has been raised.", e);
		}
	}

	@Override
	public void jobPaused(JobKey jobKey) {
		try {
			for (Sofia2BatchSchedulerListener listener : listeners) {
				logger.debug("Notifying job paused event. ListenerId = {}.", listener.getId());
				listener.onJobPaused(jobKey.getName());
			}
		} catch (Throwable e) {
			logger.error("An exception has been raised.", e);
		}
	}

	@Override
	public void jobsPaused(String jobGroup) {
		try {
			for (Sofia2BatchSchedulerListener listener : listeners) {
				logger.debug("Notifying jobs paused event. ListenerId = {}.", listener.getId());
				listener.onJobsPaused();
			}
		} catch (Throwable e) {
			logger.error("An exception has been raised.", e);
		}
	}

	@Override
	public void jobResumed(JobKey jobKey) {
		try {
			for (Sofia2BatchSchedulerListener listener : listeners) {
				logger.debug("Notifying job resumed event. ListenerId = {}.", listener.getId());
				listener.onJobResumed(jobKey.getName());
			}
		} catch (Throwable e) {
			logger.error("An exception has been raised.", e);
		}
	}

	@Override
	public void jobsResumed(String jobGroup) {
		try {
			for (Sofia2BatchSchedulerListener listener : listeners) {
				logger.debug("Notifying jobs resumed event. ListenerId = {}.", listener.getId());
				listener.onJobsResumed();
			}
		} catch (Throwable e) {
			logger.error("An exception has been raised.", e);
		}
	}

	@Override
	public void schedulerError(String msg, SchedulerException cause) {}

	@Override
	public void schedulerInStandbyMode() {}

	@Override
	public void schedulerStarted() {}

	@Override
	public void schedulerStarting() {}

	@Override
	public void schedulerShutdown() {}

	@Override
	public void schedulerShuttingdown() {}

	@Override
	public void schedulingDataCleared() {}
}
