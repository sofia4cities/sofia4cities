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
package com.indracompany.sofia2.config.services.flow;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.indracompany.sofia2.config.model.Flow;
import com.indracompany.sofia2.config.repository.FlowRepository;
import com.indracompany.sofia2.config.services.exceptions.FlowServiceException;

@Service
public class FlowServiceImpl implements FlowService {

	@Autowired
	private FlowRepository flowRepository;

	@Override
	public List<Flow> getFlowByDomain(String domainIdentification) {
		return flowRepository.findByFlowDomain_Identification(domainIdentification);
	}

	@Override
	public Flow createFlow(Flow flow) {
		Flow result = flowRepository.findByNodeRedFlowId(flow.getNodeRedFlowId());
		if (result == null) {
			return flowRepository.save(flow);
		} else {
			throw new FlowServiceException("Flow already exists");
		}
	}

	@Override
	public Flow getFlowByNodeRedFlowId(String nodeRedFlowId) {
		return flowRepository.findByNodeRedFlowId(nodeRedFlowId);
	}

}
