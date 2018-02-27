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

import org.apache.camel.CamelContext;
import org.apache.camel.component.metrics.routepolicy.MetricsRoutePolicyFactory;
import org.apache.camel.spring.boot.CamelContextConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.autoconfigure.ExportMetricReader;
import org.springframework.boot.actuate.autoconfigure.ExportMetricWriter;
import org.springframework.boot.actuate.endpoint.MetricsEndpoint;
import org.springframework.boot.actuate.endpoint.MetricsEndpointMetricReader;
import org.springframework.boot.actuate.metrics.jmx.JmxMetricWriter;
import org.springframework.boot.actuate.metrics.reader.MetricReader;
import org.springframework.boot.actuate.metrics.reader.MetricRegistryMetricReader;
import org.springframework.boot.actuate.metrics.writer.MetricWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jmx.export.MBeanExporter;

import com.codahale.metrics.MetricRegistry;

@Configuration
public class MetricsConfig {

	@Autowired
	private MetricRegistry metricRegistry;

	@Bean
	MetricsEndpointMetricReader metricsEndpointMetricReader(MetricsEndpoint metricsEndpoint) {
		return new MetricsEndpointMetricReader(metricsEndpoint);
	}

	@Bean
	@ExportMetricReader
	public MetricReader metricReader() {
		return new MetricRegistryMetricReader(metricRegistry());
	}

	public MetricRegistry metricRegistry() {
		final MetricRegistry metricRegistry = new MetricRegistry();
		return metricRegistry;
	}

	@Bean
	@ExportMetricWriter
	MetricWriter metricWriter(MBeanExporter exporter) {
		return new JmxMetricWriter(exporter);
	}

	@Bean
	CamelContextConfiguration contextConfiguration() {
		return new CamelContextConfiguration() {
			@Override
			public void beforeApplicationStart(CamelContext context) {
				MetricsRoutePolicyFactory fac = new MetricsRoutePolicyFactory();
				fac.setMetricsRegistry(metricRegistry);
				context.addRoutePolicyFactory(fac);
			}

			@Override
			public void afterApplicationStart(CamelContext camelContext) {
				// noop
			}
		};
	}

}
