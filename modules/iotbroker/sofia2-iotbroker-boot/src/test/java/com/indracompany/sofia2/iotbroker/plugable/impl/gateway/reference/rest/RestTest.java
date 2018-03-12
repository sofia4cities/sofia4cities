package com.indracompany.sofia2.iotbroker.plugable.impl.gateway.reference.rest;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Optional;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.indracompany.sofia2.iotbroker.mock.pojo.Person;
import com.indracompany.sofia2.iotbroker.mock.pojo.PojoGenerator;
import com.indracompany.sofia2.iotbroker.plugable.impl.security.SecurityPluginManager;
import com.indracompany.sofia2.iotbroker.plugable.interfaces.security.IoTSession;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RestTest {
	@Value("${local.server.port}")
	private int port;

	private MockMvc mockMvc;
	@Autowired
	private WebApplicationContext wac;
	private ResultActions resultAction;
	private final String URL_PATH  = "/rest";
	@Autowired
	ObjectMapper mapper;

	@MockBean
	SecurityPluginManager securityPluginManager;
	Person subject;

	IoTSession session;

	private void securityMocks() {
		session = PojoGenerator.generateSession();

		when(securityPluginManager.authenticate(any(), any(), any())).thenReturn(Optional.of(session));
		when(securityPluginManager.getSession(anyString())).thenReturn(Optional.of(session));
		when(securityPluginManager.checkSessionKeyActive(anyString())).thenReturn(true);
		when(securityPluginManager.checkAuthorization(any(), any(), any())).thenReturn(true);
	}


	@Before
	public void setup() {
		mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();

		subject = PojoGenerator.generatePerson();
		securityMocks();
	}

	@Test
	public void test_join() throws Exception {

		final StringBuilder url = new StringBuilder(URL_PATH);
		url.append("/client/join?token=2382c702758c4f26ad1d38d1309335d0&clientPlatform=GTKP-Example&clientPlatformId=1111");
		resultAction = mockMvc.perform(MockMvcRequestBuilders.get(url.toString())
				.accept(org.springframework.http.MediaType.APPLICATION_JSON)
				.contentType(org.springframework.http.MediaType.APPLICATION_JSON));

		resultAction.andExpect(status().is2xxSuccessful());
		final JsonNode result = mapper.readValue(resultAction.andReturn().getResponse().getContentAsString(),
				JsonNode.class);
		Assert.assertNotNull(result);

		//		final String a = (String) result.getBody();
		System.out.println(result);





	}



}
