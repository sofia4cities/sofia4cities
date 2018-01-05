package com.indracompany.sofia2.persistence.mongodb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class PersistenceRITestApplication {

	public static void main(String[] args) {
		SpringApplication.run(PersistenceRITestApplication.class, args);
	}
}
