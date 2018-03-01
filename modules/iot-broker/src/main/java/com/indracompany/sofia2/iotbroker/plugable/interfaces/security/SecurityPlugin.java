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
package com.indracompany.sofia2.iotbroker.plugable.interfaces.security;

import java.util.Optional;

import com.indracompany.sofia2.ssap.enums.SSAPMessageTypes;

public interface SecurityPlugin {
	public Optional<IoTSession> authenticate(String token, String clientPlatform, String clientPlatformInstance);

	public boolean closeSession(String sessionKey);

	public boolean checkSessionKeyActive(String sessionKey);

	public boolean checkAuthorization(SSAPMessageTypes messageType, String ontology, String sessionKey);

	public Optional<IoTSession> getSession(String sessionKey);
}
