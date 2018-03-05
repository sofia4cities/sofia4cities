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


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.indracompany.sofia2.router.service.app.model.NotificationModel;
import com.indracompany.sofia2.router.service.app.model.OperationModel;
import com.indracompany.sofia2.router.service.app.model.OperationResultModel;
import com.indracompany.sofia2.router.service.app.service.RouterServiceImpl;

public class Application {

    private static final Logger log = LoggerFactory.getLogger(Application.class);

    public static void main(String args[]) {
    	RouterServiceImpl impl = new RouterServiceImpl();
    	impl.setRouterStandaloneURL("http://localhost:19100/router/router/");
    	
    	NotificationModel input = new NotificationModel();
    	OperationModel model = new OperationModel();
    	
    	String query = "select * from product where name = \"admin\"";
    	model.setOntologyName("product");
    	model.setQueryType("SQLLIKE");
    	model.setBody(query);
    	model.setUser("administrator");
    	model.setOperationType("GET");
    	
		input.setOperationModel(model);
    	
		OperationResultModel result = impl.execute(input);
		
		System.out.println(result);
		
		
		
    }

}