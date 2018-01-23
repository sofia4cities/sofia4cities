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
package com.indra.sofia2.support.util.encryption;

import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;

import javax.crypto.Cipher;

public class RSAStringEncryptor extends StringEncryptor {

	private static final String DEFAULT_ALGORITHM = "RSA-2048";

	private String algorithm;

	private PrivateKey privateKey;

	public RSAStringEncryptor(String algorithm, PrivateKey privateKey) {
		this.algorithm = algorithm;
		this.privateKey = privateKey;
	}

	public RSAStringEncryptor(PrivateKey privateKey){
		this.privateKey = privateKey;
		this.algorithm = DEFAULT_ALGORITHM;
	}

	@Override
	public byte[] encrypt2Bytes(String input) throws NoSuchAlgorithmException{
		byte[] cipherText = null;
		try {
			final Cipher cipher = Cipher.getInstance(this.algorithm);
			cipher.init(Cipher.ENCRYPT_MODE, this.privateKey);
			cipherText = cipher.doFinal(input.getBytes());
		} catch (NoSuchAlgorithmException e) {
			throw e;
		} catch (Exception e){
			e.printStackTrace();
		}
		return cipherText;
	}
	
	@Override
	public int getMinEncryptedMessageLength() {
		return 32;
	}

	@Override
	public byte[] encrypt2BytesSF2(String input) throws NoSuchAlgorithmException {
		byte[] cipherText = null;
		try{
			final Cipher cipher = Cipher.getInstance(this.algorithm);
			cipher.init(Cipher.ENCRYPT_MODE, this.privateKey);
			cipherText = cipher.doFinal(input.getBytes());
		}catch(NoSuchAlgorithmException e){
			throw e;
		}catch(Exception e){
			e.printStackTrace();
		}
		return cipherText;
	}
}