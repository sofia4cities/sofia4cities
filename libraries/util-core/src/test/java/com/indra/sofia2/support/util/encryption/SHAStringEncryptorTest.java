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

import java.security.NoSuchAlgorithmException;

import org.junit.Test;

import com.indra.sofia2.support.util.encryption.SHAStringEncryptor;

import junit.framework.TestCase;

public class SHAStringEncryptorTest extends TestCase {

	public SHAStringEncryptorTest(String name) {
		super(name);
	}	

	@Test
	/*
	 * Checked with http://hash.online-convert.com/sha256-generator.
	 */
	public void testEncrypt2StringWithSorting() throws NoSuchAlgorithmException {
		String input = "Sofia 2 rules!!";
		SHAStringEncryptor shaEnc = new SHAStringEncryptor("SHA-256", false);
		String output = shaEnc.encrypt2String(input);
		String expected_output = "03d7299d91523938d44314f7225f871ef18b1e1c2d585ed8495e520666deaca1";		
		assertEquals(output, expected_output);		
	}
	
	@Test
	/*
	 * Checked with http://www.tools4noobs.com/online_php_functions/sha1/
	 */
	public void testEncrypt2StringWithoutSorting() throws NoSuchAlgorithmException {
		String input = "This is working@@@###!!!!";
		SHAStringEncryptor shaEnc = new SHAStringEncryptor("SHA-1", false);
		String output = shaEnc.encrypt2String(input);
		String expected_output = "bd469a1da5d33600456cb1145334972efa51767e";
		assertEquals(output, expected_output);
	}
}