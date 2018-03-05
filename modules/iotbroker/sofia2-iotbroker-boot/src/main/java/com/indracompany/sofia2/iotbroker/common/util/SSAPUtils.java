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
package com.indracompany.sofia2.iotbroker.common.util;

import com.indracompany.sofia2.ssap.SSAPMessage;
import com.indracompany.sofia2.ssap.body.SSAPBodyReturnMessage;
import com.indracompany.sofia2.ssap.enums.SSAPErrorCode;
import com.indracompany.sofia2.ssap.enums.SSAPMessageDirection;

public class SSAPUtils {

	public static SSAPMessage<SSAPBodyReturnMessage> generateErrorMessage(SSAPMessage message, SSAPErrorCode code, String error) {
		final SSAPMessage<SSAPBodyReturnMessage> responseMessage = new SSAPMessage<>();
		responseMessage.setDirection(SSAPMessageDirection.ERROR);
		responseMessage.setMessageId(message.getMessageId());
		responseMessage.setMessageType(message.getMessageType());
		//		responseMessage.setOntology(message.getOntology());
		responseMessage.setSessionKey(message.getSessionKey());
		responseMessage.setBody(new SSAPBodyReturnMessage());
		responseMessage.getBody().setOk(false);
		responseMessage.getBody().setErrorCode(code);
		responseMessage.getBody().setError(error);

		return responseMessage;
	}
}
