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
package com.indracompany.sofia2.iotbroker.processor;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import com.indracompany.sofia2.commons.testing.IntegrationTest;
import com.indracompany.sofia2.config.model.IoTSession;
import com.indracompany.sofia2.iotbroker.common.exception.AuthenticationException;
import com.indracompany.sofia2.iotbroker.common.exception.SSAPProcessorException;
import com.indracompany.sofia2.iotbroker.mock.pojo.PojoGenerator;
import com.indracompany.sofia2.iotbroker.mock.ssap.SSAPMessageGenerator;
import com.indracompany.sofia2.iotbroker.plugable.impl.security.SecurityPluginManager;
import com.indracompany.sofia2.ssap.SSAPMessage;
import com.indracompany.sofia2.ssap.body.SSAPBodyJoinMessage;
import com.indracompany.sofia2.ssap.body.SSAPBodyReturnMessage;
import com.indracompany.sofia2.ssap.enums.SSAPErrorCode;

@RunWith(SpringRunner.class)
@SpringBootTest
@Category(IntegrationTest.class)
public class JoinProcessorWithTokenTest {

	@Autowired
	MessageProcessorDelegate processor;

	@MockBean
	SecurityPluginManager securityPluginManager;

	SSAPMessage<SSAPBodyJoinMessage> ssapJoin;

	@MockBean
	DeviceManager deviceManager;

	IoTSession session = new IoTSession();

	// @MockBean
	// IotBrokerAuditableAspect iotBrokerAuditableAspect;

	private void auditMocks() {
		try {
			// TODO it is not possible to mock processTx in this way. It returns a value.
			// TODO Furthermore, it is an @Around aspect, so it executes before and after
			// the jointpoint method.
			// doNothing().when(iotBrokerAuditableAspect).processTx(any(), any(), any(),
			// any());
			// doNothing().when(iotBrokerAuditableAspect).doRecoveryActions(any(), any(),
			// any(), any(), any());

		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Before
	public void setup() throws SSAPProcessorException {
		session = new IoTSession();
		session.setClientPlatform("someDevice");
		ssapJoin = SSAPMessageGenerator.generateJoinMessageWithToken();
		when(deviceManager.registerActivity(any(), any(), any(), any())).thenReturn(true);
		when(securityPluginManager.getSession(any())).thenReturn(Optional.of(session));
		auditMocks();
	}

	@Test
	public void given_OneJoinProcessor_When_OneValidSessionIsUsed_Then_TheResponseIndicatesTheOperationWasPerformed()
			throws AuthenticationException {
		final String assignedSessionKey = UUID.randomUUID().toString();
		session.setUserID("valid_user_id");
		session.setSessionKey(assignedSessionKey);
		when(securityPluginManager.authenticate(anyString(), anyString(), anyString(), anyString()))
				.thenReturn(Optional.of(session));
		ssapJoin.getBody().setToken(UUID.randomUUID().toString());
		final SSAPMessage<SSAPBodyReturnMessage> responseMessage = processor.process(ssapJoin,
				PojoGenerator.generateGatewayInfo());

		Assert.assertNotNull(responseMessage);
		Assert.assertNotNull(responseMessage.getBody());
		Assert.assertEquals(assignedSessionKey, responseMessage.getSessionKey());
	}

	@Test
	public void given_OneJoinProcessor_When_ItUsesAnInvalidToken_Then_TheResponseIndicatesAuthenticationError()
			throws AuthenticationException {
		when(securityPluginManager.authenticate(anyString(), anyString(), anyString(), anyString()))
				.thenReturn(Optional.empty());
		ssapJoin.getBody().setToken(UUID.randomUUID().toString());
		final SSAPMessage<SSAPBodyReturnMessage> responseMessage = processor.process(ssapJoin,
				PojoGenerator.generateGatewayInfo());

		Assert.assertNotNull(responseMessage);
		Assert.assertNotNull(responseMessage.getBody());
		Assert.assertEquals(SSAPErrorCode.AUTENTICATION, responseMessage.getBody().getErrorCode());
	}

	@Test
	public void given_OneJoinProcessor_When_AnEmptyOrNullTokenIsUsed_Then_TheResponseIndicatesProcessorError() {

		// Token is an Empty string
		{
			ssapJoin.getBody().setToken("");
			final SSAPMessage<SSAPBodyReturnMessage> responseMessage = processor.process(ssapJoin,
					PojoGenerator.generateGatewayInfo());

			Assert.assertNotNull(responseMessage);
			Assert.assertNotNull(responseMessage.getBody());
			Assert.assertEquals(SSAPErrorCode.PROCESSOR, responseMessage.getBody().getErrorCode());

		}

		// Token is NULL
		{
			ssapJoin.getBody().setToken(null);
			final SSAPMessage<SSAPBodyReturnMessage> responseMessage = processor.process(ssapJoin,
					PojoGenerator.generateGatewayInfo());

			Assert.assertNotNull(responseMessage);
			Assert.assertNotNull(responseMessage.getBody());
			Assert.assertEquals(SSAPErrorCode.PROCESSOR, responseMessage.getBody().getErrorCode());

		}

	}

}
