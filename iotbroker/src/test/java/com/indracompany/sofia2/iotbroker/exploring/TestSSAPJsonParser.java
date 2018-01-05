package com.indracompany.sofia2.iotbroker.exploring;

import org.junit.Test;

import com.indracompany.sofia2.common.exception.BaseException;
import com.indracompany.sofia2.iotbroker.ssap.generator.SSAPMessageGenerator;
import com.indracompany.sofia2.ssap.SSAPMessage;
import com.indracompany.sofia2.ssap.body.SSAPBodyJoinMessage;
import com.indracompany.sofia2.ssap.json.SSAPJsonParser;

//@RunWith(SpringRunner.class)
//@SpringBootTest
//SpringBoot context not necessary to execute this tests 
public class TestSSAPJsonParser {
	
	SSAPJsonParser parser = SSAPJsonParser.getInstance();
	@Test
	public void test() throws BaseException {
		SSAPMessage<SSAPBodyJoinMessage> message = SSAPMessageGenerator.generateJoinMessage();
		String strMessage = parser.serialize(message);
		
		SSAPMessage ssapMessage = parser.deserialize(strMessage);
		
		System.out.println(parser.serialize(ssapMessage));
        
	}

}
