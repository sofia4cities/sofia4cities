package com.indracompany.sofia2.libraries.mail;

import javax.mail.MessagingException;

import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;

public interface MailService {
	void sendMail(String to, String subject, String text) throws MailException;

	void sendMailWithTemplate(String to, String subject, SimpleMailMessage template, String... templateArgs)
			throws MailException;

	void sendHtmlMail(String to, String subject, String text, String pathToAttachment) throws MessagingException;
}
