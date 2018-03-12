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
package com.indracompany.sofia2.iotbroker.processor;

import java.util.function.Consumer;
import java.util.function.Function;

import com.indracompany.sofia2.ssap.SSAPMessage;
import com.indracompany.sofia2.ssap.body.SSAPBodyCommandMessage;
import com.indracompany.sofia2.ssap.body.SSAPBodyIndicationMessage;
import com.indracompany.sofia2.ssap.body.SSAPBodyReturnMessage;

public interface GatewayNotifier {

	void addSubscriptionListener(String key, Consumer<SSAPMessage<SSAPBodyIndicationMessage>> indication);

	void addCommandListener(String key, Function<SSAPMessage<SSAPBodyCommandMessage>, SSAPMessage<SSAPBodyReturnMessage>> command);

	void notify(SSAPMessage<SSAPBodyIndicationMessage> indication);

	void sendCommandAsync(SSAPMessage<SSAPBodyCommandMessage> command);

	//	SSAPMessage<SSAPBodyReturnMessage> sendCommandSync(SSAPMessage<SSAPBodyCommandMessage> cmd);



}
