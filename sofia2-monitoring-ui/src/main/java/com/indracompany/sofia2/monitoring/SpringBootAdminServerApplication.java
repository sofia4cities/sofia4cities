package com.indracompany.sofia2.monitoring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import de.codecentric.boot.admin.config.EnableAdminServer;

@EnableAdminServer
@SpringBootApplication
@EnableJpaAuditing
@ComponentScan("com.indracompany.sofia2")
public class SpringBootAdminServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootAdminServerApplication.class, args);
    }
}
