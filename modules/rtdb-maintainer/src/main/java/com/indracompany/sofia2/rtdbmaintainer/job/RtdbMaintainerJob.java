package com.indracompany.sofia2.rtdbmaintainer.job;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.indracompany.sofia2.commons.OSDetector;
import com.indracompany.sofia2.config.model.Ontology;
import com.indracompany.sofia2.config.services.ontology.OntologyService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class RtdbMaintainerJob {
	@Value("${sofia2.database.elasticsearch.database:sofia2_s4c_es}")
	private String SOFIA2_DB_ES;
	@Value("${sofia2.database.mongodb.database:sofia2_s4c}")
	private String SOFIA2_DB_MONGO;
	@Autowired
	private OntologyService ontologyService;

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
			String command = null;
			SimpleDateFormat format = new SimpleDateFormat("yyyy-dd-MM");
			Runtime r = Runtime.getRuntime();

			long startDateMillis = System.currentTimeMillis(); // - this.ontology.getRtdbCleanLapse().getMilliseconds();

			if (OSDetector.isWindows()) {
				String query = "--query {'contextData.timestampMillis':{$lt:" + startDateMillis + "}}";
				if (ontology.getRtdbDatasource().name().equals("Mongo")) {
					command = "s:/tools/mongo/bin/mongoexport --db " + SOFIA2_DB_MONGO + " --collection "
							+ ontology.getIdentification() + " " + query + " --out s:\\tools\\mongo\\export\\"
							+ ontology.getIdentification() + format.format(new Date()) + ".json";
					System.out.println(command);
				} else {
					// elasticsearch cmd windows
				}

			} else {
				if (ontology.getRtdbDatasource().name().equals("Mongo")) {
					command = "mongoexport --db " + SOFIA2_DB_MONGO + " --collection " + ontology.getIdentification()
							+ " --out /tmp/mongo/export" + ontology.getIdentification() + format.format(new Date())
							+ ".json";

				} else {
					// elasticsearch cmd linux
				}

			}
			if (command != null)
				try {
					r.exec(command);
				} catch (IOException e) {
					e.printStackTrace();
				}

		}

	}

	public class RtdbMaintainerThreadPoolExecutor extends ThreadPoolExecutor {

		public RtdbMaintainerThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime,
				TimeUnit unit, BlockingQueue<Runnable> workQueue) {
			super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
		}

	}

}
