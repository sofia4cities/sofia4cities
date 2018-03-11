package com.indracompany.sofia2.iotbroker.processor;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Optional;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import com.fasterxml.jackson.databind.JsonNode;
import com.indracompany.sofia2.iotbroker.mock.pojo.Person;
import com.indracompany.sofia2.iotbroker.mock.pojo.PojoGenerator;
import com.indracompany.sofia2.iotbroker.mock.ssap.SSAPMessageGenerator;
import com.indracompany.sofia2.iotbroker.plugable.impl.security.SecurityPluginManager;
import com.indracompany.sofia2.iotbroker.plugable.interfaces.security.IoTSession;
import com.indracompany.sofia2.ssap.SSAPMessage;
import com.indracompany.sofia2.ssap.body.SSAPBodyReturnMessage;
import com.indracompany.sofia2.ssap.body.SSAPBodySubscribeMessage;
import com.indracompany.sofia2.ssap.enums.SSAPMessageDirection;
import com.indracompany.sofia2.ssap.enums.SSAPMessageTypes;
import com.indracompany.sofia2.ssap.enums.SSAPQueryType;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SubscribeProcessorTest {

	@Autowired
	MessageProcessorDelegate subscribeProcessor;

	@MockBean
	SecurityPluginManager securityPluginManager;

	//	@Autowired
	//	SuscriptionModelRepository repositoy;


	SSAPMessage<SSAPBodySubscribeMessage> ssapSbuscription;
	IoTSession session;

	private void securityMocks() {
		session = PojoGenerator.generateSession();

		when(securityPluginManager.getSession(anyString())).thenReturn(Optional.of(session));
		when(securityPluginManager.checkSessionKeyActive(anyString())).thenReturn(true);
		when(securityPluginManager.checkAuthorization(any(), any(), any())).thenReturn(true);
	}

	@Before
	public void setUp() throws IOException, Exception {
		//		repositoy.deleteByOntologyName(Person.class.getSimpleName());
		ssapSbuscription = SSAPMessageGenerator.generateSubscriptionMessage(Person.class.getSimpleName(), session.getSessionKey(), SSAPQueryType.SQL, "select * from Person");

		securityMocks();
	}

	@Test
	public void given_OneSubsctiptionProcessorWhenSubscriptionArrivesThenSubscriptionIsStoredAndReturned() {
		final SSAPMessage<SSAPBodyReturnMessage> response = subscribeProcessor.process(ssapSbuscription);
		Assert.assertNotNull(response);
		Assert.assertEquals(SSAPMessageDirection.RESPONSE, response.getDirection());
		Assert.assertEquals(SSAPMessageTypes.SUBSCRIBE, response.getMessageType());
		Assert.assertNotNull(response.getBody());
		Assert.assertNotNull(response.getBody().getData());
		final JsonNode data = response.getBody().getData();
		Assert.assertNotNull(data.at("/subscriptionId").asText());

	}

}
