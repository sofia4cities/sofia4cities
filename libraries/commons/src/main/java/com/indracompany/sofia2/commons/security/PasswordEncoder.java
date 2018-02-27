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
package com.indracompany.sofia2.commons.security;

import java.security.MessageDigest;
import java.util.Base64;

public class PasswordEncoder {
	private static PasswordEncoder instance;
	private final static int ITERATION_COUNT = 5;

	private final static String SALT_KEY = "PveFT7isDjGYFTaYhc2Fzw==";

	private PasswordEncoder() {
	}

	public static synchronized PasswordEncoder getInstance() {
		if (instance == null) {
			PasswordEncoder returnPasswordEncoder = new PasswordEncoder();
			return returnPasswordEncoder;
		} else
			return instance;
	}

	public synchronized String encodeSHA256(String password) throws Exception {
		return encodeSHA256(password, SALT_KEY);

	}

	public synchronized String encodeSHA256(String password, String saltKey) throws Exception {
		String encodedPassword = null;
		byte[] salt = base64ToByte(saltKey);

		MessageDigest digest = MessageDigest.getInstance("SHA-256");
		digest.reset();
		digest.update(salt);

		byte[] btPass = digest.digest(password.getBytes("UTF-8"));
		for (int i = 0; i < ITERATION_COUNT; i++) {
			digest.reset();
			btPass = digest.digest(btPass);
		}

		encodedPassword = byteToBase64(btPass);
		return encodedPassword;
	}

	private byte[] base64ToByte(String str) throws Exception {
		return Base64.getDecoder().decode(str);
	}

	private String byteToBase64(byte[] bt) {
		return Base64.getEncoder().encodeToString(bt);
	}

	public static void main(String[] args) throws Exception {
		String password = "Secrete@343";
		String saltKey = "PveFT7isDjGYFTaYhc2Fzw==";
		String hash1, hash2 = null;

		// Assume from UI
		PasswordEncoder encoder1 = PasswordEncoder.getInstance();
		hash1 = encoder1.encodeSHA256(password, saltKey);
		System.out.println(hash1);

		// Assume the same present in db
		PasswordEncoder encoder2 = PasswordEncoder.getInstance();
		hash2 = encoder2.encodeSHA256(password, saltKey);
		System.out.println(hash2);

		if (hash1.equalsIgnoreCase(hash2))
			System.out.println("Both hash Matches..");
		else
			System.out.println("Hash matches fails..");
	}
}