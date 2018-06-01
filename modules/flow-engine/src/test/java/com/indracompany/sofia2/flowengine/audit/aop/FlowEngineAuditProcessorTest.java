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
package com.indracompany.sofia2.flowengine.audit.aop;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.util.Base64;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;

import com.indracompany.sofia2.audit.bean.Sofia2AuditError;
import com.indracompany.sofia2.audit.bean.Sofia2AuditEvent.EventType;
import com.indracompany.sofia2.audit.bean.Sofia2AuditEvent.Module;
import com.indracompany.sofia2.audit.bean.Sofia2AuditEvent.OperationType;
import com.indracompany.sofia2.audit.bean.Sofia2AuditEvent.ResultOperationType;
import com.indracompany.sofia2.commons.flow.engine.dto.FlowEngineDomain;
import com.indracompany.sofia2.config.model.FlowDomain;
import com.indracompany.sofia2.config.model.User;
import com.indracompany.sofia2.config.repository.FlowDomainRepository;
import com.indracompany.sofia2.flowengine.api.rest.pojo.DecodedAuthentication;
import com.indracompany.sofia2.flowengine.api.rest.service.FlowEngineValidationNodeService;
import com.indracompany.sofia2.flowengine.audit.bean.FlowEngineAuditEvent;
import com.indracompany.sofia2.flowengine.exception.NotAuthorizedException;
import com.indracompany.sofia2.router.service.app.model.OperationModel.QueryType;

@RunWith(MockitoJUnitRunner.class)
@SpringBootTest
public class FlowEngineAuditProcessorTest {

	@InjectMocks
	private FlowEngineAuditProcessor flowEngineAuditProcessor;

	@Mock
	private FlowDomainRepository domainRepository;

	@Mock
	private FlowEngineValidationNodeService flowEngineValidationNodeService;

	private final String DOMAIN_ID = "dummyDomainId";
	private final String USER_ID = "dummyUserId";
	private final String RESULT_OK = "OK";
	private final String RESULT_KO = "NOTOK";
	private final String ONTOLOGY_ID = "dummyOntologyId";
	private final String AUTHENTICATION = "user:pass";
	private final String INSTANCE = "{\"dummy\":\"dummyvalue\"}";

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
	}

	public User getTestUser() {
		User user = new User();
		user.setUserId(USER_ID);
		return user;
	}

	public FlowDomain getTestFlowDomain() {
		FlowDomain flowDomain = new FlowDomain();
		flowDomain.setActive(true);
		flowDomain.setIdentification(DOMAIN_ID);
		flowDomain.setUser(getTestUser());
		return flowDomain;
	}

	public DecodedAuthentication getDecodedAuthentication() {
		DecodedAuthentication decodedAuth = new DecodedAuthentication(
				Base64.getEncoder().encodeToString(AUTHENTICATION.getBytes()).toString());
		return decodedAuth;
	}

	@Test
	public void given_get_event_retVal_error() {

		String methodName = "startFlowEngineDomain";

		FlowEngineDomain domain = FlowEngineDomain.builder().domain(DOMAIN_ID).build();

		when(domainRepository.findByIdentification(DOMAIN_ID)).thenReturn(getTestFlowDomain());

		FlowEngineAuditEvent event = flowEngineAuditProcessor.getEvent(methodName, RESULT_KO, domain,
				OperationType.START);
		Assert.assertEquals(OperationType.START.name(), event.getOperationType());
		Assert.assertEquals(ResultOperationType.ERROR, event.getResultOperation());
		Assert.assertEquals(EventType.FLOWENGINE, event.getType());
		Assert.assertEquals(Module.FLOWENGINE, event.getModule());
		Assert.assertEquals(getTestUser().getUserId(), event.getUser());
		Assert.assertEquals(domain.getDomain(), event.getDomain());
		Assert.assertEquals(RESULT_KO, event.getMessage());

	}

	@Test
	public void given_get_event_retVal_ok() {

		String methodName = "startFlowEngineDomain";

		FlowEngineDomain domain = FlowEngineDomain.builder().domain(DOMAIN_ID).build();

		when(domainRepository.findByIdentification(DOMAIN_ID)).thenReturn(getTestFlowDomain());

		FlowEngineAuditEvent event = flowEngineAuditProcessor.getEvent(methodName, RESULT_OK, domain,
				OperationType.START);
		Assert.assertEquals(OperationType.START.name(), event.getOperationType());
		Assert.assertEquals(ResultOperationType.SUCCESS, event.getResultOperation());
		Assert.assertEquals(EventType.FLOWENGINE, event.getType());
		Assert.assertEquals(Module.FLOWENGINE, event.getModule());
		Assert.assertEquals(getTestUser().getUserId(), event.getUser());
		Assert.assertEquals(domain.getDomain(), event.getDomain());

	}

	@Test
	public void given_get_event_incorrect_credentials() {

		DecodedAuthentication decodedAuth = getDecodedAuthentication();

		when(flowEngineValidationNodeService.decodeAuth(AUTHENTICATION)).thenReturn(decodedAuth);

		when(flowEngineValidationNodeService.validateUserCredentials(decodedAuth.getUserId(),
				decodedAuth.getPassword())).thenThrow(new NotAuthorizedException(""));

		FlowEngineAuditEvent event = flowEngineAuditProcessor.getEvent(ONTOLOGY_ID, "", QueryType.NATIVE.name(), null,
				"message", AUTHENTICATION, OperationType.QUERY);

		Assert.assertNull(event);

	}

	@Test
	public void given_create_error_event() {
		Exception ex = new Exception();
		String message = "dummyMessage";

		Sofia2AuditError event = flowEngineAuditProcessor.createErrorEvent(USER_ID, message, ex);

		assertEquals(EventType.ERROR, event.getType());
		Assert.assertNotNull(event.getMessage());
		assertEquals(ex, event.getEx());
		assertEquals(Module.FLOWENGINE, event.getModule());
		assertEquals(USER_ID, event.getUser());

	}

	@Test
	public void given_get_error_event_incorrect_credentials() {

		DecodedAuthentication decodedAuth = getDecodedAuthentication();

		when(flowEngineValidationNodeService.decodeAuth(AUTHENTICATION)).thenReturn(decodedAuth);

		when(flowEngineValidationNodeService.validateUserCredentials(decodedAuth.getUserId(),
				decodedAuth.getPassword())).thenThrow(new NotAuthorizedException(""));

		Sofia2AuditError error = flowEngineAuditProcessor.getErrorEvent("", AUTHENTICATION, new Exception());

		Assert.assertNull(error);
	}

	@Test
	public void given_get_error_event() {
		Exception ex = new Exception();
		String methodName = "dummyMethod";
		FlowEngineDomain domain = FlowEngineDomain.builder().domain(DOMAIN_ID).build();

		FlowDomain flowDomain = new FlowDomain();
		flowDomain.setUser(getTestUser());

		when(domainRepository.findByIdentification(DOMAIN_ID)).thenReturn(flowDomain);

		Sofia2AuditError event = flowEngineAuditProcessor.getErrorEvent(methodName, domain, ex);

		assertEquals(EventType.ERROR, event.getType());
		Assert.assertNotNull(event.getMessage());
		assertEquals(ex, event.getEx());
		assertEquals(Module.FLOWENGINE, event.getModule());
		assertEquals(USER_ID, event.getUser());
	}

	@Test
	public void given_get_query_event() {

		DecodedAuthentication decodedAuth = getDecodedAuthentication();
		String query = "";

		when(flowEngineValidationNodeService.decodeAuth(AUTHENTICATION)).thenReturn(decodedAuth);

		when(flowEngineValidationNodeService.validateUserCredentials(decodedAuth.getUserId(),
				decodedAuth.getPassword())).thenReturn(getTestUser());

		FlowEngineAuditEvent event = flowEngineAuditProcessor.getQueryEvent(ONTOLOGY_ID, query, QueryType.NATIVE.name(),
				"", AUTHENTICATION);

		assertEquals(EventType.FLOWENGINE, event.getType());
		Assert.assertNull(event.getData());
		assertEquals(ONTOLOGY_ID, event.getOntology());
		assertEquals(query, event.getQuery());
		assertEquals(Module.FLOWENGINE, event.getModule());
		assertEquals(OperationType.QUERY.name(), event.getOperationType());
		assertEquals(USER_ID, event.getUser());
	}

	@Test
	public void given_get_insert_event() {

		DecodedAuthentication decodedAuth = getDecodedAuthentication();

		when(flowEngineValidationNodeService.decodeAuth(AUTHENTICATION)).thenReturn(decodedAuth);

		when(flowEngineValidationNodeService.validateUserCredentials(decodedAuth.getUserId(),
				decodedAuth.getPassword())).thenReturn(getTestUser());

		FlowEngineAuditEvent event = flowEngineAuditProcessor.getInsertEvent(ONTOLOGY_ID, INSTANCE, "", AUTHENTICATION);

		assertEquals(EventType.FLOWENGINE, event.getType());
		assertEquals(ONTOLOGY_ID, event.getOntology());
		assertEquals(INSTANCE, event.getData());
		Assert.assertNull(event.getQuery());
		assertEquals(Module.FLOWENGINE, event.getModule());
		assertEquals(OperationType.INSERT.name(), event.getOperationType());
		assertEquals(USER_ID, event.getUser());
	}
}
