package com.indracompany.sofia2.systemconfig.init;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class SystemConfigInitApplication {

	public static void main(String[] args) {
		SpringApplication.run(SystemConfigInitApplication.class, args);
	}
}
