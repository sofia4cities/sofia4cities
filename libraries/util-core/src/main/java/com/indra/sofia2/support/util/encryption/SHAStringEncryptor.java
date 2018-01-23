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
 * 2013 - 2014  SPAIN
 * 
 * All rights reserved
 ******************************************************************************/
package com.indra.sofia2.support.util.encryption;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SHAStringEncryptor extends StringEncryptor {
	
	private static final String DEFAULT_ALGORITHM = "SHA-1";
	
	private static final String STRING_TO_APPEND = "$Er5ç(O";
	
	private static final String STRING_TO_SF2 ="SF2";
	
	private String algorithm;
	
	private boolean appendRandomString;
	
	public SHAStringEncryptor(String algorithm, boolean appendRandomString) {
		this.algorithm = algorithm;
		this.appendRandomString = appendRandomString;
	}
	
	public SHAStringEncryptor() {
		this(DEFAULT_ALGORITHM, true);
	}
	//Correc: Renombrada variable local
	@Override
	public byte[] encrypt2Bytes(String input) throws NoSuchAlgorithmException {
		MessageDigest md = MessageDigest.getInstance(this.algorithm);	
		String inputAux = input;
		if (this.appendRandomString){
			inputAux = inputAux + STRING_TO_APPEND;
		}
		byte[] output = md.digest(inputAux.getBytes());
		return output;
	}
	//Correc: Renombrada variable local
	@Override
	public byte[] encrypt2BytesSF2(String input) throws NoSuchAlgorithmException{
		MessageDigest md = MessageDigest.getInstance(this.algorithm);
		String inputAux = input.concat(STRING_TO_SF2);
		byte[] output = md.digest(inputAux.getBytes());
		return output;
	}
	
	@Override
	public int getMinEncryptedMessageLength() {
		return 64;
	}

}