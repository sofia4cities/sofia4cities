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
package com.indra.sofia2.support.util.passwords.validation;

import org.junit.Test;

import com.indra.sofia2.support.util.passwords.validation.PasswordValidator;

import junit.framework.TestCase;

public class PasswordValidatorTest extends TestCase {

	public PasswordValidatorTest(String name) {
		super(name);
	}
	
	@Test
	public void testDummyValidation(){
		PasswordValidator validator = new PasswordValidator(".*", false, 1, 20);
		assertTrue(validator.validate("pepe", "pepe"));
	}
	
	@Test
	public void testUsernameValidation(){
		PasswordValidator validator = new PasswordValidator(".*", true, 6, 20);
		assertFalse(validator.validate("pepe", "pepe"));
		assertFalse(validator.validate("pepe", "IndrapePe01"));
		assertTrue(validator.validate("pepe", "Indrapep01"));
	}
	
	@Test
	public void testContentValidation(){
		/* At least 1 digit, 1 lowercase letter, 1 uppercase letter
		 * and a length between 6 and 20 characters
		 */
		String password_pattern = "^((?=.*\\p{Digit})(?=.*\\p{Lower})(?=.*\\p{Upper})" +
				"| (?=.*\\p{Digit})(?=.*\\p{Upper})(?=.*\\p{Lower})" +
				"| (?=.*\\p{Lower})(?=.*\\p{Upper})(?=.*\\p{Digit})" +
				"| (?=.*\\p{Lower})(?=.*\\p{Digit})(?=.*\\p{Upper})" +
				"| (?=.*\\p{Upper})(?=.*\\p{Lower})(?=.*\\p{Digit})" +
				"| (?=.*\\p{Upper})(?=.*\\p{Digit})(?=.*\\p{Lower})).*$";
		PasswordValidator validator = new PasswordValidator(password_pattern, true, 6, 20);
		assertFalse(validator.validate("username", "sH@r9"));
		assertTrue(validator.validate("username", "9eAddddddddd"));
		assertTrue(validator.validate("username", "e4F9sdfwer"));
		assertTrue(validator.validate("username", "Indra2014!"));
		assertFalse(validator.validate("username", "pAs$wor%d002too lllllllllllllllloooooooooooonnnnnnng"));
	}
}