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
package com.indracompany.sofia2.ssap.util;

import com.indracompany.sofia2.ssap.SSAPMessage;
import com.indracompany.sofia2.ssap.body.SSAPBodyReturnMessage;
import com.indracompany.sofia2.ssap.body.parent.SSAPBodyMessage;
import com.indracompany.sofia2.ssap.enums.SSAPErrorCode;
import com.indracompany.sofia2.ssap.enums.SSAPMessageDirection;

public class SSAPMessageGenerator {

	public static SSAPMessage<SSAPBodyReturnMessage> generateResponseErrorMessage(SSAPMessage<? extends SSAPBodyMessage> message,
			SSAPErrorCode errorCode, String errorMessage) {
		SSAPMessage<SSAPBodyReturnMessage> m = null;
		m = new SSAPMessage<>();
		m.setDirection(SSAPMessageDirection.ERROR);
		m.setMessageId(message.getMessageId());
		m.setSessionKey(message.getSessionKey());
		m.setBody(new SSAPBodyReturnMessage());
		m.getBody().setOk(false);
		m.getBody().setErrorCode(errorCode);
		m.getBody().setError(errorMessage);

		return m;
	}
}
