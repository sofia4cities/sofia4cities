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

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class EncryptionExample {
	public static void main(String[] argv) {

		try {

			// KeyGenerator keygenerator = KeyGenerator.getInstance("DES");
			// SecretKey myDesKey = keygenerator.generateKey();
			SecretKey myDesKey = new SecretKeySpec("Sofia2En".getBytes(), "DES");

			Cipher desCipher = Cipher.getInstance("DES/ECB/PKCS5Padding");

			// Initialize the cipher for encryption
			desCipher.init(Cipher.ENCRYPT_MODE, myDesKey);

			// sensitive information
			byte[] text = "changeIt!".getBytes();

			System.out.println("Text [Byte Format] : " + text);
			System.out.println("Text : " + new String(text));

			// Encrypt the text
			byte[] textEncrypted = desCipher.doFinal(text);

			// String encrypted = new
			// String(Base64.encode(desCipher.doFinal(textEncrypted)), "UTF-8");

			System.out.println("Text Encrypted [Byte Format]: " + textEncrypted);
			System.out.println("Text Encrypted : " + new String(textEncrypted));
			// System.out.println("Text Encryted Base64 : " + encrypted);

			// Initialize the same cipher for decryption
			desCipher.init(Cipher.DECRYPT_MODE, myDesKey);

			// Decrypt the text
			byte[] textDecrypted = desCipher.doFinal(textEncrypted);

			// String decrypted = new
			// String(desCipher.doFinal(Base64.decode(textDecrypted)), "UTF-8");

			System.out.println("Text Decryted : " + new String(textDecrypted));
			// System.out.println("Text Decryted Base64: " + decrypted);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
