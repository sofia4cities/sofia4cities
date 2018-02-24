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
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import java.util.UUID;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import com.indracompany.sofia2.common.exception.AuthenticationException;
import com.indracompany.sofia2.iotbroker.common.MessageException;
import com.indracompany.sofia2.iotbroker.ssap.generator.SSAPMessageGenerator;
import com.indracompany.sofia2.plugin.iotbroker.security.SecurityPluginManager;
import com.indracompany.sofia2.ssap.SSAPMessage;
import com.indracompany.sofia2.ssap.body.SSAPBodyJoinMessage;
import com.indracompany.sofia2.ssap.body.SSAPBodyReturnMessage;
import com.indracompany.sofia2.ssap.enums.SSAPErrorCode;

@RunWith(SpringRunner.class)
@SpringBootTest
public class JoinProcessorWithTokenTest {

	@Autowired
	MessageProcessorDelegate processor;

	@MockBean
	SecurityPluginManager securityPluginManager;

	SSAPMessage<SSAPBodyJoinMessage> ssapJoin;

	@Before
	public void setup() {
		ssapJoin = SSAPMessageGenerator.generateJoinMessageWithToken();
	}

	@Test
	public void test_join_with_valid_token() throws AuthenticationException {
		final String assignedSessionKey = UUID.randomUUID().toString();
		when(securityPluginManager.authenticate(ssapJoin)).thenReturn(assignedSessionKey);
		ssapJoin.getBody().setToken(UUID.randomUUID().toString());
		final SSAPMessage<SSAPBodyReturnMessage> responseMessage = processor.process(ssapJoin);

		Assert.assertNotNull(responseMessage);
		Assert.assertNotNull(responseMessage.getBody());
		Assert.assertEquals(assignedSessionKey, responseMessage.getSessionKey());
	}

	@Test
	public void test_join_with_invalid_token() throws AuthenticationException {
		doThrow(new AuthenticationException(MessageException.ERR_TOKEN_IS_INVALID)).when(securityPluginManager).authenticate(any());
		ssapJoin.getBody().setToken(UUID.randomUUID().toString());
		final SSAPMessage<SSAPBodyReturnMessage> responseMessage = processor.process(ssapJoin);

		Assert.assertNotNull(responseMessage);
		Assert.assertNotNull(responseMessage.getBody());
		Assert.assertEquals(SSAPErrorCode.AUTENTICATION, responseMessage.getBody().getErrorCode());
	}

	@Test
	public void test_join_with_empty_token() {

		//Token is an Empty string
		{
			ssapJoin.getBody().setToken("");
			final SSAPMessage<SSAPBodyReturnMessage> responseMessage = processor.process(ssapJoin);

			Assert.assertNotNull(responseMessage);
			Assert.assertNotNull(responseMessage.getBody());
			Assert.assertEquals(SSAPErrorCode.PROCESSOR, responseMessage.getBody().getErrorCode());

		}

		//Token is NULL
		{
			ssapJoin.getBody().setToken(null);
			final SSAPMessage<SSAPBodyReturnMessage> responseMessage = processor.process(ssapJoin);

			Assert.assertNotNull(responseMessage);
			Assert.assertNotNull(responseMessage.getBody());
			Assert.assertEquals(SSAPErrorCode.PROCESSOR, responseMessage.getBody().getErrorCode());

		}

	}

}
