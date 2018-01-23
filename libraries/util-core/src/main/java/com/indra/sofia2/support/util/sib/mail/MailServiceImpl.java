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
/*******************************************************************************
 * © Indra Sistemas, S.A.
 * 2013 - 2014  SPAIN
 * 
 * All rights reserved
 ******************************************************************************/
package com.indra.sofia2.support.util.sib.mail;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Properties;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring3.SpringTemplateEngine;

import com.indra.jee.arq.spring.core.contexto.ArqSpringContext;

@Component
public class MailServiceImpl implements MailService {

	private static final String HOST_PROPERTY = "mail.host";
	private static final String PORT_PROPERTY = "mail.port";
	private static final String PROTOCOL_PROPERTY = "mail.protocol";
	private static final String ENABLE_ANONYMOUS_MODE = "mail.anonymousMode";
	private static final String USERNAME_PROPERTY = "mail.username";
	private static final String PASSWORD_PROPERTY = "mail.password";
	private static final String SMTPS_AUTH_PROPERTY = "mail.smtps.auth";
	private static final String SMTPS_STARTTLS_PROPERTY = "mail.smtps.starttls.enable";
	private static final String SMTPS_DEBUG_PROPERTY = "mail.smtps.debug";
	private static final String DELIVERY_TIMEOUT_PROPERTY = "mail.delivery.timeout.seconds";
	private static final long DEFAULT_DELIVERY_TIMEOUT = 20L;
	
	@Autowired
	@Qualifier("emailTemplateEngine")
	private SpringTemplateEngine templateEngine;
	
	private long deliveryTimeout;
	private boolean initializationErrors;
	private JavaMailSender mailSender;
	
	private static final Logger logger = LoggerFactory.getLogger(MailServiceImpl.class);
	
	@PostConstruct
	public void init(){
		String lastProperty = null;
		this.initializationErrors = false;
		try {
			JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
			lastProperty = HOST_PROPERTY;
			mailSender.setHost(ArqSpringContext.getPropiedad(HOST_PROPERTY));
			lastProperty = PORT_PROPERTY;
			mailSender.setPort(ArqSpringContext.getPropiedadInt(PORT_PROPERTY));
			lastProperty = PROTOCOL_PROPERTY;
			mailSender.setProtocol(ArqSpringContext.getPropiedad(PROTOCOL_PROPERTY));
			lastProperty = ENABLE_ANONYMOUS_MODE;
			Properties javaMailProperties = new Properties();
			if (!ArqSpringContext.getPropiedadBoolean(ENABLE_ANONYMOUS_MODE)){
				lastProperty = USERNAME_PROPERTY;
				mailSender.setUsername(ArqSpringContext.getPropiedad(USERNAME_PROPERTY));
				lastProperty = PASSWORD_PROPERTY;
				mailSender.setPassword(ArqSpringContext.getPropiedad(PASSWORD_PROPERTY));
				javaMailProperties.setProperty(SMTPS_AUTH_PROPERTY, Boolean.toString(true));
				lastProperty = SMTPS_STARTTLS_PROPERTY;
				javaMailProperties.setProperty(SMTPS_STARTTLS_PROPERTY, ArqSpringContext.getPropiedad(SMTPS_STARTTLS_PROPERTY));
				lastProperty = SMTPS_DEBUG_PROPERTY;
				javaMailProperties.setProperty(SMTPS_DEBUG_PROPERTY, ArqSpringContext.getPropiedad(SMTPS_DEBUG_PROPERTY));
			} 
			mailSender.setJavaMailProperties(javaMailProperties);
			this.mailSender = mailSender;
		} catch (Exception e){
			logger.error("Unable to load the property " + lastProperty 
					+ ". The MailService has not been initialized correctly.", e);
			this.initializationErrors = true;
		}
		try {
			deliveryTimeout = ArqSpringContext.getPropiedadLong(DELIVERY_TIMEOUT_PROPERTY);
		} catch (Exception e) {
			deliveryTimeout = DEFAULT_DELIVERY_TIMEOUT;
			logger.warn("Unable to load property {}. Using default value {}.", DELIVERY_TIMEOUT_PROPERTY,
					deliveryTimeout);
		}
	}

	@Override
	public void sendMail(String from, String to, String subject, String msg) {
		if (initializationErrors){
			logger.error("The mail service is not configured properly. Nothing will be sent");
			return;
		}
		try {
			MimeMessage mimeMessage = mailSender.createMimeMessage();
			MimeMessageHelper message = new MimeMessageHelper(mimeMessage, true, "UTF-8");
			message.setSubject(subject);
			message.setFrom(from);
			message.setTo(to);
			message.setText(msg, false);
			deliverEmail(mimeMessage);
		} catch (MessagingException e){
			logger.error("Unable to build MIME message.", e);
		}
	}
	
	@Override
	public void sendMailHtml(String from, String to, String subject, String msg) {
		if (initializationErrors){
			logger.error("The mail service is not configured properly. Nothing will be sent");
			return;
		}
		try {
			MimeMessage mimeMessage = mailSender.createMimeMessage();
			MimeMessageHelper message = new MimeMessageHelper(mimeMessage, true, "UTF-8");
			message.setSubject(subject);
			message.setFrom(from);
			message.setTo(to);
			message.setText(msg, true);
			deliverEmail(mimeMessage);
		} catch (MessagingException e){
			logger.error("Unable to build MIME message.", e);
		}
	}

	@Override
	public void sendMail(String from, String[] to, String subject, String msg) {
		if (initializationErrors){
			logger.error("The mail service is not configured properly. Nothing will be sent");
			return;
		}
		try {
			MimeMessage mimeMessage = mailSender.createMimeMessage();
			MimeMessageHelper message = new MimeMessageHelper(mimeMessage, true, "UTF-8");
			message.setSubject(subject);
			message.setFrom(from);
			message.setTo(to);
			message.setText(msg, false);
			deliverEmail(mimeMessage);
		} catch (MessagingException e){
			logger.error("Unable to build MIME message.", e);
		}
	}

	@Override
	public void sendFormattedMail(String from, String[] to, String subject,
			String template, Collection<TemplateVariable<?>> templateVariables) throws MessagingException {
		if (initializationErrors){
			logger.error("The mail service is not configured properly. Nothing will be sent");
			return;
		}
		Context ctx = new Context();
		for(TemplateVariable<?> variable: templateVariables){
			if (variable.getCollectionValue() != null){
				ctx.setVariable(variable.getName(), variable.getCollectionValue());
			} else {
				ctx.setVariable(variable.getName(), variable.getStringValue());
			}
		}
		String htmlContent = templateEngine.process(template, ctx);
		MimeMessage mimeMessage = mailSender.createMimeMessage();
		MimeMessageHelper message = new MimeMessageHelper(mimeMessage, true, "UTF-8");
		message.setSubject(subject);
		message.setFrom(from);
		message.setTo(to);
		message.setText(htmlContent, true);
		deliverEmail(mimeMessage);
	}
	
	private void deliverEmail(final MimeMessage msg){
		ExecutorService executor = Executors.newFixedThreadPool(1);
		ArrayList<Callable<Object>> tasks = new ArrayList<Callable<Object>>(1);
		tasks.add(new Callable<Object>(){
			@Override
			public Object call() throws Exception {
				mailSender.send(msg);
				return null;
			}
		});
		try {
			long currentTimestamp = System.currentTimeMillis();
			executor.invokeAll(tasks, deliveryTimeout, TimeUnit.SECONDS);
			if (System.currentTimeMillis() - currentTimestamp >= deliveryTimeout * 1000L)
				logger.warn("The email could not be delivered due to a timeout error.");
		} catch (InterruptedException e){}
		executor.shutdownNow();
	}
}