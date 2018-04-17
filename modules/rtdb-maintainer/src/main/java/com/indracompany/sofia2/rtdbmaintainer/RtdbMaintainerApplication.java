package com.indracompany.sofia2.rtdbmaintainer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableCaching(proxyTargetClass = true)
@ComponentScan("com.indracompany.sofia2")
public class RtdbMaintainerApplication {

	public static void main(String[] args) {
		SpringApplication.run(RtdbMaintainerApplication.class, args);
	}

}
