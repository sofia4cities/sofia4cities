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
package com.indracompany.sofia2.flowengine.nodered.sync;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.indracompany.sofia2.commons.flow.engine.dto.FlowEngineDomainStatus;
import com.indracompany.sofia2.config.model.FlowDomain;
import com.indracompany.sofia2.config.repository.FlowDomainRepository;
import com.indracompany.sofia2.flowengine.nodered.communication.NodeRedAdminClient;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class NodeRedDomainSyncMonitorImpl implements NodeRedDomainSyncMonitor, Runnable {

	@Value("${sofia2.flowengine.sync.monitor.interval.sec:30}")
	private int monitorInterval;
	@Value("${sofia2.flowengine.sync.monitor.initial.delay.sec:20}")
	private int initialSyncDelay;

	@Autowired
	private FlowDomainRepository domainRepository;

	@Autowired
	private NodeRedAdminClient nodeRedAdminClient;

	private ScheduledExecutorService monitor;

	@Override
	public void startMonitor() {
		monitor = Executors.newScheduledThreadPool(1);

		// Al arrancar sincroniza la BD con lo que hay en el motor de flujos
		monitor.schedule(new Runnable() {
			@Override
			public void run() {
				try {
					nodeRedAdminClient.synchronizeMF(getAllCdbDomains());
				} catch (Exception e) {
					log.error("Unable to sync CDB domains with NodeRedAdmin process. Cause = {}, message = {}.",
							e.getCause(), e.getMessage());
				}

			}
		}, this.initialSyncDelay, TimeUnit.SECONDS);

		// Programa el chequeo periodico
		monitor.scheduleAtFixedRate(this, this.monitorInterval, this.monitorInterval, TimeUnit.SECONDS);
	}

	@Override
	public void stopMonitor() {
		if (null != monitor) {
			monitor.shutdown();
		}
	}

	@Override
	public void run() {

		try {
			List<FlowEngineDomainStatus> domainStatusList = nodeRedAdminClient.getAllFlowEnginesDomains();
			if (domainStatusList != null) {
				for (FlowEngineDomainStatus domainStatus : domainStatusList) {
					FlowDomain domain = domainRepository.findByIdentification(domainStatus.getDomain());
					if (domain == null) {
						log.warn(
								"Domain {} not found in CDB. Request for deletion will be asked to NodeRedAdminClient.",
								domainStatus.getDomain());
						nodeRedAdminClient.deleteFlowEngineDomain(domainStatus.getDomain());
					}
				}
			} else {
				log.error("Unable to retrieve domain's statuses.");
			}
		} catch (Exception e) {
			log.error("Unable to retrieve domain's statuses. Cause = {}, message = {}", e.getCause(), e.getClass());
		}
	}

	private List<FlowEngineDomainStatus> getAllCdbDomains() {
		List<FlowEngineDomainStatus> domainsToSync = new ArrayList<>();
		List<FlowDomain> domainList = domainRepository.findAll();
		for (FlowDomain domain : domainList) {
			domainsToSync.add(FlowEngineDomainStatus.builder().domain(domain.getIdentification()).home(domain.getHome())
					.port(domain.getPort()).servicePort(domain.getServicePort()).state(domain.getState()).build());
		}
		return domainsToSync;
	}
}
