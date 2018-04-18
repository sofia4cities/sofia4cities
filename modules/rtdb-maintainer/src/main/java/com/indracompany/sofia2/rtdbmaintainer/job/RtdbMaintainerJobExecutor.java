package com.indracompany.sofia2.rtdbmaintainer.job;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.indracompany.sofia2.scheduler.job.BatchGenericExecutor;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class RtdbMaintainerJobExecutor implements BatchGenericExecutor {

	@Autowired
	RtdbMaintainerJob rtdbMaintainerJob;

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		try {
			this.rtdbMaintainerJob.execute(context);
			log.info("Executed");
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
