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
package com.indracompany.sofia2.config.converters;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.spec.AlgorithmParameterSpec;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class CipherInitializer {

	private static final String CIPHER_INSTANCE_NAME = "AES/CBC/PKCS5Padding";
	private static final String SECRET_KEY_ALGORITHM = "AES";

	public Cipher prepareAndInitCipher(int encryptionMode, String key) throws InvalidKeyException,
			NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException {
		Cipher cipher = Cipher.getInstance(CIPHER_INSTANCE_NAME);
		Key secretKey = new SecretKeySpec(key.getBytes(), SECRET_KEY_ALGORITHM);
		AlgorithmParameterSpec algorithmParameters = getAlgorithmParameterSpec(cipher);

		callCipherInit(cipher, encryptionMode, secretKey, algorithmParameters);
		return cipher;
	}

	void callCipherInit(Cipher cipher, int encryptionMode, Key secretKey, AlgorithmParameterSpec algorithmParameters)
			throws InvalidKeyException, InvalidAlgorithmParameterException {
		cipher.init(encryptionMode, secretKey, algorithmParameters);
	}

	int getCipherBlockSize(Cipher cipher) {
		return cipher.getBlockSize();
	}

	private AlgorithmParameterSpec getAlgorithmParameterSpec(Cipher cipher) {
		byte[] iv = new byte[getCipherBlockSize(cipher)];
		return new IvParameterSpec(iv);
	}

}
