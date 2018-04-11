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
package com.indracompany.sofia2.api.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.cxf.Bus;
import org.apache.cxf.endpoint.Server;
import org.apache.cxf.feature.LoggingFeature;
import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;
import org.apache.cxf.jaxrs.swagger.Swagger2Feature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.codahale.metrics.JmxReporter;
import com.codahale.metrics.MetricRegistry;
import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;

@Configuration
public class JaxRSConfig {

	@Autowired
	private Bus bus;

	@Autowired
	ApplicationContext applicationContext;

	@Bean
	public Server rsServer() {

		List<Object> lista = new ArrayList<Object>();
		JAXRSServerFactoryBean endpoint = new JAXRSServerFactoryBean();
		endpoint.setBus(bus);

		Map<String, Object> beansOfType2 = applicationContext.getBeansWithAnnotation(io.swagger.annotations.Api.class);

		Iterator<Entry<String, Object>> iterator = beansOfType2.entrySet().iterator();
		while (iterator.hasNext()) {
			lista.add(iterator.next().getValue());
		}

		List<Object> providers = new ArrayList<Object>();
		providers.add(new JacksonJaxbJsonProvider());
		// providers.add(new JacksonJaxbXMLProvider());

		endpoint.setProviders(providers);
		endpoint.setServiceBeans(lista);
		endpoint.setAddress("/");
		// TODO REFACTOR!
		// endpoint.setFeatures(Arrays.asList(createSwaggerFeature(),
		// loggingFeature(),new MetricsFeature(new CodahaleMetricsProvider(bus))));
		endpoint.setFeatures(Arrays.asList(createSwaggerFeature(), loggingFeature()));
		endpoint.setProperties(
				Collections.singletonMap("org.apache.cxf.management.service.counter.name", "cxf-services."));
		return endpoint.create();
	}

	@Bean(name = "loggingFeature")
	LoggingFeature loggingFeature() {
		LoggingFeature loggingFeature = new LoggingFeature();
		loggingFeature.setPrettyLogging(true);
		return loggingFeature;
	}

	@Bean(initMethod = "start", destroyMethod = "stop")
	public JmxReporter jmxReporter() {
		return JmxReporter.forRegistry(metricRegistry()).build();
	}

	@Bean
	public MetricRegistry metricRegistry() {
		return new MetricRegistry();
	}

	public Swagger2Feature createSwaggerFeature() {
		Swagger2Feature swagger2Feature = new Swagger2Feature();
		swagger2Feature.setPrettyPrint(true);
		swagger2Feature.setTitle("Sofia2Open API Manager");
		swagger2Feature.setContact("The Sofia2Open team");
		swagger2Feature.setDescription("");
		swagger2Feature.setVersion("1.0.0");
		swagger2Feature.setPrettyPrint(true);
		swagger2Feature.setScan(true);
		swagger2Feature.setScanAllResources(true);
		swagger2Feature.setSupportSwaggerUi(true);
		return swagger2Feature;
	}

}
