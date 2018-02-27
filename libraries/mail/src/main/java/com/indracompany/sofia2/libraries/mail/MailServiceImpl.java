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

import java.io.File;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class MailServiceImpl implements MailService {

	@Autowired
	public JavaMailSenderImpl emailSender;

	@Override
	public void sendMail(String to, String subject, String text) throws MailException {
		try {
			// emailSender.
			SimpleMailMessage message = new SimpleMailMessage();
			message.setTo(to);
			message.setSubject(subject);
			message.setText(text);
			emailSender.send(message);
		} catch (MailException e) {
			log.error("Error sending mail", e);
			log.error("If you are using GMail check https://support.google.com/accounts/answer/6010255");
			throw e;
		}
	}

	@Override
	public void sendMailWithTemplate(String to, String subject, SimpleMailMessage template, String... templateArgs)
			throws MailException {
		String text = String.format(template.getText(), templateArgs);
		sendMail(to, subject, text);
	}

	@Override
	public void sendHtmlMail(String to, String subject, String htmlText, String attachment) throws MessagingException {
		MimeMessageHelper helper = null;
		try {
			MimeMessage message = emailSender.createMimeMessage();
			// pass 'true' to the constructor to create a multipart message
			if (attachment != null) {
				helper = new MimeMessageHelper(message, true);
				helper.addAttachment(attachment,
						new File(getClass().getClassLoader().getResource(attachment).getFile()));
			} else {
				helper = new MimeMessageHelper(message);
			}

			helper.setTo(to);
			helper.setSubject(subject);
			helper.setText(htmlText, true);

			emailSender.send(message);
		} catch (MessagingException e) {
			log.error("Error sending mail", e);
			log.error("If you are using GMail check https://support.google.com/accounts/answer/6010255");
			throw e;
		}
	}

}
