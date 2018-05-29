package com.indracompany.sofia2.examples;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class ApiRestController {

	private static final Logger LOG =   LoggerFactory.getLogger(ApiRestController.class);
	
	private final RestTemplate restTemplate = new RestTemplate();
	
    @RequestMapping("/greeting")
    public String index() {
        return "Greetings from Spring Boot!";
    }
    
    @RequestMapping("/message")
    public String message(@RequestParam("msg") String msgReceived) {
    	
    	LOG.info("MSG RECIEVED: " +  msgReceived);
    	//String msg = processMsg(msgRecieved);
    	
    	String msg = postMsg(msgReceived);
    	
    	return msg;
    }
    
    private String postMsg(String msg) {

    	String url = "https://minbots.hypersait.com/bot_s4c/";
    	String requestJson = "{\"msg\":\""+msg+"\"}";
    	HttpHeaders headers = new HttpHeaders();
    	headers.setContentType(MediaType.APPLICATION_JSON);

    	HttpEntity<String> entity = new HttpEntity<String>(requestJson,headers);
    	String answer = restTemplate.postForObject(url, entity, String.class);
    	System.out.println(answer);
    	return answer;
    }

//	private String processMsg(String msgRecieved) {
//		String msg;
//		List<String> buttons = new ArrayList<String>();
//		
//		switch (msgRecieved) {
//		case "Hi":
//		case "hi":
//		case "HI":
//			msg = "Hi! I swear I will not kill anyone. Trust me.";
//			break;
//		case "orders":
//			msg = "These are things that I can never do";
//			buttons.add("Order 1");
//			buttons.add("Order 2");
//			break;
//		default:
//			msg = "zzz";
//			break;
//		}
//		StringBuilder json = new StringBuilder();
//		json.append("{");
//		json.append('"');
//		json.append("msg");
//		json.append('"');
//		json.append(':');
//		json.append('"');
//		json.append(msg);
//		json.append('"');
//		json.append(',');
//		json.append('"');
//		json.append("buttons");
//		json.append('"');
//		json.append(':');
//		json.append('[');
//		Iterator<String> it = buttons.iterator();
//		while (it.hasNext()) {
//			json.append('"');
//			json.append(it.next());
//			json.append('"');
//			if (it.hasNext()) {
//				json.append(',');
//			}
//		}
//		json.append(']');
//		json.append("}");
//		
//		return json.toString();
//	}

}
