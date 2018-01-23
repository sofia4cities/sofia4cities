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
package com.indra.sofia2.support.util.encryption.tools;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;

public class RSAKeyUtils extends KeyUtils {

	public static final String ALGORITHM = "RSA";

	public static final int DEFAULT_KEY_LENGTH = 1024;

	private int keyLength;

	public RSAKeyUtils() {
		this(DEFAULT_KEY_LENGTH);
	}

	public RSAKeyUtils(int key_length) {
		this.keyLength = key_length;
	}

	public void generateKey(String privateKeyPath, String publicKeyPath)
			throws IOException, NoSuchAlgorithmException, FileNotFoundException {
		// TODO Auto-generated method stub
		KeyPairGenerator keyGen = KeyPairGenerator.getInstance(ALGORITHM);
		keyGen.initialize(keyLength);
		KeyPair key = keyGen.generateKeyPair();

		File privateKeyFile = new File(privateKeyPath);
		File publicKeyFile = new File(publicKeyPath);

		// Create files to store public and private key
		if (privateKeyFile.getParentFile() != null) {
			privateKeyFile.getParentFile().mkdirs();
		}
		privateKeyFile.createNewFile();

		if (publicKeyFile.getParentFile() != null) {
			publicKeyFile.getParentFile().mkdirs();
		}
		publicKeyFile.createNewFile();

		// Saving the Public key in a file
		ObjectOutputStream publicKeyOS = new ObjectOutputStream(
				new FileOutputStream(publicKeyFile));
		publicKeyOS.writeObject(key.getPublic());
		publicKeyOS.close();

		// Saving the Private key in a file
		ObjectOutputStream privateKeyOS = new ObjectOutputStream(
				new FileOutputStream(privateKeyFile));
		privateKeyOS.writeObject(key.getPrivate());
		privateKeyOS.close();

	}

	public static void main(String[] args) throws NoSuchAlgorithmException,
			FileNotFoundException, IOException {
		RSAKeyUtils rsakeyGen = new RSAKeyUtils(2048);
		rsakeyGen.generateKey("C:\\private.key", "C:\\public.key");
	}
}
