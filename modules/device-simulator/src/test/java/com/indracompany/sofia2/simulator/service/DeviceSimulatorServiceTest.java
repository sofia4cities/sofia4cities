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
package com.indracompany.sofia2.simulator.service;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DeviceSimulatorServiceTest {

	@Autowired
	PersistenceServiceImpl persistenceService;

	private String clientPlatform;
	private String clientPlatformInstance;
	private String ontology;
	private String user;
	private String instance;

	@Before
	public void setUp() {
		this.clientPlatform = "Ticketing App";
		this.clientPlatformInstance = "Test";
		this.ontology = "Ticket";
		this.user = "developer";
		this.instance = "{\"Ticket\":{\"Identification\":\"\",\"Status\":\"\",\"Email\":\"\",\"Name\":\"\",\"Response_via\":\"\",\"File\":{\"data\":\"\",\"media\":{\"name\":\"\",\"storageArea\":\"SERIALIZED\",\"binaryEncoding\":\"Base64\",\"mime\":\"application/pdf\"}},\"Coordinates\":{\"coordinates\":{\"latitude\":55.5842,\"longitude\":44.0645},\"type\":\"Point\"}},\"contextData\":{\"clientPatform\":\"Ticketing App\",\"clientPatformInstance\":\"Ticketing App:Ticket\",\"clientConnection\":\"\",\"clientSession\":\"01dbb0ec-74b8-4567-b103-759cf164d9a6\",\"user\":\"developer\",\"timezoneId\":\"Europe/Paris\",\"timestamp\":\"Wed Apr 18 15:01:21 CEST 2018\",\"timestampMillis\":{\"$numberLong\":\"1524056481126\"}}}";
	}

	@Test
	public void test_Iotbroker_connection() throws InterruptedException {
		this.persistenceService.connectDeviceRest(clientPlatform, clientPlatformInstance);
		Assert.assertNotNull(persistenceService.getSessionKeys().get(clientPlatform));
	}

	@Test(expected = IndexOutOfBoundsException.class)
	public void test_Itobroker_trysToConnect_withInvalidDevice_thenThrowsIndexOutOfBoundsException()
			throws InterruptedException {
		this.clientPlatform = "NoDevice";
		this.persistenceService.connectDeviceRest(clientPlatform, clientPlatformInstance);
		Assert.assertNull(this.persistenceService.getSessionKeys().get(clientPlatform));

	}

	@Test
	public void test_IotbrokerDisconnect() throws InterruptedException {
		this.persistenceService.connectDeviceRest(clientPlatform, clientPlatformInstance);
		Assert.assertNotNull(persistenceService.getSessionKeys().get(clientPlatform));
		this.persistenceService.disconnectDeviceRest(clientPlatform);
		Assert.assertNull(persistenceService.getSessionKeys().get(clientPlatform));
	}

	@Test
	public void test_IotbrokerInsert() throws Exception {
		this.clientPlatform = "Ticketing App";
		this.persistenceService.insertOntologyInstance(instance, ontology, user, clientPlatform,
				clientPlatformInstance);
		Assert.assertNotNull(persistenceService.getSessionKeys().get(clientPlatform));
	}
}
