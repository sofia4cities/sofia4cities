package com.indracompany.sofia2.rtdbmaintainer.job;

import java.util.Date;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.quartz.Calendar;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.Scheduler;
import org.quartz.Trigger;
import org.quartz.TriggerKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import lombok.extern.slf4j.Slf4j;

@RunWith(SpringRunner.class)
@SpringBootTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@Slf4j
public class JobTest {

	@Autowired
	RtdbMaintainerJob job;

	@Test
	public void test_execution() throws InterruptedException {
		this.job.execute(new JobExecutionContext() {

			@Override
			public void setResult(Object arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void put(Object arg0, Object arg1) {
				// TODO Auto-generated method stub

			}

			@Override
			public boolean isRecovering() {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public Trigger getTrigger() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Scheduler getScheduler() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Date getScheduledFireTime() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Object getResult() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public int getRefireCount() {
				// TODO Auto-generated method stub
				return 0;
			}

			@Override
			public TriggerKey getRecoveringTriggerKey() throws IllegalStateException {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Date getPreviousFireTime() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Date getNextFireTime() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public JobDataMap getMergedJobDataMap() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public long getJobRunTime() {
				// TODO Auto-generated method stub
				return 0;
			}

			@Override
			public Job getJobInstance() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public JobDetail getJobDetail() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Date getFireTime() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public String getFireInstanceId() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Calendar getCalendar() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Object get(Object arg0) {
				// TODO Auto-generated method stub
				return null;
			}
		});
		// try {
		// Thread.sleep(1000);
		// } catch (InterruptedException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
	}

}
