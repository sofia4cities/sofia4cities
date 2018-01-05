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
		SSAPMessage<SSAPBodyJoinMessage> message = SSAPMessageGenerator.generateJoinMessage();
		
		SSAPMessage<SSAPBodyReturnMessage> response =  processor.process(message);
		
		System.out.println(SSAPJsonParser.getInstance().serialize(response));
		
	}

}
