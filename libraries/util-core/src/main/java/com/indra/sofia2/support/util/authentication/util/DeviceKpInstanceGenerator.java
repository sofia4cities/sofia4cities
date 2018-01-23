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
package com.indra.sofia2.support.util.authentication.util;

import java.security.NoSuchAlgorithmException;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.indra.sofia2.support.entity.gestion.dominio.Asset;
import com.indra.sofia2.support.util.encryption.StringEncryptor;
import com.indra.sofia2.support.util.encryption.StringEncryptorFactory;


@Component
@Lazy
public class DeviceKpInstanceGenerator {
	
	@Value("${energy.security.deviceCredentials.hashingFunction:SHA-1}")
	private String algorithm;
	
	@Value("${energy.security.deviceCredentials.kpName:ITACApp}")
	private String kpName;
	
	@Value("${energy.security.deviceCredentials.defaultKpInstance:ITACApp01}")
	private String defaultKpInstanceId;
	
	@Value("${energy.security.deviceCredentials.hashing.seed:SF2}")
	private String seed;
	
	private StringEncryptor stringEncryptor;
	
	@PostConstruct
	public void init() throws NoSuchAlgorithmException {
		stringEncryptor = StringEncryptorFactory.buildEncryptor(algorithm, null, false);
		stringEncryptor.encrypt2String("");
	}
	
	public String generateKpInstance(Asset asset) {
		String dataToEncrypt = asset.getMac() + asset.getNumserie() + seed;
		try {
			return stringEncryptor.encrypt2String(dataToEncrypt);
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}
	
	public String getKpName() {
		return kpName;
	}
	
	public String getDefaultKpInstanceId() {
		return defaultKpInstanceId;
	}
}
