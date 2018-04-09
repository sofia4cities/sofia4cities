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
package com.indracompany.sofia2.config.services.configuration;

import java.io.IOException;
import java.util.Map;

import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.indracompany.sofia2.config.components.TwitterConfiguration;
import com.indracompany.sofia2.config.components.Urls;
import com.indracompany.sofia2.config.model.Configuration;

@RunWith(SpringRunner.class)
@SpringBootTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ConfigurationServiceIntegrationTest {

	@Autowired
	ConfigurationService service;

	@Test
	public void given_AValidConfigurationYAML_When_ItIsValidated_Then_ValuesCanBeCorrectlyObtained() {
		String yaml = "" + "twitter:\n" + "      accessToken: 74682827-D6cX2uurqpxy6yWlg6wioRl49f9Rtt2pEXUu6YNUy\n"
				+ "      accessTokenSecret: Cmd9XOX9N8xMRvlYUz3Wg49ZCGFnanMJvJPI9QMfTXix2\n"
				+ "      consumerKey: PWgCyepuon5U8X9HqfUtNpntq\n"
				+ "      consumerSecret: zo6rbSh6J470t7CCz4ZtXhHEFhpt36TMPKYolJgIiLOpEW9oc4\n";
		Assert.assertTrue(service.isValidYaml(yaml));
		Map<?, ?> values = service.fromYaml(yaml);
		Map<?, ?> value = (Map<?, ?>) values.get("twitter");
		Assert.assertEquals(value.get("accessToken"), "74682827-D6cX2uurqpxy6yWlg6wioRl49f9Rtt2pEXUu6YNUy");
	}

	@Test
	public void given_OneConfiguration_When_TwitterPropertiesAreRequested_TheCorrectValuesAreObtained() {
		Configuration config = service.getConfiguration(Configuration.Type.TwitterConfiguration, "default", "lmgracia");
		Map<?, ?> values = service.fromYaml(config.getYmlConfig());
		Map<?, ?> value = (Map<?, ?>) values.get("twitter");
		Assert.assertEquals(value.get("accessToken"), "74682827-D6cX2uurqpxy6yWlg6wioRl49f9Rtt2pEXUu6YNUy");
	}

	@Test
	public void given_OneConfiguration_When_TwitterWholeConfigurationIsRequested_ItIsObtained() {
		TwitterConfiguration config = service.getTwitterConfiguration("default", "lmgracia");
		Assert.assertEquals(config.getAccessToken(), "74682827-D6cX2uurqpxy6yWlg6wioRl49f9Rtt2pEXUu6YNUy");
	}

	@Test
	public void endpointsConfiguration_fromYaml() throws IOException {
		Urls urls = this.service.getEndpointsUrls("default");
		Assert.assertTrue(urls.getIotbroker().getBase().contains("iotbroker"));
	}
}
