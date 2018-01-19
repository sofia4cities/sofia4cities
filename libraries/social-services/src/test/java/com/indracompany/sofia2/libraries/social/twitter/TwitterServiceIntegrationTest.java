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

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.social.twitter.api.GeoCode;
import org.springframework.social.twitter.api.SearchParameters;
import org.springframework.social.twitter.api.SearchResults;
import org.springframework.social.twitter.api.Stream;
import org.springframework.social.twitter.api.StreamDeleteEvent;
import org.springframework.social.twitter.api.StreamListener;
import org.springframework.social.twitter.api.StreamWarningEvent;
import org.springframework.social.twitter.api.Tweet;
import org.springframework.social.twitter.api.TwitterProfile;
import org.springframework.test.context.junit4.SpringRunner;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
*
* @author Luis Miguel Gracia
*/
@RunWith(SpringRunner.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@Slf4j
public class TwitterServiceIntegrationTest {

	TwitterServiceSpringSocialImpl twitterS;
	
	private String accessToken="74682827-D6cX2uurqpxy6yWlg6wioRl49f9Rtt2pEXUu6YNUy";
	private String accessTokenSecret	="Cmd9XOX9N8xMRvlYUz3Wg49ZCGFnanMJvJPI9QMfTXix2";
	private String consumerKey ="PWgCyepuon5U8X9HqfUtNpntq";
	private String consumerSecret ="zo6rbSh6J470t7CCz4ZtXhHEFhpt36TMPKYolJgIiLOpEW9oc4";
		
	@Before
	public void setup() {
		twitterS = TwitterServiceFactory.getSpringSocialImpl(consumerKey, consumerSecret, accessToken, accessTokenSecret);
	}
	
	@Test
	public void test4_getFollowers() {
		List<TwitterProfile> followers = twitterS.getFollowers("SOFIA2_Platform");
		Assert.assertTrue(followers.size()>1);		
	}
	
	@Test
	public void test2_findSimple() {
		SearchResults results = twitterS.search("madrid");
		Assert.assertTrue(results.getTweets().size()>0);		
	}
	
	@Test
	public void test3_findAdvanced() {
		SearchParameters params = new SearchParameters("madrid")
        .geoCode(new GeoCode(52.379241, 4.900846, 100, GeoCode.Unit.MILE))
        .lang("es")
        .resultType(SearchParameters.ResultType.RECENT)
        .count(25)
        .includeEntities(false);
		SearchResults results = twitterS.search(params);
		Assert.assertTrue(results.getTweets().size()==25);		
	}
	
	@Test 
	public void test1_Streaming() {
		TestTwitterStreamListener listener = new TestTwitterStreamListener();
		Stream stream = twitterS.createFilterStreaming("madrid", listener);
		//stream.open();
		while (listener.getLastTweet()==null) {
			try {
				Thread.currentThread().sleep(1000);
	        } catch (InterruptedException e) {
	            //e.printStackTrace();
	        }
		}
		log.info("** listener.getLastTweet()="+listener.getLastTweet().getText());
		Assert.assertTrue(listener.getLastTweet()!=null);
		stream.close();
		
	}
}

@Slf4j
class TestTwitterStreamListener implements StreamListener {
	
	@Getter private Tweet lastTweet=null;
	

	@Override
	public void onDelete(StreamDeleteEvent arg0) {
		log.info("onDelete:"+arg0);
		
	}

	@Override
	public void onLimit(int arg0) {
		log.info("onLimit:"+arg0);
		
	}

	@Override
	public void onTweet(Tweet arg0) {
		if (arg0!=null)
			this.lastTweet=arg0;
		log.info("onTweet:"+arg0.getText());	
	}

	@Override
	public void onWarning(StreamWarningEvent arg0) {
		log.info("onWarning:"+arg0.getMessage());
		
	}
	
}
