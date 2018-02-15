package com.indracompany.sofia2.example.config.scheduler;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.quartz.JobExecutionContext;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class TwitterStreamingJob {
	
	public void execute (JobExecutionContext context) {
		
		String id = context.getJobDetail().getJobDataMap().getString("id");
		String ontology = context.getJobDetail().getJobDataMap().getString("ontology");
		String kp = context.getJobDetail().getJobDataMap().getString("clientPlatform");
		String token = context.getJobDetail().getJobDataMap().getString("token");
		String topics = context.getJobDetail().getJobDataMap().getString("topics");
		boolean geolocation = context.getJobDetail().getJobDataMap().getBoolean("geolocation");
		int timeout = context.getJobDetail().getJobDataMap().getInt("timeout");
		String config=context.getJobDetail().getJobDataMap().getString("configuration");
		List<String> keywords=getKeywordsForListener(topics);
	
		
		//now suscribe
	}

	private List<String> getKeywordsForListener(String topics) {
		try {
			topics = new String(topics.getBytes("iso-8859-1"), "utf8");
		 } catch (UnsupportedEncodingException e) {
			 log.debug("Problem decodifying keywords");
		 }
		 List<String> arrayKeywords=new ArrayList<String>();
		 StringTokenizer st=new StringTokenizer(topics, ",");
	     while(st.hasMoreTokens()) {
			 arrayKeywords.add(st.nextToken().trim());
		 }
	     return arrayKeywords;
	}

}
