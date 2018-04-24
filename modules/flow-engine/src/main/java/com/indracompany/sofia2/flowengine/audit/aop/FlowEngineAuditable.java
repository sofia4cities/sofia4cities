package com.indracompany.sofia2.flowengine.audit.aop;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Documented
@Retention(RUNTIME)
@Target(METHOD)
public @interface FlowEngineAuditable {

	String doNotify() default "yes";

	String module() default "FLOWENGINE";

}
