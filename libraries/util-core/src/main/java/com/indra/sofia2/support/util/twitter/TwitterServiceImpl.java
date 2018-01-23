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
/*******************************************************************************
 * © Indra Sistemas, S.A.
 * 2013 - 2014  SPAIN
 * 
 * All rights reserved
 ******************************************************************************/
package com.indra.sofia2.support.util.twitter;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

import com.indra.sofia2.support.entity.gestion.dominio.Configuracionrrss;
import com.indra.sofia2.support.util.twitter.exceptions.TwitterException;

import twitter4j.Status;
import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

@Component
public class TwitterServiceImpl implements TwitterService {
	
	private String text=null;
	private static Log log = LogFactory.getLog(TwitterServiceImpl.class);

	public long sendTweet(String apiKey, String apiKeySecret, String token, String tokenSecret, Map<String, String> parameters) throws TwitterException {

		Configuracionrrss conf = new Configuracionrrss();
		
		conf.setOauthConsumerKey(apiKey);
		conf.setOauthConsumerSecret(apiKeySecret);
		conf.setOauthAccessToken(token);
		conf.setOauthAccessSecret(tokenSecret);
		
		log.debug("Configures Twitter parameters");
		ConfigurationBuilder cb=setProxy (parameters);
		Twitter twitter = getInstanceTwitterbyConfiguration(cb,conf);
		
		try {
        	StatusUpdate status = new StatusUpdate(this.text);
        	log.debug("Sending the message");
        	Status estado=twitter.updateStatus(status);
        	log.debug("Message sent to Twitter account");
        	return estado.getId();
		}
		catch (twitter4j.TwitterException e) {
			log.error("Could not send the message "+e.getMessage());
			throw new TwitterException(e);
		}
	}

	private ConfigurationBuilder setProxy (Map<String, String> parameters){
		
		ConfigurationBuilder cb = new ConfigurationBuilder();
		
		for (Map.Entry<String, String> entry : parameters.entrySet()){
			
			if 	(entry.getKey().equalsIgnoreCase("text") || entry.getKey().equalsIgnoreCase("status") ){
				this.text=entry.getValue();
			}else if (entry.getKey().equalsIgnoreCase("host")){
				cb.setHttpProxyHost(entry.getValue());
			} else if (entry.getKey().equalsIgnoreCase("port")){
				try {
					cb.setHttpProxyPort(Integer.parseInt(entry.getValue()));
				}catch (NumberFormatException e){
					log.debug("Using port by default (8080)");
					cb.setHttpProxyPort(8080);
				}
			} else if (entry.getKey().equalsIgnoreCase("user")){
				cb.setHttpProxyUser(entry.getValue());
			}else if (entry.getKey().equalsIgnoreCase("password")){
				cb.setHttpProxyPassword(entry.getValue());
			}
		}
		return cb;
	}
	
	private Twitter getInstanceTwitterbyConfiguration(ConfigurationBuilder cb,Configuracionrrss configuracionrrss){
		 
	    	cb.setDebugEnabled(true)
	    	  .setOAuthConsumerKey(configuracionrrss.getOauthConsumerKey())
	    	  .setOAuthConsumerSecret(configuracionrrss.getOauthConsumerSecret())
	    	  .setOAuthAccessToken(configuracionrrss.getOauthAccessToken())
	    	  .setOAuthAccessTokenSecret(configuracionrrss.getOauthAccessSecret())
	    	  .setJSONStoreEnabled(true);
	    	Configuration cf=cb.build();
	    	TwitterFactory tf = new TwitterFactory(cf);
	    	return tf.getInstance();
	    }
	
}