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
package com.indra.sofia2.support.util.cripto;

import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.UnrecoverableKeyException;

import org.apache.commons.configuration.ConfigurationException;

public class KeyStoreData {
	
	private KeyStore ks;
	private char[] keystorePassword;
	private String keystoreName;
	
	public KeyStoreData(String keystoreFile, String keystorePassword, String keystoreName) throws ConfigurationException{
		if (keystoreFile!=null&&!keystoreFile.trim().equals("")
				&&keystorePassword!=null&&!keystorePassword.trim().equals("")){
			try{
				ks = KeyStore.getInstance(KeyStore.getDefaultType());
				FileInputStream fis = new FileInputStream(keystoreFile);
				this.keystorePassword=keystorePassword.toCharArray();
				this.keystoreName=keystoreName;
				ks.load(fis, this.keystorePassword);
			}catch (Exception e) {
				throw new ConfigurationException(e);
			}
		}else{
			throw new ConfigurationException();
		}
	}
	
	public PrivateKey getPrivateKey() throws UnrecoverableKeyException, KeyStoreException, NoSuchAlgorithmException{
		return getPrivateKey(keystoreName);
	}
	
	public PrivateKey getPrivateKey(String keystoreName) throws UnrecoverableKeyException, KeyStoreException, NoSuchAlgorithmException{
		return (PrivateKey) ks.getKey(keystoreName, keystorePassword);
	}
	
	public PublicKey getPublicKey() throws UnrecoverableKeyException, KeyStoreException, NoSuchAlgorithmException{
		return getPublicKey(keystoreName);
	}
	
	public PublicKey getPublicKey(String keystoreName) throws UnrecoverableKeyException, KeyStoreException, NoSuchAlgorithmException{
		return ks.getCertificate(keystoreName).getPublicKey();
	}
	
}
