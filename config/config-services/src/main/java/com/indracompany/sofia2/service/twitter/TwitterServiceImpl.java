package com.indracompany.sofia2.service.twitter;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.indracompany.sofia2.config.model.Configuration;
import com.indracompany.sofia2.config.model.TwitterListener;
import com.indracompany.sofia2.config.repository.ConfigurationRepository;
import com.indracompany.sofia2.config.repository.TwitterListenerRepository;
import com.indracompany.sofia2.service.user.UserService;

@Service
public class TwitterServiceImpl implements TwitterService {
	
	@Autowired
	TwitterListenerRepository twitterListenerRepository;
	@Autowired 
	ConfigurationRepository configurationRepository;
	@Autowired
	UserService userService;
	
	
	public List<TwitterListener> getAllListens()
	{
		return this.twitterListenerRepository.findAll();
	}
	public List<TwitterListener> getAllListensByUserId(String userId)
	{
		List<TwitterListener> listens=new ArrayList<TwitterListener>();
		for(TwitterListener listen: this.getAllListens())
		{
			if(listen.getOntologyId().getUserId().getUserId().equals(userId)) listens.add(listen);
			
		}
		return listens;
		
	}
	public List<Configuration> getAllConfigurations()
	{
		return this.configurationRepository.findAll();
	}
	public List<Configuration> getConfigurationsByUserId(String userId)
	{
		return this.configurationRepository.findByUserId(this.userService.getUser(userId));
		
	}

}
