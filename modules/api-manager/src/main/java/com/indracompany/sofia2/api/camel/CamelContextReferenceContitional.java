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