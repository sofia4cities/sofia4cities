package com.indracompany.sofia2.config.services.notebook.configuration;
import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Component
public class NotebookServiceConfiguration {
	@Value("${sofia2.analytics.notebook.zeppelinProtocol:http}")
	private String zeppelinProtocol;
	@Value("${sofia2.analytics.notebook.zeppelinHostname:localhost}")
	private String zeppelinHostname;
	@Value("${sofia2.analytics.notebook.zeppelinPort:8080}")
	private int zeppelinPort;
	@Value("${sofia2.analytics.notebook.zeppelinPathname:#{null}}")
	private String zeppelinPathname;
	@Value("${sofia2.analytics.notebook.shiroAdminUsername:#{null}}")
	private String zeppelinShiroAdminUsername;
	@Value("${sofia2.analytics.notebook.shiroAdminPass:#{null}}")
	private String zeppelinShiroAdminPass;
	@Value("${sofia2.analytics.notebook.shiroUsername:#{null}}")
	private String zeppelinShiroUsername;
	@Value("${sofia2.analytics.notebook.shiroPass:#{null}}")
	private String zeppelinShiroPass;
	@Value("${sofia2.analytics.notebook.restUsername:#{null}}")
	private String restUsername;
	@Value("${sofia2.analytics.notebook.restPass:#{null}}")
	private String restPass;
	private String baseURL;
	
	@PostConstruct
	public void init() {
		baseURL = String.format("%s://%s:%s/%s", zeppelinProtocol, zeppelinHostname, zeppelinPort, zeppelinPathname);
	}
}