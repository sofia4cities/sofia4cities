package com.indracompany.sofia2.libraries.mail;

import java.io.IOException;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
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
public class MailServiceTest {
	public static void main(String[] args) {
		SpringApplication.run(MailServiceTest.class, args);
	}

	@Autowired
	MailService mail;

	@Before
	public void setUp() throws IOException, Exception {
	}

	@After
	public void tearDown() {
	}

	@Test
	public void testSendMail() {
		try {
			mail.sendMail("lmgracia@indra.es", "Test", "Test");
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("Error sending mail");
		}
	}

	@Test
	public void testSendHtmlMailWithAttachment() {
		try {
			String htmlMail = "<html><body>Here is application.yml<body></html>";
			mail.sendHtmlMail("lmgracia@indra.es", "Test", htmlMail, "application.yml");
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("Error sending mail");
		}
	}

}
