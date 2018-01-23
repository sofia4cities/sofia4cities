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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PasswordValidator {
	
	private Pattern pattern;
	
	private boolean usernameCheck;
	
	private int minLength;
	
	private int maxLength;
	
	public PasswordValidator(String password_regexp, boolean username_check, int min_length, int max_length){
		this.pattern = Pattern.compile(password_regexp);
		this.minLength = min_length;
		this.maxLength = max_length;
		this.usernameCheck = username_check;
	}

	public boolean validate(String username, String password){
		Matcher matcher = this.pattern.matcher(password);
		boolean result = matcher.matches() && this.minLength <= password.length() && this.maxLength >= password.length();
		if (this.usernameCheck ){
			result = result && !password.toLowerCase().contains(username.toLowerCase());
		}
		return result;
	}
}
