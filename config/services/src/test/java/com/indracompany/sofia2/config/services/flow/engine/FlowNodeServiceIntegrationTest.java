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
package com.indracompany.sofia2.config.services.flow.engine;

import java.util.List;
import java.util.UUID;

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
import com.indracompany.sofia2.config.model.FlowNode.MessageType;
import com.indracompany.sofia2.config.model.FlowNode.Type;
import com.indracompany.sofia2.config.model.NotificationEntity;
import com.indracompany.sofia2.config.model.User;
import com.indracompany.sofia2.config.services.flow.FlowService;
import com.indracompany.sofia2.config.services.flowdomain.FlowDomainService;
import com.indracompany.sofia2.config.services.flownode.FlowNodeService;
import com.indracompany.sofia2.config.services.ontology.OntologyService;
import com.indracompany.sofia2.config.services.user.UserService;

import lombok.extern.slf4j.Slf4j;

@RunWith(SpringRunner.class)
@SpringBootTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@Slf4j
public class FlowNodeServiceIntegrationTest {

	@Autowired
	private FlowDomainService domainService;

	@Autowired
	private FlowService flowService;

	@Autowired
	private FlowNodeService nodeService;

	@Autowired
	private UserService userService;

	@Autowired
	private OntologyService ontologyService;

	private String domainIdentification;
	private String ontologyId;

	@Before
	public void setUp() {
		// Create one domain, flow and notificator node
		ontologyId = "OntologyTest";
		domainIdentification = "DomainTest_" + UUID.randomUUID().toString().substring(0, 30);

		User user = userService.getUser("developer");
		FlowDomain domain = domainService.createFlowDomain(domainIdentification, user);

		Flow flow = new Flow();
		flow.setActive(true);
		flow.setIdentification("Test Flow 1");
		flow.setFlowDomain(domain);
		flow.setNodeRedFlowId("nodeRedFlowId");

		flowService.createFlow(flow);

		// Create Node with properties

		FlowNode node = new FlowNode();
		node.setFlow(flow);
		node.setNodeRedNodeId("nodeRedNodeId");
		node.setFlowNodeType(Type.HTTP_NOTIFIER);
		node.setMessageType(MessageType.INSERT);
		node.setOntology(ontologyService.getOntologyByIdentification(ontologyId, user.getUserId()));
		node.setPartialUrl("/notificationPoint");
		nodeService.createFlowNode(node);
	}

	@Test
	public void test1_getNotificationEntities() {
		List<NotificationEntity> notificationEntities = nodeService.getNotificationsByOntologyAndMessageType(ontologyId,
				"INSERT");
		Assert.assertTrue(notificationEntities != null && !notificationEntities.isEmpty());
	}

	@After
	public void cleanUp() {
		domainService.deleteFlowdomain(this.domainIdentification);
	}
}
