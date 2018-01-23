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

public class HexConverter {
	public static String toHexString(byte[] input){
		StringBuilder result = new StringBuilder();
		for (int i = 0; i < input.length; i++) {
			String hex_code = Integer.toHexString(0xff & input[i]);
			String leading_zero = "";
			if (hex_code.length() == 1)
				leading_zero = "0";
			result.append(leading_zero + hex_code);
		}
		return result.toString();
	}
}
