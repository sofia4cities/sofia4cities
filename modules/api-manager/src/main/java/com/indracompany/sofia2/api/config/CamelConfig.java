package com.indracompany.sofia2.api.config;

import org.apache.camel.component.servlet.CamelHttpTransportServlet;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

@Configuration
@ImportResource("classpath:api-camel-context.xml")
public class CamelConfig  {

	 @Bean
	    public ServletRegistrationBean camelServletRegistrationBean() {
	        ServletRegistrationBean registration = new ServletRegistrationBean(new CamelHttpTransportServlet(), "/camel/*");
	        registration.setName("CamelServlet");
	        registration.setAsyncSupported(true);
	        registration.setLoadOnStartup(1);
	        return registration;
	        
	}

}
