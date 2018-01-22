package com.indracompany.sofia2.config;

import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.indracompany.sofia2.servlet.Api;

@Configuration
public class ServletApiConfig {

    @Bean
    public ServletRegistrationBean genericCustomServlet() {
        ServletRegistrationBean bean = new ServletRegistrationBean(new Api(), "/api/*");
        bean.setLoadOnStartup(1);
        return bean;
    }
}