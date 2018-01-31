package com.indracompany.sofia2.service.twitter;

import java.util.List;

import com.indracompany.sofia2.config.model.Configuration;
import com.indracompany.sofia2.config.model.TwitterListener;


public interface TwitterService {
	
	List<TwitterListener> getAllListens();
	List<TwitterListener> getAllListensByUserId(String userId);
	List<Configuration> getAllConfigurations();
	List<Configuration> getConfigurationsByUserId(String userId);

}
