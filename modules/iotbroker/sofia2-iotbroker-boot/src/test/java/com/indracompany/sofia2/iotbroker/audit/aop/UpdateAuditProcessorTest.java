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
import com.indracompany.sofia2.iotbroker.audit.processor.UpdateAuditProcessor;
import com.indracompany.sofia2.iotbroker.mock.pojo.PojoGenerator;
import com.indracompany.sofia2.iotbroker.plugable.interfaces.gateway.GatewayInfo;
import com.indracompany.sofia2.iotbroker.plugable.interfaces.security.IoTSession;
import com.indracompany.sofia2.ssap.SSAPMessage;
import com.indracompany.sofia2.ssap.body.SSAPBodyUpdateByIdMessage;
import com.indracompany.sofia2.ssap.body.SSAPBodyUpdateMessage;
import com.indracompany.sofia2.ssap.enums.SSAPMessageDirection;
import com.indracompany.sofia2.ssap.enums.SSAPMessageTypes;

@RunWith(MockitoJUnitRunner.class)
@SpringBootTest
public class UpdateAuditProcessorTest {

	@InjectMocks
	private UpdateAuditProcessor updateAuditProcessor;

	private final String ONTOLOGY_NAME = "ontology";
	private final String QUERY = "db.test.find({});";

	@Test
	public void given_a_update_message_get_audit_event() {

		IoTSession session = PojoGenerator.generateSession();
		GatewayInfo info = PojoGenerator.generateGatewayInfo();

		SSAPMessage<SSAPBodyUpdateMessage> message = new SSAPMessage<SSAPBodyUpdateMessage>();
		message.setMessageType(SSAPMessageTypes.UPDATE);
		message.setBody(new SSAPBodyUpdateMessage());
		message.setSessionKey(session.getSessionKey());
		message.setDirection(SSAPMessageDirection.REQUEST);
		message.getBody().setOntology(ONTOLOGY_NAME);
		message.getBody().setQuery(QUERY);

		IotBrokerAuditEvent event = updateAuditProcessor.process(message, session, info);

		Assert.assertEquals(OperationType.UPDATE.name(), event.getOperationType());
		Assert.assertEquals(ONTOLOGY_NAME, event.getOntology());
		Assert.assertNotNull(event.getQuery());
		Assert.assertNull(event.getData());
		Assert.assertEquals(info, event.getGatewayInfo());
		Assert.assertEquals(Module.IOTBROKER, event.getModule());
	}

	@Test
	public void given_a_update_by_id_message_get_audit_event() {

		IoTSession session = PojoGenerator.generateSession();
		GatewayInfo info = PojoGenerator.generateGatewayInfo();

		SSAPMessage<SSAPBodyUpdateByIdMessage> message = new SSAPMessage<SSAPBodyUpdateByIdMessage>();
		message.setMessageType(SSAPMessageTypes.UPDATE_BY_ID);
		message.setBody(new SSAPBodyUpdateByIdMessage());
		message.setSessionKey(session.getSessionKey());
		message.setDirection(SSAPMessageDirection.REQUEST);
		message.getBody().setOntology(ONTOLOGY_NAME);

		IotBrokerAuditEvent event = updateAuditProcessor.process(message, session, info);

		Assert.assertEquals(OperationType.UPDATE.name(), event.getOperationType());
		Assert.assertEquals(ONTOLOGY_NAME, event.getOntology());
		Assert.assertNull(event.getQuery());
		Assert.assertNull(event.getData());
		Assert.assertEquals(info, event.getGatewayInfo());
		Assert.assertEquals(Module.IOTBROKER, event.getModule());
	}

	@Test
	public void given_getMessageTypes() {

		List<SSAPMessageTypes> types = updateAuditProcessor.getMessageTypes();
		List<SSAPMessageTypes> expected = Arrays.asList(SSAPMessageTypes.UPDATE, SSAPMessageTypes.UPDATE_BY_ID);
		Assert.assertEquals(expected, types);

	}
}
