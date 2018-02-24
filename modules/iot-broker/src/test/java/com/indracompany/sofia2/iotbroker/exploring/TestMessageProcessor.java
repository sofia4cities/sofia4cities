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
package com.indracompany.sofia2.iotbroker.exploring;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.indracompany.sofia2.iotbroker.processor.MessageProcessorDelegate;
import com.indracompany.sofia2.iotbroker.ssap.generator.SSAPMessageGenerator;
import com.indracompany.sofia2.ssap.SSAPMessage;
import com.indracompany.sofia2.ssap.body.SSAPBodyJoinMessage;
import com.indracompany.sofia2.ssap.body.SSAPBodyReturnMessage;
import com.indracompany.sofia2.ssap.json.SSAPJsonParser;
import com.indracompany.sofia2.ssap.json.Exception.SSAPParseException;


@RunWith(SpringRunner.class)
@SpringBootTest
public class TestMessageProcessor {
	
	@Autowired
	MessageProcessorDelegate processor;
	
	SSAPJsonParser ssapParser;
	
	@Test
	public void test() throws SSAPParseException {
		SSAPMessage<SSAPBodyJoinMessage> message = SSAPMessageGenerator.generateJoinMessageWithToken();
		
		SSAPMessage<SSAPBodyReturnMessage> response =  processor.process(message);
		
		System.out.println(SSAPJsonParser.getInstance().serialize(response));
		
	}

}
