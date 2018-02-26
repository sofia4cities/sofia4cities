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
package com.indracompany.sofia2.libraries.flow.engine;

import java.util.List;

import org.springframework.web.bind.annotation.RequestParam;

import com.indracompany.sofia2.commons.flow.engine.dto.FlowEngineDomain;
import com.indracompany.sofia2.commons.flow.engine.dto.FlowEngineDomainStatus;

public interface FlowEngineService {

	public void stopFlowEngineDomain(String domain);

	public void startFlowEngineDomain(FlowEngineDomain domain);

	public void createFlowengineDomain(FlowEngineDomain domain);

	public void deleteFlowEngineDomain(String domainId);

	public FlowEngineDomain getFlowEngineDomain(@RequestParam String domainId);

	public List<FlowEngineDomainStatus> getAllFlowEnginesDomains();

	public List<FlowEngineDomainStatus> getFlowEngineDomainStatus(List<String> domainList);

}
