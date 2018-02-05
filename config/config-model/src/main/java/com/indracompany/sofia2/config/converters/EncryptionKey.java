package com.indracompany.sofia2.config.converters;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class EncryptionKey {

	@Value("${sofia2.encryption.key:sofia2}")
	private String key;

	public static String KEY = null;

	public void setKey(String key) {
		this.key = key;
		KEY = key;
	}

}
