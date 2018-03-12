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
package com.indracompany.sofia2.iotbroker.plugable.impl.gateway.reference.jvm;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.indracompany.sofia2.iotbroker.processor.MessageProcessor;
import com.indracompany.sofia2.iotbroker.processor.GatewayNotifier;
import com.indracompany.sofia2.ssap.SSAPMessage;
import com.indracompany.sofia2.ssap.body.SSAPBodyReturnMessage;

@Component
public class JvmGateway {

	@Autowired
	MessageProcessor processor;

	@Autowired
	GatewayNotifier subscriptor;

	@PostConstruct
	private void init() {
		subscriptor.addSubscriptionListener("jmv",  (s) -> System.out.println("processing") );
	}

	public SSAPMessage<SSAPBodyReturnMessage> process(SSAPMessage request) {
		final SSAPMessage<SSAPBodyReturnMessage> response = processor.process(request);
		return response;
	}

}
