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
package com.indracompany.sofia2.flowengine.api.rest.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.indracompany.sofia2.flowengine.api.rest.service.FlowEngineService;
import com.indracompany.sofia2.flowengine.nodered.communication.NodeRedAdminClient;
import com.indracompany.sofia2.flowengine.nodered.communication.dto.FlowEngineAdminResponse;
import com.indracompany.sofia2.flowengine.nodered.communication.dto.FlowEngineDomainStatus;

@Service
public class FlowEngineServiceImpl implements FlowEngineService {

	@Autowired
	private NodeRedAdminClient nodeRedClientAdmin;

	@Override
	public String test() {
		// TODO Auto-generated method stub
		List<String> dominios = new ArrayList<>();
		dominios.add("DominioTest");
		FlowEngineAdminResponse domainsStatus = nodeRedClientAdmin.getFlowEngineDomainStatus(dominios);
		List<FlowEngineDomainStatus> domains2Sync;
		try {
			domains2Sync = (List<FlowEngineDomainStatus>) FlowEngineDomainStatus
					.fromJsonArrayToDomainStatus(domainsStatus.getBody());
			nodeRedClientAdmin.synchronizeMF(domains2Sync);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		nodeRedClientAdmin.getAllFlowEnginesDomains();
		nodeRedClientAdmin.startFlowEngineDomain("DominioTest", 8000, "/tmp/testUser/", 7000);
		nodeRedClientAdmin.stopFlowEngineDomain("DominioTest");
		nodeRedClientAdmin.getFlowEngineDomain("DominioTest");
		nodeRedClientAdmin.deleteFlowEngineDomain("DominioTest");
		nodeRedClientAdmin.createFlowengineDomain("DominioTest", 8000, "/tmp/testUser/", 7000);
		return "ok";
	}

}
