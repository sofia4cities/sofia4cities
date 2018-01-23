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
package com.indra.sofia2.support.util.encryption.tools;

import org.junit.Test;

import com.indra.sofia2.support.util.encryption.tools.HexConverter;

import junit.framework.TestCase;

public class HexConverterTest extends TestCase {

	public HexConverterTest(String name) {
		super(name);
	}

	private static byte[] hexStringToByteArray(String s) {
		int len = s.length();
		byte[] data = new byte[len / 2];
		for (int i = 0; i < len; i += 2) {
			data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character
					.digit(s.charAt(i + 1), 16));
		}
		return data;
	}

	@Test
	public void testConversion() {
		String input = "75546f7272656e745c436f6d706c657465645c6e667375635f6f73745f62795f6d757374616e675c50656e64756c756d2d392c303030204d696c65732e6d7033006d7033006d7033004472756d202620426173730050656e64756c756d00496e2053696c69636f00496e2053696c69636f2a3b2a0050656e64756c756d0050656e64756c756d496e2053696c69636f303038004472756d2026204261737350656e64756c756d496e2053696c69636f30303800392c303030204d696c6573203c4d757374616e673e50656e64756c756d496e2053696c69636f3030380050656e64756c756d50656e64756c756d496e2053696c69636f303038004d50330000";
		byte[] hex_input = hexStringToByteArray(input);
		String conversion = HexConverter.toHexString(hex_input);
		assertEquals(input, conversion);
	}
}
