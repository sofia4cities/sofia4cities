/**
 * Copyright Indra Sistemas, S.A.
 * 2013-2018 SPAIN
 *
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
package com.indracompany.sofia2.rtdbmaintainer.job;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.indracompany.sofia2.config.model.Ontology;
import com.indracompany.sofia2.config.services.ontology.OntologyService;
import com.indracompany.sofia2.persistence.services.ManageDBPersistenceServiceFacade;

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
	@Autowired
	private ManageDBPersistenceServiceFacade manageDBPersistenceServiceFacade;
	private final static int CORE_POOL_SIZE = 10;
	private final static int MAXIMUM_THREADS = 15;
	private final static long KEEP_ALIVE = 20;
	private final static long DEFAULT_TIMEOUT = 10;

	public void execute(JobExecutionContext context) throws InterruptedException {

		List<Ontology> ontologies = this.ontologyService.getCleanableOntologies();

		if (ontologies.size() > 0) {

			TimeUnit timeUnit = (TimeUnit) context.getJobDetail().getJobDataMap().get("timeUnit");
			long timeout = context.getJobDetail().getJobDataMap().getLongValue("timeout");
			if (timeout == 0)
				timeout = DEFAULT_TIMEOUT;

			BlockingQueue<Runnable> blockingQueue = new ArrayBlockingQueue<Runnable>(ontologies.size());
			ThreadPoolExecutor executor = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_THREADS, KEEP_ALIVE,
					TimeUnit.SECONDS, blockingQueue);
			executor.setRejectedExecutionHandler(new RejectedExecutionHandler() {

				@Override
				public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
					// TODO Auto-generated method stub

				}
			});
			//
			List<CompletableFuture<String>> futureList = ontologies.stream()
					.map(o -> CompletableFuture.supplyAsync(() -> {
						performExport(o);
						return "";
					}, executor)).collect(Collectors.toList());

			// List<String> results = futureList.stream().map((f) ->
			// f.join()).collect(Collectors.toList());

			CompletableFuture<Void> globalResut = CompletableFuture
					.allOf(futureList.toArray(new CompletableFuture[futureList.size()]));

			try {

				globalResut.get(timeout, timeUnit);

			} catch (ExecutionException | TimeoutException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	public void performExport(Ontology ontology) {
		manageDBPersistenceServiceFacade.exportToJson(ontology.getRtdbDatasource(), ontology.getIdentification(),
				System.currentTimeMillis() - ontology.getRtdbCleanLapse().getMilliseconds());
	}

}
