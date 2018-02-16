package com.indracompany.sofia2.config.repository;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class EncryptionTest {
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
