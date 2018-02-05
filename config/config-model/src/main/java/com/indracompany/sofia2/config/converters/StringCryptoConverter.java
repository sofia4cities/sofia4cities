package com.indracompany.sofia2.config.converters;

import javax.persistence.Converter;

@Converter
public class StringCryptoConverter extends AbstractCryptoConverter<String> {

	public StringCryptoConverter() {
		this(new CipherInitializer());
	}

	public StringCryptoConverter(CipherInitializer cipherInitializer) {
		super(cipherInitializer);
	}

	@Override
	boolean isNotNullOrEmpty(String cs) {
		return cs == null || cs.length() == 0;
	}

	@Override
	String stringToEntityAttribute(String dbData) {
		return dbData;
	}

	@Override
	String entityAttributeToString(String attribute) {
		return attribute;
	}
}
