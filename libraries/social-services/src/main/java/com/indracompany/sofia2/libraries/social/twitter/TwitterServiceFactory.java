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
package com.indracompany.sofia2.libraries.social.twitter;

import org.springframework.social.twitter.api.Twitter;
import org.springframework.social.twitter.api.impl.TwitterTemplate;
import org.springframework.stereotype.Component;

@Component
public class TwitterServiceFactory {

	/**
	 * https://docs.spring.io/spring-social-twitter/docs/current/reference/htmlsingle/
	 * TODO: Validate if it is necessary to check if there is a connection with this credentials and return it instead of creating
	 * @param consumerKey
	 * @param consumerSecret
	 * @param accessToken
	 * @param accessTokenSecret
	 * @return
	 */
	public static TwitterServiceSpringSocialImpl getSpringSocialImpl(String consumerKey,String consumerSecret,String accessToken,String accessTokenSecret) {
		Twitter twitter = new TwitterTemplate(consumerKey,consumerSecret,accessToken,accessTokenSecret);
		TwitterServiceSpringSocialImpl twitterService = new TwitterServiceSpringSocialImpl(twitter);
		return twitterService;
	}	
	
}
