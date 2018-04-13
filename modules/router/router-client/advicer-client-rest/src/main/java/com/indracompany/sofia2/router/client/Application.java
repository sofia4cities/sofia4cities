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
package com.indracompany.sofia2.router.client;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.indracompany.sofia2.router.service.app.model.NotificationCompositeModel;
import com.indracompany.sofia2.router.service.app.model.NotificationModel;
import com.indracompany.sofia2.router.service.app.model.OperationResultModel;
import com.indracompany.sofia2.router.service.app.service.advice.AdviceServiceImpl;

public class Application {

    private static final Logger log = LoggerFactory.getLogger(Application.class);

    public static void main(String args[]) throws KeyManagementException, NoSuchAlgorithmException {
       /* RestTemplate restTemplate = new RestTemplate();
        restTemplate.getInterceptors().add(new BasicAuthorizationInterceptor("admin", "admin"));
        //String quote = restTemplate.getForObject("http://localhost:19100/router/router/node-red", String.class);
        Notification input = new Notification();
        input.setBody("hey");
        input.setObjectId("id");
        Notification quote = restTemplate.postForObject("http://localhost:19100/router/router/test-operation",input, Notification.class);
        System.out.println(quote.toString());
        */
        
    	OperationResultModel input = new OperationResultModel();
    	NotificationCompositeModel model = new NotificationCompositeModel();
      
        
        RouterClient<NotificationCompositeModel,OperationResultModel> routerClient= new AdviceServiceImpl();
		RouterClientGateway<NotificationCompositeModel,OperationResultModel> gateway = new RouterClientGateway<NotificationCompositeModel,OperationResultModel>
				(
				RouterClientGateway.setupDefault("PEPE","PEPE"),
				routerClient);
		gateway.setFallback(input);
		input = gateway.execute(model);
		
		
		
    }

}