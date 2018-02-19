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
package com.indracompany.sofia2.config.services.flowdomain;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.indracompany.sofia2.config.model.Flow;
import com.indracompany.sofia2.config.model.FlowDomain;
import com.indracompany.sofia2.config.model.FlowNode;
import com.indracompany.sofia2.config.repository.FlowDomainRepository;
import com.indracompany.sofia2.config.repository.FlowNodeRepository;
import com.indracompany.sofia2.config.repository.FlowRepository;

@Service
public class FlowDomainServiceImpl implements FlowDomainService {

	@Autowired
	public FlowDomainRepository domainRepository;

	@Autowired
	private FlowRepository flowRepository;

	@Autowired
	private FlowNodeRepository nodeRepository;

	@Override
	public FlowDomain getFlowDomainByUser(String user) {
		return domainRepository.findByUser_userId(user);
	}

	@Override
	public void deleteFlowDomainFlows(String domainIdentification) {
		FlowDomain domain = domainRepository.findByIdentification(domainIdentification);
		// Delete all data from this Domain,
		// including flows, nodes and properties
		List<Flow> flows = flowRepository.findByFlowDomain_Identification(domain.getIdentification());
		for (Flow flow : flows) {
			List<FlowNode> nodes = nodeRepository.findByFlow_NodeRedFlowId(flow.getNodeRedFlowId());
			for (FlowNode node : nodes) {
				nodeRepository.delete(node);
			}
			flowRepository.delete(flow);
		}
	}

	@Override
	public void deleteFlowdomain(String domainIdentification) {
		this.deleteFlowDomainFlows(domainIdentification);
		domainRepository.deleteByIdentification(domainIdentification);
	}

	@Override
	public FlowDomain getFlowDomainByIdentification(String identification) {
		return this.domainRepository.findByIdentification(identification);
	}

}
