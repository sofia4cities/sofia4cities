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
package com.indracompany.sofia2.config.repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.indracompany.sofia2.config.model.Flow;
import com.indracompany.sofia2.config.model.FlowDomain;
import com.indracompany.sofia2.config.model.FlowNode;
import com.indracompany.sofia2.config.model.FlowNodeProperties;
import com.indracompany.sofia2.config.model.FlowNodeType;

@RunWith(SpringRunner.class)
@SpringBootTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class FlowIntegrationTest {

	@Autowired
	private FlowRepository flowRepository;
	@Autowired
	private FlowDomainRepository domainRepository;

	@Autowired
	private FlowNodeRepository nodeRepository;

	@Autowired
	private FlowNodeTypeRepository nodeTypeRepository;

	@Autowired
	private UserRepository userRepository;

	@Before
	public void setUp() {

	}

	@After
	public void cleanUp() {

	}

	@Test
	public void test1_getAllFlowsFromDomain() {
		if (domainRepository.findByIdentification("DominioTest") == null) {
			FlowDomain domain = new FlowDomain();
			domain.setIdentification("DominioTest");
			domain.setHome("/tmp/usuario");
			domain.setPort(8003);
			domain.setServicePort(7003);
			domain.setState("STOP");
			domain.setDemoMode(false);
			domain.setActive(true);
			domain.setUser(userRepository.findByUserId("collaborator"));
			domainRepository.save(domain);
		}
		if (flowRepository.findByFlowDomain_Identification("DominioTest").size() == 0) {
			Flow flow = new Flow();
			flow.setActive(true);
			flow.setFlowDomain(domainRepository.findByIdentification("DominioTest"));
			flow.setIdentification("Flow Test");
			flow.setNodeRedFlowId("Internal Id Flow 2");
			flowRepository.save(flow);
		}
		Assert.assertTrue(flowRepository.findByFlowDomain_Identification("DominioTest").size() > 0);
	}

	@Test
	public void test2_getAllNodesFlow() {
		List<Flow> flows = flowRepository.findByIdentification("Flow Test");
		List<FlowNode> nodes = nodeRepository.findByflowNodeType_Identification("script-topic");
		if (nodes.isEmpty()) {
			// Insert one node
			FlowNode node = new FlowNode();
			node.setFlow(flows.get(0));
			node.setNodeRedNodeId("internal NodeRed ID_2");
			node.setFlowNodeType(nodeTypeRepository.findByIdentification("script-topic"));
			Map<String, FlowNodeProperties> flowNodeProperties = new HashMap<>();
			FlowNodeProperties property = FlowNodeProperties.builder().name("name").value("nombre 1").flowNode(node)
					.build();
			flowNodeProperties.put("name", property);

			property = FlowNodeProperties.builder().name("direccion").value("direccion 1").flowNode(node).build();
			flowNodeProperties.put("direccion", property);

			property = FlowNodeProperties.builder().name("tipomensaje").value("tipomensaje 1").flowNode(node).build();
			flowNodeProperties.put("tipomensaje", property);

			property = FlowNodeProperties.builder().name("ontology").value("ontology 1").flowNode(node).build();
			flowNodeProperties.put("ontology", property);

			property = FlowNodeProperties.builder().name("kp").value("kp 1").flowNode(node).build();
			flowNodeProperties.put("kp", property);

			property = FlowNodeProperties.builder().name("kpInstance").value("kpInstance 1").flowNode(node).build();
			flowNodeProperties.put("kpInstance", property);
			node.setFlowNodeProperties(flowNodeProperties);
			nodeRepository.save(node);
		} else {
			// update the node properties
			FlowNode node = nodes.get(0);
			FlowNodeProperties property = node.getFlowNodeProperties().get("name");
			property.setValue("nuevo nombre");

			nodeRepository.save(node);
		}
		List<FlowNode> ssapNodes = nodeRepository.findByflowNodeType_Identification("script-topic");
		Assert.assertTrue(ssapNodes.size() > 0);
	}

	@Test
	public void test3_generateFlowNodeTypes() {

		FlowNodeType type = new FlowNodeType();
		type.setIdentification("script-topic");
		nodeTypeRepository.save(type);

		Assert.assertTrue(true);
	}
}
