package com.indracompany.sofia2.config.services.configuration;

import java.util.Map;

import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import lombok.extern.slf4j.Slf4j;

@RunWith(SpringRunner.class)
@SpringBootTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@Slf4j
public class ConfigurationServiceIntegrationTest {

	@Autowired
	ConfigurationService service;

	@Test
	public void testReadYamlOK() {
		String yaml = "" + "twitter:\n" + "   account:\n"
				+ "      accessToken: 74682827-D6cX2uurqpxy6yWlg6wioRl49f9Rtt2pEXUu6YNUy\n"
				+ "      accessTokenSecret: Cmd9XOX9N8xMRvlYUz3Wg49ZCGFnanMJvJPI9QMfTXix2\n"
				+ "      consumerKey: PWgCyepuon5U8X9HqfUtNpntq\n"
				+ "      consumerSecret: zo6rbSh6J470t7CCz4ZtXhHEFhpt36TMPKYolJgIiLOpEW9oc4\n";
		Assert.assertTrue(service.isValidYaml(yaml));
		Map values = service.fromYaml(yaml);
		Assert.assertEquals("74682827-D6cX2uurqpxy6yWlg6wioRl49f9Rtt2pEXUu6YNUy",
				values.get("twitter.account.accesToken"));

	}
}
