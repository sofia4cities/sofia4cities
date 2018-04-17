package com.indracompany.sofia2.rtdbmaintainer.job;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.indracompany.sofia2.config.model.Ontology;
import com.indracompany.sofia2.config.services.ontology.OntologyService;
import com.indracompany.sofia2.persistence.services.BasicOpsPersistenceServiceFacade;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class RtdbMaintainerJob {

	@Autowired
	private OntologyService ontologyService;
	@Autowired
	private BasicOpsPersistenceServiceFacade basicOpsDBService;
	private final static int CORE_POOL_SIZE = 10;
	private final static int MAXIMUM_THREADS = 15;
	private final static long KEEP_ALIVE = 20;

	public void execute(JobExecutionContext context) {

		List<Ontology> ontologies = this.ontologyService.getCleanableOntologies();

		BlockingQueue<Runnable> blockingQueue = new ArrayBlockingQueue<Runnable>(ontologies.size());

		RtdbMaintainerThreadPoolExecutor executor = new RtdbMaintainerThreadPoolExecutor(CORE_POOL_SIZE,
				MAXIMUM_THREADS, KEEP_ALIVE, TimeUnit.SECONDS, blockingQueue);

		executor.setRejectedExecutionHandler(new RejectedExecutionHandler() {

			@Override
			public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
				// TODO Auto-generated method stub

			}
		});

		for (Ontology ontology : ontologies) {
			executor.execute(new RtdbMaintainerThread(ontology));
		}
	}

	public class RtdbMaintainerThread implements Runnable {

		private Ontology ontology;

		public RtdbMaintainerThread(Ontology ontology) {
			this.ontology = ontology;
		}

		@Override
		public void run() {
			System.out.println(basicOpsDBService.count(this.ontology.getIdentification()));
		}

	}

	public class RtdbMaintainerThreadPoolExecutor extends ThreadPoolExecutor {

		public RtdbMaintainerThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime,
				TimeUnit unit, BlockingQueue<Runnable> workQueue) {
			super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
		}

	}

}
