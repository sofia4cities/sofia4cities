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

		FlowDomain domain = domainService.createFlowDomain(domainIdentification, userService.getUser("developer"));

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
		node.setOntology(ontologyService.getOntologyByIdentification(ontologyId));
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
