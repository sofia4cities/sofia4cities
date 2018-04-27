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

import static org.mockito.Mockito.when;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;

import com.indracompany.sofia2.audit.bean.Sofia2AuditEvent.EventType;
import com.indracompany.sofia2.audit.bean.Sofia2AuditEvent.Module;
import com.indracompany.sofia2.audit.bean.Sofia2AuditEvent.OperationType;
import com.indracompany.sofia2.audit.bean.Sofia2AuditEvent.ResultOperationType;
import com.indracompany.sofia2.commons.flow.engine.dto.FlowEngineDomain;
import com.indracompany.sofia2.config.model.FlowDomain;
import com.indracompany.sofia2.config.model.User;
import com.indracompany.sofia2.config.repository.FlowDomainRepository;
import com.indracompany.sofia2.flowengine.api.rest.service.FlowEngineValidationNodeService;
import com.indracompany.sofia2.flowengine.audit.bean.FlowEngineAuditEvent;

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

}
