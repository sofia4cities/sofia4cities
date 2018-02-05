package com.indracompany.sofia2.config.converters;

import org.springframework.beans.factory.annotation.Value;

//@Component
public class KeyProperty {

	public static String ENCRYPTION_KEY;

	@Value("${sofia2.encryption.key:sofia2}")
	public void setDatabase(String encryptionKey) {
		ENCRYPTION_KEY = encryptionKey;
	}

}
