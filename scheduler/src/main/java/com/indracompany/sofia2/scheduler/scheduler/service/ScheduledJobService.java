package com.indracompany.sofia2.scheduler.scheduler.service;

import java.util.List;

import com.indracompany.sofia2.scheduler.domain.ScheduledJob;

public interface ScheduledJobService {
	
	List<ScheduledJob> getAllScheduledJobs();

	List<ScheduledJob> getScheduledJobsByUsername(String username);
	
	ScheduledJob findByJobName (String jobName);
	
	void createScheduledJob (ScheduledJob job);

}
