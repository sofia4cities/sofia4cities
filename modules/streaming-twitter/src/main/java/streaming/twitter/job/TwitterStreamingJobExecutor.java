package streaming.twitter.job;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import com.indracompany.sofia2.scheduler.job.BatchGenericExecutor;

@Service
public class TwitterStreamingJobExecutor implements BatchGenericExecutor{

	@Autowired
	private TwitterStreamingJob twitterStreamingJob;
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		
		try {
			twitterStreamingJob.execute(context);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
