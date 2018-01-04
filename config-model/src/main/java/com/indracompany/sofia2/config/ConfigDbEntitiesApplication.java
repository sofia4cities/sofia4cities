package com.indracompany.sofia2.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class ConfigDbEntitiesApplication {

	public static void main(String[] args) {
		SpringApplication.run(ConfigDbEntitiesApplication.class, args);
	}
}
