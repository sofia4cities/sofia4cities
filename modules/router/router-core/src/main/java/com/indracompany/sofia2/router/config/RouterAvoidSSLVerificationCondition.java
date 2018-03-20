package com.indracompany.sofia2.router.config;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

public class RouterAvoidSSLVerificationCondition implements Condition{
	
	@Override
	public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
		String avoidSSLVerification = context.getEnvironment().getProperty("sofia2.router.avoidsslverification");
		return "true".equals(avoidSSLVerification);
	}

}
