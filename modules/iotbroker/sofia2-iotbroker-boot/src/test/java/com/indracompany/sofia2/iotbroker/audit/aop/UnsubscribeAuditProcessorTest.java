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

import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;

import com.indracompany.sofia2.audit.bean.Sofia2AuditEvent.Module;
import com.indracompany.sofia2.audit.bean.Sofia2AuditEvent.OperationType;
import com.indracompany.sofia2.iotbroker.audit.bean.IotBrokerAuditEvent;
import com.indracompany.sofia2.iotbroker.audit.processor.UnsubscribeAuditProcessor;
import com.indracompany.sofia2.iotbroker.mock.pojo.PojoGenerator;
import com.indracompany.sofia2.iotbroker.plugable.interfaces.gateway.GatewayInfo;
import com.indracompany.sofia2.iotbroker.plugable.interfaces.security.IoTSession;
import com.indracompany.sofia2.ssap.SSAPMessage;
import com.indracompany.sofia2.ssap.body.SSAPBodyUnsubscribeMessage;
import com.indracompany.sofia2.ssap.enums.SSAPMessageDirection;
import com.indracompany.sofia2.ssap.enums.SSAPMessageTypes;

@RunWith(MockitoJUnitRunner.class)
@SpringBootTest
public class UnsubscribeAuditProcessorTest {

	@InjectMocks
	private UnsubscribeAuditProcessor unsubscribeAuditProcessor;

	@Test
	public void given_a_unsubscribe_message_get_audit_event() {

		IoTSession session = PojoGenerator.generateSession();
		GatewayInfo info = PojoGenerator.generateGatewayInfo();

		SSAPMessage<SSAPBodyUnsubscribeMessage> message = new SSAPMessage<SSAPBodyUnsubscribeMessage>();
		message.setMessageType(SSAPMessageTypes.UNSUBSCRIBE);
		message.setBody(new SSAPBodyUnsubscribeMessage());
		message.setDirection(SSAPMessageDirection.REQUEST);

		IotBrokerAuditEvent event = unsubscribeAuditProcessor.process(message, session, info);

		Assert.assertEquals(OperationType.UNSUBSCRIBE.name(), event.getOperationType());
		Assert.assertNull(event.getOntology());
		Assert.assertNull(event.getQuery());
		Assert.assertNull(event.getData());
		Assert.assertEquals(info, event.getGatewayInfo());
		Assert.assertEquals(Module.IOTBROKER, event.getModule());
	}

	@Test
	public void given_getMessageTypes() {

		List<SSAPMessageTypes> types = unsubscribeAuditProcessor.getMessageTypes();
		List<SSAPMessageTypes> expected = Arrays.asList(SSAPMessageTypes.UNSUBSCRIBE);
		Assert.assertEquals(expected, types);

	}
}
