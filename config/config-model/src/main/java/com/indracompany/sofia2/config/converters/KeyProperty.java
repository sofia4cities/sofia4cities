package com.indracompany.sofia2.config.converters;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class KeyProperty {

	public static String ENCRYPTION_KEY;

	@Value("${sofia2.encryption.key}")
	public void setDatabase(String encryptionKey) {
		ENCRYPTION_KEY = encryptionKey;
	}

}
