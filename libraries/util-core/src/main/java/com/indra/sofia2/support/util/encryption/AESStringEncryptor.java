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
package com.indra.sofia2.support.util.encryption;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class AESStringEncryptor {

	private static byte[] encrypt(String plainText, String encryptionKey) throws Exception {
	    Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding", "SunJCE");
	    SecretKeySpec key = new SecretKeySpec(encryptionKey.getBytes("UTF-8"), "AES");
	    cipher.init(Cipher.ENCRYPT_MODE, key,new IvParameterSpec(AESKey.IV.getBytes("UTF-8")));
	    return cipher.doFinal(plainText.getBytes("UTF-8"));
	  }

	private static String prepareInput(String plainText) {
		while ((plainText.length() % 16 != 0) || (plainText.length() == 0))
			plainText = plainText.concat("\0");
		return plainText;
	}

	public static byte[] encrypt2String(String[] args) {
		if (args.length <= 0 || args.length >= 3) {
			System.err.println("Introduce: \n" + "\n1)EncryptionKey. 2)Text to encrypt it.");
			return null;
		}
		if (args[0].length() != 16) {
			System.err.println("The encryptionKey must have 16 characters.");
		}
		String plainText;
		if (args.length == 1)
			plainText = prepareInput("");
		else
			plainText = prepareInput(args[1]);
		try {
			String encryptionKey = args[0];
			System.out.println("Plain text:		" + plainText);
			byte[] cipher = encrypt(plainText, encryptionKey);
			for (int i = 0; i < cipher.length; i++)
				System.out.print(new Integer(cipher[i]) + " ");
			return cipher;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	public static void main(String[] args) {
		encrypt2String(args);
	}
}
