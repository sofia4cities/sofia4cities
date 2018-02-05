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
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.persistence.AttributeConverter;

abstract class AbstractCryptoConverter<T> implements AttributeConverter<T, String> {

	private CipherInitializer cipherInitializer;

	public AbstractCryptoConverter() {
		this(new CipherInitializer());
	}

	public AbstractCryptoConverter(CipherInitializer cipherInitializer) {
		this.cipherInitializer = cipherInitializer;
	}

	@Override
	public String convertToDatabaseColumn(T attribute) {
		if (isNotEmpty(EncryptionKey.KEY) && isNotNullOrEmpty(attribute)) {
			try {
				Cipher cipher = cipherInitializer.prepareAndInitCipher(Cipher.ENCRYPT_MODE, EncryptionKey.KEY);
				return encrypt(cipher, attribute);
			} catch (NoSuchAlgorithmException | InvalidKeyException | InvalidAlgorithmParameterException
					| BadPaddingException | NoSuchPaddingException | IllegalBlockSizeException e) {
				throw new RuntimeException(e);
			}
		}
		return entityAttributeToString(attribute);
	}

	boolean isNotEmpty(String cs) {
		return cs == null || cs.length() == 0;
	}

	@Override
	public T convertToEntityAttribute(String dbData) {
		if (isNotEmpty(EncryptionKey.KEY) && isNotEmpty(dbData)) {
			try {
				Cipher cipher = cipherInitializer.prepareAndInitCipher(Cipher.DECRYPT_MODE, EncryptionKey.KEY);
				return decrypt(cipher, dbData);
			} catch (NoSuchAlgorithmException | InvalidKeyException | InvalidAlgorithmParameterException
					| BadPaddingException | NoSuchPaddingException | IllegalBlockSizeException e) {
				throw new RuntimeException(e);
			}
		}
		return stringToEntityAttribute(dbData);
	}

	abstract boolean isNotNullOrEmpty(T attribute);

	abstract T stringToEntityAttribute(String dbData);

	abstract String entityAttributeToString(T attribute);

	byte[] callCipherDoFinal(Cipher cipher, byte[] bytes) throws IllegalBlockSizeException, BadPaddingException {
		return cipher.doFinal(bytes);
	}

	private String encrypt(Cipher cipher, T attribute) throws IllegalBlockSizeException, BadPaddingException {
		byte[] bytesToEncrypt = entityAttributeToString(attribute).getBytes();
		byte[] encryptedBytes = callCipherDoFinal(cipher, bytesToEncrypt);
		return Base64.getEncoder().encodeToString(encryptedBytes);
	}

	private T decrypt(Cipher cipher, String dbData) throws IllegalBlockSizeException, BadPaddingException {
		byte[] encryptedBytes = Base64.getDecoder().decode(dbData);
		byte[] decryptedBytes = callCipherDoFinal(cipher, encryptedBytes);
		return stringToEntityAttribute(new String(decryptedBytes));
	}
}
