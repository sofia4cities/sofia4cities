/**
 * Copyright Indra Sistemas, S.A.
 * 2013-2018 SPAIN
 *
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
package com.indracompany.sofia2.api.camel;

import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.ConfigurationCondition;
import org.springframework.core.io.Resource;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.stereotype.Service;

@Service
public class CamelContextReferenceContitional implements ConfigurationCondition{
	 

    @Override
    public ConfigurationPhase getConfigurationPhase() {
        return ConfigurationPhase.PARSE_CONFIGURATION;
    }

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
    	
    	 Resource resource =  context.getResourceLoader().getResource("classpath:camel-context-reference.xml"); 
    	 if (resource.exists()) {
    		 return false;
    	 }
    	 else return true;
    	
    }

}