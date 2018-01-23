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
/*******************************************************************************
 * © Indra Sistemas, S.A.
 * 2013 - 2015  SPAIN
 * 
 * All rights reserved
 ******************************************************************************/

package com.indra.sofia2.support.util.encryption;

import java.security.NoSuchAlgorithmException;

import org.apache.commons.codec.binary.Base64;

public class Base64StringEncryptor extends StringEncryptor {

	@Override
	public byte[] encrypt2Bytes(String input) throws NoSuchAlgorithmException {
		byte[] inputBytes = input.getBytes();
		return Base64.encodeBase64(inputBytes);
	}

	@Override
	public int getMinEncryptedMessageLength() {
		return 32;
	}

	@Override
	public byte[] encrypt2BytesSF2(String input) throws NoSuchAlgorithmException {
		return encrypt2Bytes(input);
	}

}
