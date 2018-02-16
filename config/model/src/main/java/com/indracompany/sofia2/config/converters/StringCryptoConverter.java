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
