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
package com.indra.sofia2.support.util.cripto;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.configuration.ConfigurationException;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

public class Sofia2Cripto {

	private Cipher cipherEnc;
	private Cipher cipherDec;
	
	public Sofia2Cripto(String password, String algorithm, KeyStoreData keysoreData) throws ConfigurationException{
		if (algorithm!=null&&!algorithm.trim().equals("")){
			try{
				if (algorithm.equals("RSA")){
					if (keysoreData!=null){
						cipherDec.init(Cipher.DECRYPT_MODE, keysoreData.getPrivateKey());
						cipherEnc.init(Cipher.ENCRYPT_MODE, keysoreData.getPublicKey());
					}else{
						throw new ConfigurationException();
					}
				}else{
					if (password!=null&&!password.trim().equals("")){
						String pwdStr = new String(password);
						byte[] keyBytes = pwdStr.getBytes();
						SecretKeySpec secretKey = new SecretKeySpec(keyBytes, algorithm);
						cipherDec = Cipher.getInstance(algorithm);
						cipherEnc = Cipher.getInstance(algorithm);
						cipherDec.init(Cipher.DECRYPT_MODE, secretKey);
						cipherEnc.init(Cipher.ENCRYPT_MODE, secretKey);
					}else{
						throw new ConfigurationException();
					}
				}
			}catch (Exception e) {
				throw new ConfigurationException(e);
			}
		}else{
			throw new ConfigurationException();
		}
	}

	public String encrypt(String text) throws IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException{
		byte[] data = text.getBytes("UTF8");
		byte[] encrypted = cipherEnc.doFinal(data);
		return new BASE64Encoder().encode(encrypted);
	}
	
	public String decrypt(String text) throws IOException, IllegalBlockSizeException, BadPaddingException{
		byte[] data = new BASE64Decoder().decodeBuffer(text);
		byte[] encrypted = cipherDec.doFinal(data);
		return new String(encrypted);
	}
}
