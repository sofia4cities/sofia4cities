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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;

public abstract class KeyUtils {
	public abstract void generateKey(String privateKeyPath, String publicKeyPath)
			throws IOException, NoSuchAlgorithmException, FileNotFoundException;

	public static PublicKey readPublicKey(String publicKeyPath)
			throws FileNotFoundException, IOException {
		ObjectInputStream is = null;
		try {
			is = new ObjectInputStream(new FileInputStream(
					publicKeyPath));
			PublicKey publicKey = (PublicKey) is.readObject();
			return publicKey;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return null;
		} finally {
			if (is != null)
				is.close();
		}
	}

	public static PrivateKey readPrivateKey(String privateKeyPath)
			throws FileNotFoundException, IOException {
		ObjectInputStream is = null;
		try {
			is = new ObjectInputStream(new FileInputStream(
					privateKeyPath));
			PrivateKey privateKey = (PrivateKey) is.readObject();
			return privateKey;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return null;
		} finally {
			if (is != null)
				is.close();
		}
	}
}
