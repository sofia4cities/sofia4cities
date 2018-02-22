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
package com.indracompany.sofia2.streaming.twitter.job;

import org.quartz.InterruptableJob;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.UnableToInterruptJobException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import com.indracompany.sofia2.scheduler.job.BatchGenericExecutor;
import com.indracompany.sofia2.streaming.twitter.listener.TwitterStreamListener;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class TwitterStreamingJobExecutor implements BatchGenericExecutor, InterruptableJob {

	@Autowired
	private TwitterStreamingJob twitterStreamingJob;
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		
		try {
			log.info("Executed job");
			//twitterStreamingJob.execute(context);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	@Override
	public void interrupt() throws UnableToInterruptJobException {
		
		twitterStreamingJob.destroy();
	}

}
