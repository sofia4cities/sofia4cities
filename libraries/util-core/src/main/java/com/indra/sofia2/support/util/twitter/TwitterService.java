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

import com.indra.sofia2.support.util.twitter.exceptions.TwitterException;

public interface TwitterService {
	/**
	   * Send a tweet to the Twitter account of the parameters:
	   * @param apiKey String
	   * @param apiKeySecret String
	   * @param token String
	   * @param tokenSecret String
	   * @param parameters Map<String, String>:  It must contain the key "text"
	   * and the proxy settings in case of you need this configuration.
	   * Example: def map = [text:"HELLO",password:"",port:"",user:" ", host: "  "];
	   * @return id
	   */
	long sendTweet(String apiKey, String apiKeySecret, String token, String tokenSecret, Map<String, String> parameters) throws TwitterException;
	
}
