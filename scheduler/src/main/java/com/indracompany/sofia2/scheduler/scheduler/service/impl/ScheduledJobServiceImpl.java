package com.indracompany.sofia2.scheduler.scheduler.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.indracompany.sofia2.scheduler.domain.ScheduledJob;
import com.indracompany.sofia2.scheduler.repository.ScheduledJobRepository;
import com.indracompany.sofia2.scheduler.scheduler.service.ScheduledJobService;

@Service
public class ScheduledJobServiceImpl implements ScheduledJobService{
	
	@Autowired
	private ScheduledJobRepository scheduledJobRepository;

	@Override
	public List<ScheduledJob> getAllScheduledJobs() {
		return scheduledJobRepository.findAll();
	}

	@Override
	public List<ScheduledJob> getScheduledJobsByUsername(String username) {
		return scheduledJobRepository.findAllByUserId(username);
	}

	@Override
	public void createScheduledJob(ScheduledJob job) {
		scheduledJobRepository.save(job);
	}

	@Override
	public ScheduledJob findByJobName(String jobName) {
		return scheduledJobRepository.findByJobName(jobName);
	}
	
	
	
}
