package com.indracompany.sofia2.examples;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ApiRestController {

	private static final Logger LOG =   LoggerFactory.getLogger(ApiRestController.class);
	
    @RequestMapping("/greeting")
    public String index() {
        return "Greetings from Spring Boot!";
    }
    
    @RequestMapping("/message")
    public String message(@RequestParam("msg") String msgRecieved) {
    	
    	LOG.info("MSG RECIEVED: " +  msgRecieved);
    	String msg = processMsg(msgRecieved);
    	
    	return msg;
    }

	private String processMsg(String msgRecieved) {
		String msg;
		List<String> buttons = new ArrayList<String>();
		
		switch (msgRecieved) {
		case "Hi":
		case "hi":
		case "HI":
			msg = "Hi! I swear I will not kill anyone. Trust me.";
			break;
		case "orders":
			msg = "These are things that I can never do";
			buttons.add("Order 1");
			buttons.add("Order 2");
			break;
		default:
			msg = "zzz";
			break;
		}
		StringBuilder json = new StringBuilder();
		json.append("{");
		json.append('"');
		json.append("msg");
		json.append('"');
		json.append(':');
		json.append('"');
		json.append(msg);
		json.append('"');
		json.append(',');
		json.append('"');
		json.append("buttons");
		json.append('"');
		json.append(':');
		json.append('[');
		Iterator<String> it = buttons.iterator();
		while (it.hasNext()) {
			json.append('"');
			json.append(it.next());
			json.append('"');
			if (it.hasNext()) {
				json.append(',');
			}
		}
		json.append(']');
		json.append("}");
		
		return json.toString();
	}

}
