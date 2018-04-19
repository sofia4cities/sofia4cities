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
package com.indracompany.sofia2.libraries.mail;

import java.io.IOException;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest()
@SpringBootApplication
public class MailServiceIntegrationTest {
	public static void main(String[] args) {
		SpringApplication.run(MailServiceIntegrationTest.class, args);
	}

	@Autowired
	MailService mail;

	@Before
	public void setUp() throws IOException, Exception {
	}

	@After
	public void tearDown() {
	}

	@Ignore
	@Test
	public void given_OneValidEmailAddress_When_OneTextMessageIsSent_Then_TheMessageIsSent() {
		try {
			mail.sendMail("lmgracia@indra.es", "Test", "Test");
		} catch (Exception e) {
			log.error("Exception reached "+e.getMessage(),e);
			Assert.fail("Error sending mail");
		}
	}

	@Ignore
	@Test
	public void given_OneValidEmailAddress_When_OneHTMLMessageIsSent_Then_TheMessageIsSent() {
		try {
			String htmlMail = "<html><body>Here is application.yml<body></html>";
			mail.sendHtmlMail("lmgracia@indra.es", "Test", htmlMail, "application.yml");
		} catch (Exception e) {
			log.error("Exception reached "+e.getMessage(),e);
			Assert.fail("Error sending mail");
		}
	}

}
