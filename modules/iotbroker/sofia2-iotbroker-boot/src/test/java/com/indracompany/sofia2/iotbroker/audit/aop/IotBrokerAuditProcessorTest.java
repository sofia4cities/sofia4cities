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
package com.indracompany.sofia2.iotbroker.audit.aop;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;

import com.indracompany.sofia2.audit.bean.AuditConst;
import com.indracompany.sofia2.audit.bean.Sofia2AuditError;
import com.indracompany.sofia2.audit.bean.Sofia2AuditEvent.EventType;
import com.indracompany.sofia2.audit.bean.Sofia2AuditEvent.Module;
import com.indracompany.sofia2.audit.bean.Sofia2AuditEvent.ResultOperationType;
import com.indracompany.sofia2.config.model.IoTSession;
import com.indracompany.sofia2.iotbroker.audit.bean.IotBrokerAuditEvent;
import com.indracompany.sofia2.iotbroker.audit.bean.IotBrokerAuditEventFactory;
import com.indracompany.sofia2.iotbroker.audit.processor.DeleteAuditProcessor;
import com.indracompany.sofia2.iotbroker.audit.processor.InsertAuditProcessor;
import com.indracompany.sofia2.iotbroker.audit.processor.IotBrokerAuditProcessor;
import com.indracompany.sofia2.iotbroker.audit.processor.JoinAuditProcessor;
import com.indracompany.sofia2.iotbroker.audit.processor.LeaveAuditProcessor;
import com.indracompany.sofia2.iotbroker.audit.processor.QueryAuditProcessor;
import com.indracompany.sofia2.iotbroker.audit.processor.SubscribeAuditProcessor;
import com.indracompany.sofia2.iotbroker.audit.processor.UnsubscribeAuditProcessor;
import com.indracompany.sofia2.iotbroker.audit.processor.UpdateAuditProcessor;
import com.indracompany.sofia2.iotbroker.common.exception.SSAPAuditProcessorException;
import com.indracompany.sofia2.iotbroker.mock.pojo.PojoGenerator;
import com.indracompany.sofia2.iotbroker.plugable.impl.security.SecurityPluginManager;
import com.indracompany.sofia2.iotbroker.plugable.interfaces.gateway.GatewayInfo;
import com.indracompany.sofia2.ssap.SSAPMessage;
import com.indracompany.sofia2.ssap.body.SSAPBodyInsertMessage;
import com.indracompany.sofia2.ssap.body.SSAPBodyReturnMessage;
import com.indracompany.sofia2.ssap.enums.SSAPMessageDirection;
import com.indracompany.sofia2.ssap.enums.SSAPMessageTypes;

@RunWith(MockitoJUnitRunner.class)
@SpringBootTest
public class IotBrokerAuditProcessorTest {

	@InjectMocks
	private IotBrokerAuditProcessor iotBrokerAuditProcessor;

	@Mock
	private SecurityPluginManager securityPluginManager;

	@Mock
	private List<MessageAuditProcessor> processors;

	private final List<MessageAuditProcessor> auditProcessors = Arrays.asList(new DeleteAuditProcessor(),
			new InsertAuditProcessor(), new JoinAuditProcessor(), new LeaveAuditProcessor(), new QueryAuditProcessor(),
			new SubscribeAuditProcessor(), new UnsubscribeAuditProcessor(), new UpdateAuditProcessor());

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
	}

	@Test(expected = SSAPAuditProcessorException.class)
	public void given_getEvent_with_an_unexisting_audit_processor() throws SSAPAuditProcessorException {

		IoTSession session = PojoGenerator.generateSession();
		GatewayInfo info = PojoGenerator.generateGatewayInfo();

		when(securityPluginManager.getSession(anyString())).thenReturn(Optional.of(session));
		when(processors.stream()).thenReturn(new ArrayList<MessageAuditProcessor>().stream());

		SSAPMessage<SSAPBodyInsertMessage> message = new SSAPMessage<SSAPBodyInsertMessage>();
		message.setMessageType(SSAPMessageTypes.INSERT);
		message.setSessionKey(session.getSessionKey());

		iotBrokerAuditProcessor.getEvent(message, info);
	}

	@Test(expected = SSAPAuditProcessorException.class)
	public void given_getEvent_with_an_unexisting_audit_message_type() throws SSAPAuditProcessorException {

		IoTSession session = PojoGenerator.generateSession();
		GatewayInfo info = PojoGenerator.generateGatewayInfo();

		when(securityPluginManager.getSession(anyString())).thenReturn(Optional.of(session));
		when(processors.stream()).thenReturn(auditProcessors.stream());

		SSAPMessage<SSAPBodyInsertMessage> message = new SSAPMessage<SSAPBodyInsertMessage>();
		message.setMessageType(null);
		message.setSessionKey(session.getSessionKey());

		iotBrokerAuditProcessor.getEvent(message, info);
	}

	@Test(expected = SSAPAuditProcessorException.class)
	public void given_getErrorEvent_with_an_unexisting_audit_processor() throws SSAPAuditProcessorException {

		IoTSession session = PojoGenerator.generateSession();
		GatewayInfo info = PojoGenerator.generateGatewayInfo();

		when(securityPluginManager.getSession(anyString())).thenReturn(Optional.of(session));
		when(this.processors.stream()).thenReturn(new ArrayList<MessageAuditProcessor>().stream());

		SSAPMessage<SSAPBodyInsertMessage> message = new SSAPMessage<SSAPBodyInsertMessage>();
		message.setMessageType(SSAPMessageTypes.INSERT);
		message.setSessionKey(session.getSessionKey());

		iotBrokerAuditProcessor.getErrorEvent(message, info, new Exception());
	}

	@Test(expected = SSAPAuditProcessorException.class)
	public void given_getErrorEvent_with_an_unexisting_audit_message_type() throws SSAPAuditProcessorException {

		IoTSession session = PojoGenerator.generateSession();
		GatewayInfo info = PojoGenerator.generateGatewayInfo();

		when(securityPluginManager.getSession(anyString())).thenReturn(Optional.of(session));
		when(processors.stream()).thenReturn(auditProcessors.stream());

		SSAPMessage<SSAPBodyInsertMessage> message = new SSAPMessage<SSAPBodyInsertMessage>();
		message.setMessageType(null);
		message.setSessionKey(session.getSessionKey());

		iotBrokerAuditProcessor.getErrorEvent(message, info, new Exception());
	}

	@Test(expected = SSAPAuditProcessorException.class)
	public void given_getErrorEvent_with_session_null() throws SSAPAuditProcessorException {

		IoTSession session = PojoGenerator.generateSession();
		GatewayInfo info = PojoGenerator.generateGatewayInfo();
		Exception ex = new Exception();

		when(securityPluginManager.getSession(anyString())).thenReturn(Optional.empty());
		when(processors.stream()).thenReturn(auditProcessors.stream());

		SSAPMessage<SSAPBodyInsertMessage> message = new SSAPMessage<SSAPBodyInsertMessage>();
		message.setMessageType(null);
		message.setSessionKey(session.getSessionKey());

		Sofia2AuditError event = iotBrokerAuditProcessor.getErrorEvent(message, info, ex);

		Assert.assertEquals(ResultOperationType.ERROR, event.getResultOperation());
		Assert.assertNull(event.getUser());
		Assert.assertEquals(ex, event.getEx());
		Assert.assertEquals(Module.IOTBROKER, event.getModule());
	}

	@Test
	public void given_getErrorEvent_with_message_and_info_null() throws SSAPAuditProcessorException {
		Exception ex = new Exception();
		Sofia2AuditError event = iotBrokerAuditProcessor.getErrorEvent(null, null, ex);
		Assert.assertEquals(EventType.ERROR, event.getType());
		Assert.assertNull(event.getUser());
		Assert.assertEquals(ex, event.getEx());
		Assert.assertEquals(Module.IOTBROKER, event.getModule());
	}

	@Test
	public void given_getErrorEvent_with_message_and_info() throws SSAPAuditProcessorException {

		IoTSession session = PojoGenerator.generateSession();
		GatewayInfo info = PojoGenerator.generateGatewayInfo();
		Exception ex = new Exception();

		when(securityPluginManager.getSession(anyString())).thenReturn(Optional.of(session));
		when(processors.stream()).thenReturn(auditProcessors.stream());

		SSAPMessage<SSAPBodyInsertMessage> message = new SSAPMessage<SSAPBodyInsertMessage>();
		message.setMessageType(SSAPMessageTypes.INSERT);
		message.setBody(new SSAPBodyInsertMessage());
		message.setSessionKey(session.getSessionKey());

		Sofia2AuditError event = iotBrokerAuditProcessor.getErrorEvent(message, info, ex);

		Assert.assertEquals(EventType.ERROR, event.getType());
		Assert.assertNotNull(event.getUser());
		Assert.assertEquals(ex, event.getEx());
		Assert.assertEquals(Module.IOTBROKER, event.getModule());
	}

	@Test
	public void given_completeEvent_with_not_user_then_anonymous_user_is_set() {

		// anonymous
		SSAPMessage<SSAPBodyInsertMessage> insertMessage = new SSAPMessage<SSAPBodyInsertMessage>();
		String insertMessageText = "This is a Insert message test";
		insertMessage.setMessageType(SSAPMessageTypes.INSERT);
		insertMessage.setBody(new SSAPBodyInsertMessage());

		GatewayInfo info = PojoGenerator.generateGatewayInfo();
		IoTSession session = PojoGenerator.generateSession();

		IotBrokerAuditEvent event = IotBrokerAuditEventFactory.builder().build()
				.createIotBrokerAuditEvent(insertMessage.getBody(), insertMessageText, null, info);

		SSAPMessage<SSAPBodyReturnMessage> message = new SSAPMessage<SSAPBodyReturnMessage>();
		message.setDirection(SSAPMessageDirection.ERROR);
		message.setSessionKey(session.getSessionKey());
		message.setBody(new SSAPBodyReturnMessage());

		event.setUser(null);

		when(securityPluginManager.getSession(anyString())).thenReturn(Optional.empty());
		when(processors.stream()).thenReturn(auditProcessors.stream());

		IotBrokerAuditEvent e = iotBrokerAuditProcessor.completeEventWithResponseMessage(message, event);

		Assert.assertEquals(AuditConst.ANONYMOUS_USER, e.getUser());
		Assert.assertEquals(Module.IOTBROKER, event.getModule());
	}

	@Test
	public void given_completeEvent_with_response_error() {

		SSAPMessage<SSAPBodyInsertMessage> insertMessage = new SSAPMessage<SSAPBodyInsertMessage>();
		String insertMessageText = "This is a Insert message test";
		insertMessage.setMessageType(SSAPMessageTypes.INSERT);
		insertMessage.setBody(new SSAPBodyInsertMessage());

		GatewayInfo info = PojoGenerator.generateGatewayInfo();
		IoTSession session = PojoGenerator.generateSession();

		IotBrokerAuditEvent event = IotBrokerAuditEventFactory.builder().build()
				.createIotBrokerAuditEvent(insertMessage.getBody(), insertMessageText, null, info);

		String errorMessage = "test error message";

		SSAPMessage<SSAPBodyReturnMessage> message = new SSAPMessage<SSAPBodyReturnMessage>();
		message.setDirection(SSAPMessageDirection.ERROR);
		message.setSessionKey(session.getSessionKey());
		message.setBody(new SSAPBodyReturnMessage());
		message.getBody().setError(errorMessage);

		when(securityPluginManager.getSession(anyString())).thenReturn(Optional.of(session));
		when(processors.stream()).thenReturn(auditProcessors.stream());

		IotBrokerAuditEvent e = iotBrokerAuditProcessor.completeEventWithResponseMessage(message, event);

		Assert.assertEquals(ResultOperationType.ERROR, e.getResultOperation());
		Assert.assertEquals(errorMessage, e.getMessage());
		Assert.assertEquals(Module.IOTBROKER, event.getModule());
	}

	@Test
	public void given_completeEvent_with_session_parameters() {

		SSAPMessage<SSAPBodyInsertMessage> insertMessage = new SSAPMessage<SSAPBodyInsertMessage>();
		String insertMessageText = "This is a Insert message test";
		insertMessage.setMessageType(SSAPMessageTypes.INSERT);
		insertMessage.setBody(new SSAPBodyInsertMessage());

		GatewayInfo info = PojoGenerator.generateGatewayInfo();
		IoTSession session = PojoGenerator.generateSession();

		IotBrokerAuditEvent event = IotBrokerAuditEventFactory.builder().build()
				.createIotBrokerAuditEvent(insertMessage.getBody(), insertMessageText, null, info);

		SSAPMessage<SSAPBodyReturnMessage> message = new SSAPMessage<SSAPBodyReturnMessage>();
		message.setDirection(SSAPMessageDirection.RESPONSE);
		message.setSessionKey(session.getSessionKey());
		message.setBody(new SSAPBodyReturnMessage());

		when(securityPluginManager.getSession(anyString())).thenReturn(Optional.of(session));
		when(processors.stream()).thenReturn(auditProcessors.stream());

		IotBrokerAuditEvent e = iotBrokerAuditProcessor.completeEventWithResponseMessage(message, event);

		Assert.assertEquals(ResultOperationType.SUCCESS, e.getResultOperation());
		Assert.assertEquals(session.getUserID(), event.getUser());
		Assert.assertEquals(session.getSessionKey(), event.getSessionKey());
		Assert.assertEquals(session.getClientPlatform(), event.getClientPlatform());
		Assert.assertEquals(session.getDevice(), event.getClientPlatformInstance());
		Assert.assertEquals(Module.IOTBROKER, event.getModule());

	}

}
