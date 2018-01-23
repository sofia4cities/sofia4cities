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

import java.util.Collection;

import javax.mail.MessagingException;

public interface MailService {

	/**
	 * Sends a plain-text email to a single email address.
	 * @param from
	 * @param to
	 * @param subject
	 * @param msg
	 */
	void sendMail(String from, String to, String subject, String msg);

	/**
	 * Sends a plain-text email to multiple email addresses.
	 * @param from
	 * @param to
	 * @param subject
	 * @param msg
	 */
	void sendMail(String from, String[] to, String subject, String msg);
	
	/**
	 * Sends a html email to a single email address.
	 * @param from
	 * @param to
	 * @param subject
	 * @param msg
	 */
	public void sendMailHtml(String from, String to, String subject, String msg);
	
	/**
	 * Sends a HTML email to multiple email addresses.
	 * @param from
	 * @param to
	 * @param subject
	 * @param template The name of the Thymeleaf template file.
	 * @param templateVariables The values of the variables to be replaced in the template.
	 * @throws MessagingException
	 */
	void sendFormattedMail(String from, String[] to, String subject, String template,
			Collection<TemplateVariable<?>> templateVariables) throws MessagingException;
	
}
