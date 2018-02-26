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
///**
// * Copyright Indra Sistemas, S.A.
// * 2013-2018 SPAIN
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *      http://www.apache.org/licenses/LICENSE-2.0
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//package com.indracompany.sofia2.streaming.twitter.sib;
//
//import static org.mockito.Matchers.any;
//import static org.mockito.Matchers.anyString;
//import static org.mockito.Mockito.doNothing;
//import static org.mockito.Mockito.when;
//
//import java.io.IOException;
//import java.util.UUID;
//
//import org.junit.After;
//import org.junit.Assert;
//import org.junit.Before;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.data.mongodb.core.MongoTemplate;
//import org.springframework.test.context.ContextConfiguration;
//import org.springframework.test.context.junit4.SpringRunner;
//
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.indracompany.sofia2.common.exception.AuthenticationException;
//import com.indracompany.sofia2.common.exception.AuthorizationException;
//import com.indracompany.sofia2.iotbroker.common.exception.SSAPComplianceException;
//import com.indracompany.sofia2.plugin.iotbroker.security.SecurityPluginManager;
//import com.indracompany.sofia2.streaming.twitter.application.StreamingTwitterApp;
//import com.indracompany.sofia2.streaming.twitter.sib.SibService;
//
//@RunWith(SpringRunner.class)
//@SpringBootTest(classes = StreamingTwitterApp.class)
//@ContextConfiguration(classes= StreamingTwitterApp.class) 
//public class SibServiceIntegrationTest {
//
//	@Autowired
//	private SibService sibService;
//	@MockBean
//	SecurityPluginManager securityPluginManager;
//	@Autowired
//	MongoTemplate springDataMongoTemplate;
//
//	private final String token = UUID.randomUUID().toString();
//	private final String sessionKey = UUID.randomUUID().toString();
//
//	@Before
//	public void setUp() throws IOException, Exception {
//		if (springDataMongoTemplate.collectionExists("TwitterOntology")) {
//			springDataMongoTemplate.dropCollection("TwitterOntology");
//		}
//		springDataMongoTemplate.createCollection("TwitterOntology");
//	}
//
//	@After
//	public void tearDown() {
//		if (springDataMongoTemplate.collectionExists("TwitterOntology")) {
//			springDataMongoTemplate.dropCollection("TwitterOntology");
//		}
//
//	}
//
//	@Test
//	public void test_connectSib() throws SSAPComplianceException, AuthenticationException {
//		when(securityPluginManager.authenticate(any())).thenReturn(sessionKey);
//		String sessionKey = this.sibService.getSessionKey(token);
//		Assert.assertTrue(sessionKey != null);
//
//	}
//
//	// nullpointer , MessageDelegateProcessor is returning response=null when
//	// proxy(LeaveProcessor).process
//	@Test(expected = NullPointerException.class)
//	public void test_disconnectSib() throws AuthorizationException {
//		doNothing().when(securityPluginManager).closeSession(sessionKey);
//		this.sibService.disconnect(sessionKey).getBody().getErrorCode();
//	}
//
//	@Test
//	public void test_insertOntologyInstance() throws JsonProcessingException, IOException {
//		when(securityPluginManager.getUserIdFromSessionKey(anyString())).thenReturn("valid_user_id");
//		Assert.assertTrue(this.sibService.insertOntologyInstance(
//				"{\"instance\" : {\"type\":\"ontology\",\"name\":\"ontologyTest\"}}", sessionKey,
//				"TwitterOntology", "clientPlatform", "clientPlatformInstance"));
//	}
//
//}
