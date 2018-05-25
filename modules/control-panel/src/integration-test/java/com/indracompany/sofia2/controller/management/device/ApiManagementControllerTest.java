package com.indracompany.sofia2.controller.management.device;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.indracompany.sofia2.commons.testing.IntegrationTest;
import com.indracompany.sofia2.config.model.Api;
import com.indracompany.sofia2.config.model.Api.ApiStates;
import com.indracompany.sofia2.config.model.UserApi;
import com.indracompany.sofia2.config.repository.ApiRepository;
import com.indracompany.sofia2.config.repository.UserApiRepository;
import com.indracompany.sofia2.config.services.user.UserService;
import com.indracompany.sofia2.controlpanel.Sofia2ControlPanelWebApplication;

import lombok.extern.slf4j.Slf4j;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Sofia2ControlPanelWebApplication.class)
@Category(IntegrationTest.class)
@ContextConfiguration
@WebAppConfiguration
@Slf4j
public class ApiManagementControllerTest {

	@Autowired
	private WebApplicationContext context;
	MockMvc mvc;
	@Autowired
	ObjectMapper mapper;
	private String oAuthHeader;
	private Api apiDeveloper;
	private Api apiDataViewer;
	private UserApi userApi;

	@Autowired
	UserService userService;
	@Autowired
	ApiRepository apiRepository;
	@Autowired
	UserApiRepository userApiRepository;

	@Before
	public void setUp() throws Exception {
		mvc = MockMvcBuilders.webAppContextSetup(context).build();
		String response = mvc
				.perform(MockMvcRequestBuilders.post("/api-ops/login").contentType(MediaType.APPLICATION_JSON)
						.content("{\"password\":\"changeIt!\",\"username\":\"developer\"}"))
				.andReturn().getResponse().getContentAsString();
		JsonNode responseJson = this.mapper.readValue(response, JsonNode.class);
		this.oAuthHeader = "Bearer " + responseJson.get("access_token").asText();
		log.info(oAuthHeader);

		this.apiDeveloper = new Api();
		apiDeveloper.setSsl_certificate(false);
		apiDeveloper.setIdentification("Api get tickets");
		apiDeveloper.setDescription("Api for testing");
		apiDeveloper.setState(ApiStates.CREATED);
		apiDeveloper.setPublic(false);
		apiDeveloper.setUser(this.userService.getUser("developer"));

		this.apiDataViewer = new Api();
		apiDataViewer.setSsl_certificate(false);
		apiDataViewer.setIdentification("Api get tickets");
		apiDataViewer.setDescription("Api for testing");
		apiDataViewer.setState(ApiStates.CREATED);
		apiDataViewer.setPublic(false);
		apiDataViewer.setUser(this.userService.getUser("dataviewer"));

		this.apiDeveloper = this.apiRepository.save(apiDeveloper);
		this.apiDataViewer = this.apiRepository.save(apiDataViewer);

		this.userApi = new UserApi();
		userApi.setApi(apiDeveloper);
		userApi.setUser(this.userService.getUser("dataviewer"));
		this.userApi = this.userApiRepository.save(userApi);

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();

	}

	@After
	public void tearDown() {
		this.userApiRepository.delete(userApi);
		this.apiRepository.delete(apiDataViewer);
		this.apiRepository.delete(apiDeveloper);
	}

	@Test
	public void oauthToken_isNotNull() {
		Assert.assertNotNull(oAuthHeader);
	}

	@Test
	public void user_triesToChangeItsApiAuthorizations_andGetsOk() throws Exception {
		mvc.perform(MockMvcRequestBuilders
				.post("/management/authorize/api/" + this.apiDeveloper.getId() + "/user/analytics")
				.header("Authorization", this.oAuthHeader)).andExpect(MockMvcResultMatchers.status().is2xxSuccessful());
		mvc.perform(MockMvcRequestBuilders
				.post("/management/deauthorize/api/" + this.apiDeveloper.getId() + "/user/analytics")
				.header("Authorization", this.oAuthHeader)).andExpect(MockMvcResultMatchers.status().is2xxSuccessful());

	}

	@Test
	public void user_triesToChangeOthersApiAuthorizations_andGetsKo() throws Exception {
		mvc.perform(MockMvcRequestBuilders
				.post("/management/authorize/api/" + this.apiDataViewer.getId() + "/user/analytics")
				.header("Authorization", this.oAuthHeader))
				.andExpect(MockMvcResultMatchers.status().is4xxClientError());
	}
}
