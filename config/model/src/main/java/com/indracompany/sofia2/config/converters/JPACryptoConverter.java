package com.indracompany.sofia2.config.converters;

import java.util.Properties;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import lombok.extern.slf4j.Slf4j;

@Converter
@Slf4j
public class JPACryptoConverter implements AttributeConverter<String, String> {

	public static String ALGORITM = null;
	public static String KEYSPEC = null;
	public static byte[] SECRET_KEY = null;

	private static boolean encrypt = true;

	static final Properties properties = new Properties();
	static {
		try {
			properties.load(
					JPACryptoConverter.class.getClassLoader().getResourceAsStream("sofia2_encryption.properties"));
			ALGORITM = (String) properties.get("sofia2.encryption.algorithm");
			KEYSPEC = (String) properties.get("sofia2.encryption.keyspec");
			SECRET_KEY = ((String) properties.get("sofia2.encryption.secretkey")).getBytes();

		} catch (Exception e) {
			log.warn("Could not load properties file 'sofia2_encryption.properties'...ignoring encryption.");
			encrypt = false;
		}

	}

	@Override
	public String convertToDatabaseColumn(String sensitive) {
		if (!encrypt)
			return sensitive;
		try {
			SecretKey myDesKey = new SecretKeySpec(SECRET_KEY, KEYSPEC);
			Cipher desCipher = Cipher.getInstance(ALGORITM);
			// Initialize the cipher for encryption
			desCipher.init(Cipher.ENCRYPT_MODE, myDesKey);
			byte[] textEncrypted = desCipher.doFinal(sensitive.getBytes());
			return new String(textEncrypted);
		} catch (Exception e) {
			log.error("Error in convertToDatabaseColumn:" + e.getMessage());
			throw new RuntimeException(e);
		}

	}

	@Override
	public String convertToEntityAttribute(String sensitive) {
		if (!encrypt)
			return sensitive;
		try {
			SecretKey myDesKey = new SecretKeySpec(SECRET_KEY, KEYSPEC);
			Cipher desCipher = Cipher.getInstance(ALGORITM);

			// Initialize the cipher for encryption
			desCipher.init(Cipher.DECRYPT_MODE, myDesKey);
			byte[] recoveredBytes = desCipher.doFinal(sensitive.getBytes());
			return new String(recoveredBytes);
		} catch (Exception e) {
			log.error("Error in convertToEntityAttribute:" + e.getMessage());
			throw new RuntimeException(e);
		}
	}
}