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
package com.indracompany.sofia2.controlpanel;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.endpoint.MetricsEndpoint;
import org.springframework.boot.actuate.endpoint.MetricsEndpointMetricReader;
import org.springframework.boot.actuate.endpoint.PublicMetrics;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import com.github.dandelion.core.web.DandelionFilter;
import com.github.dandelion.core.web.DandelionServlet;
import com.github.dandelion.datatables.thymeleaf.dialect.DataTablesDialect;
import com.github.dandelion.thymeleaf.dialect.DandelionDialect;
import com.indracompany.sofia2.commons.ssl.SSLUtil;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootApplication
@EnableJpaAuditing
@EnableAutoConfiguration
@ComponentScan("com.indracompany.sofia2")
public class Sofia2ControlPanelWebApplication extends WebMvcConfigurerAdapter {

	@Value("${sofia2.locale.default:en}")
	@Getter
	@Setter
	private String defaultLocale;

	public static void main(String[] args) throws Exception {
		SpringApplication.run(Sofia2ControlPanelWebApplication.class, args);
	}

	@Bean
	public ResourceBundleMessageSource messageSource() {
		ResourceBundleMessageSource resourceBundleMessageSource = new ResourceBundleMessageSource();
		resourceBundleMessageSource.setBasename("i18n/messages");
		resourceBundleMessageSource.setDefaultEncoding("UTF-8");
		return resourceBundleMessageSource;
	}

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/static/**").addResourceLocations("classpath:/static/");
		registry.addResourceHandler("/notebooks/app/**").addResourceLocations("classpath:/static/notebooks/");
	}

	/**
	 * Exports the all endpoint metrics like those implementing
	 * {@link PublicMetrics}.
	 */
	@Bean
	public MetricsEndpointMetricReader metricsEndpointMetricReader(MetricsEndpoint metricsEndpoint) {
		return new MetricsEndpointMetricReader(metricsEndpoint);
	}

	/**
	 * Dandelion Config Beans
	 */
	@Bean
	public DandelionDialect dandelionDialect() {
		return new DandelionDialect();
	}

	@Bean
	public DataTablesDialect dataTablesDialect() {
		return new DataTablesDialect();
	}

	@Bean
	public DandelionFilter dandelionFilter() {
		return new DandelionFilter();
	}

	@Bean
	public ServletRegistrationBean dandelionServletRegistrationBean() {
		return new ServletRegistrationBean(new DandelionServlet(), "/dandelion-assets/*");
	}

	/**
	 * Locale Context Beans
	 */
	@Bean
	public LocaleResolver localeResolver() {
		SessionLocaleResolver slr = new SessionLocaleResolver();
		Locale locale = new Locale(defaultLocale);
		slr.setDefaultLocale(locale);
		return slr;
	}

	@Bean
	public LocaleChangeInterceptor localeChangeInterceptor() {
		LocaleChangeInterceptor lci = new LocaleChangeInterceptor();
		lci.setParamName("lang");
		lci.setIgnoreInvalidLocale(true);
		return lci;
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(localeChangeInterceptor());
	}

	private static final String MSJ_SSL_ERROR = "Error configuring SSL verification in Control Panel";

	@Bean
	@Conditional(ControlPanelAvoidSSLVerificationCondition.class)
	String sslUtil() {
		try {
			SSLUtil.turnOffSslChecking();
		} catch (KeyManagementException e) {
			log.error(MSJ_SSL_ERROR, e);
			throw new RuntimeException(MSJ_SSL_ERROR, e);
		} catch (NoSuchAlgorithmException e) {
			log.error(MSJ_SSL_ERROR, e);
			throw new RuntimeException(MSJ_SSL_ERROR, e);
		}

		return "OK";
	}

}