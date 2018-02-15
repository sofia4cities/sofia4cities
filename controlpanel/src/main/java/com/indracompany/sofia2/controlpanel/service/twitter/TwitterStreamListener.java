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
package com.indracompany.sofia2.controlpanel.service.twitter;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.social.twitter.api.StreamDeleteEvent;
import org.springframework.social.twitter.api.StreamListener;
import org.springframework.social.twitter.api.StreamWarningEvent;
import org.springframework.social.twitter.api.Tweet;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.indracompany.sofia2.common.exception.AuthenticationException;
import com.indracompany.sofia2.iotbroker.common.exception.SSAPComplianceException;
import com.indracompany.sofia2.iotbroker.processor.MessageProcessor;
import com.indracompany.sofia2.ssap.SSAPMessage;
import com.indracompany.sofia2.ssap.SSAPMessageDirection;
import com.indracompany.sofia2.ssap.SSAPMessageTypes;
import com.indracompany.sofia2.ssap.body.SSAPBodyJoinMessage;
import com.indracompany.sofia2.ssap.body.SSAPBodyLeaveMessage;
import com.indracompany.sofia2.ssap.body.SSAPBodyOperationMessage;
import com.indracompany.sofia2.ssap.body.SSAPBodyReturnMessage;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j

public class TwitterStreamListener implements StreamListener {

	@Autowired
	MessageProcessor messageProcessor;

	@Getter
	@Setter
	private List<String> keywords;
	@Getter
	@Setter
	private boolean geolocation;
	@Getter
	@Setter
	private int timeout;
	@Getter
	@Setter
	private String ontology;
	@Getter
	@Setter
	private String clientPlatform;
	@Getter
	@Setter
	private String token;
	@Getter
	@Setter
	private String id;
	@Getter
	@Setter
	private String configurationId;

	@Value("${sofia2.urls.iotbroker.services}" + "${sofia2.paths.ssap}")
	private String url;
	@Getter
	@Setter
	private String sessionKey;

	@Getter
	@Setter
	private ExecutorService executor;
	@Getter
	@Setter
	private Runnable tweetInsert;
	private static final int THREADS = 10;
	private LinkedBlockingQueue<Tweet> tweetsQueue;

	// private TwitterStream twitterStream;
	private int countInserts = 0;
	private int maxInserts;

	private static final int QUEUE_LENGTH = 25;
	private static final String A_PATTERN = "<a href=\"(.*)\" rel=\"(.*)\">(.*)</a>";
	private static final String MONGO_DATE_FORMAT = "YYYY-MM-dd'T'HH:mm:ss.SSS'Z'";
	private static final String ONTOLOGY_INSTANCE_PATTERN = "{'Tweet': {" + " 'timestamp': {'$date':'%s'},"
	// + " 'geometry': {'type': 'Point', 'coordinates': [ %s, %s ]},"
			+ " 'tweet_id': '%s'," + " 'tweet_createdat': {'$date':'%s'}," + " 'tweet_source': '%s',"
			+ " 'tweet_text': '%s'," + " 'tweet_user_id': '%s'," + " 'tweet_user_screenname': '%s',"
			+ " 'tweet_user_name': '%s'," + " 'tweet_user_profileimage': '%s'," + " 'tweet_favoritecount': '%s',"
			+ " 'tweet_retweetcount': '%s'" + "}}";

	public TwitterStreamListener() {

		tweetsQueue = new LinkedBlockingQueue<Tweet>();
		executor = Executors.newFixedThreadPool(THREADS);
		tweetInsert = defineMonitoringRunnable();
		for (int i = 0; i < THREADS; i++) {
			executor.execute(tweetInsert);
		}
	}

	@Override
	public void onTweet(Tweet tweet) {
		try {
			/* Si ha muerto alg�n hilo se vuelve a levantar */
			if (executor instanceof ThreadPoolExecutor && ((ThreadPoolExecutor) executor).getActiveCount() < THREADS) {
				for (int i = 0; i < (THREADS - ((ThreadPoolExecutor) executor).getActiveCount()); i++) {
					executor.execute(tweetInsert);
				}
			}

			/* Se agrega el tweet a la cola */

			if (tweetsQueue.size() < QUEUE_LENGTH) {
				tweetsQueue.add(tweet);

			}
		} catch (Exception e) {
			log.debug("Error on status: " + e.getMessage());
		}

	}

	@Override
	public void onDelete(StreamDeleteEvent deleteEvent) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onLimit(int numberOfLimitedTweets) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onWarning(StreamWarningEvent warningEvent) {
		// TODO Auto-generated method stub

	}

	public void insertInstance(String instance) {
		try {
			insertSIB(instance);
		} catch (Exception e) {
			log.debug("Error inserting tweet : " + this.getOntology() + ". Cause: " + e.getMessage(), e);
		}

	}

	public void connectSIB() throws SSAPComplianceException, AuthenticationException {

		SSAPBodyJoinMessage joinMessage = new SSAPBodyJoinMessage();
		joinMessage.setToken(this.getToken());
		joinMessage.setClientPlatform(this.getClientPlatform());
		joinMessage.setClientPlatformInstance(this.getClientPlatform() + ":TwitterStreaming");

		SSAPMessage<SSAPBodyJoinMessage> message = new SSAPMessage<SSAPBodyJoinMessage>();

		message.setMessageType(SSAPMessageTypes.JOIN);
		message.setOntology(this.getOntology());
		message.setDirection(SSAPMessageDirection.REQUEST);
		message.setBody(joinMessage);

		SSAPMessage<SSAPBodyReturnMessage> responseJoin = messageProcessor.process(message);
		if (responseJoin.getBody().isOk()) {
			this.setSessionKey(responseJoin.getSessionKey());
			log.debug("Connected to SIB with token: " + this.getToken() + " session key: " + this.getSessionKey());
		} else
			log.debug("Can't connect to IoT Broker");

	}

	public void disconnectSIB() {
		SSAPMessage<SSAPBodyLeaveMessage> message = new SSAPMessage<SSAPBodyLeaveMessage>();
		message.setDirection(SSAPMessageDirection.REQUEST);
		message.setBody(new SSAPBodyLeaveMessage());
		message.setMessageType(SSAPMessageTypes.LEAVE);

		try {
			messageProcessor.process(message);
			log.debug("Disconnected from SIB");
		} catch (Exception e) {
			log.debug("Couldn't disconnect from SIB");
		}

	}

	public void insertSIB(String instance) throws JsonProcessingException, IOException {

		ObjectMapper mapper = new ObjectMapper();
		JsonNode json = mapper.readTree(instance);

		SSAPMessage<SSAPBodyOperationMessage> message = new SSAPMessage<SSAPBodyOperationMessage>();
		message.setDirection(SSAPMessageDirection.REQUEST);
		message.setMessageType(SSAPMessageTypes.INSERT);
		message.setSessionKey(this.getSessionKey());
		message.setOntology(this.getOntology());
		SSAPBodyOperationMessage operationMessage = new SSAPBodyOperationMessage();
		operationMessage.setClientPlatform(this.getClientPlatform());
		operationMessage.setClientPlatformInstance(this.getClientPlatform() + ":TwitterStreaming");
		operationMessage.setData(json);
		message.setBody(operationMessage);

		SSAPMessage<SSAPBodyReturnMessage> response = messageProcessor.process(message);
		if (response.getBody().isOk())
			log.debug("Ontology instance inserted");
		else
			log.debug("Couldn't insert instance");

	}

	class ListenerThread implements Runnable {

		private TwitterStreamListener listener;

		public ListenerThread(TwitterStreamListener listener) {
			this.listener = listener;
		}

		@Override
		public void run() {
			while (true) {
				if (tweetsQueue.size() > 0) {
					Tweet tweet = tweetsQueue.poll();
					if (tweet != null) {
						// insertTweet
						String foundLongitude = "0.0";
						String foundLatitude = "0.0";

						/* Ontology instance generation */
						SimpleDateFormat sdf_mongo = new SimpleDateFormat(MONGO_DATE_FORMAT);
						Pattern pattern = Pattern.compile(A_PATTERN);
						Matcher matcher = pattern.matcher(tweet.getSource());
						String processed_source = "";
						if (matcher.find()) {
							processed_source = matcher.group(1);
						}

						String instance = String.format(ONTOLOGY_INSTANCE_PATTERN,
								sdf_mongo.format(new Date()).toString(),
								// (tweet.getExtraData()== null ? foundLongitude
								// :
								// Double.toString(tweet.getGeoLocation().getLongitude())),
								// (tweet.getGeoLocation() == null ?
								// foundLatitude :
								// Double.toString(tweet.getGeoLocation().getLatitude())),
								Long.toString(tweet.getId()), sdf_mongo.format(tweet.getCreatedAt()).toString(),
								processed_source,
								tweet.getText().replace("%", "%25").replace("\\", "").replace("'", "%27")
										.replace("\"", "%22").replace("\r", " ").replace("\n", " "),
								Long.toString(tweet.getUser().getId()),
								tweet.getUser().getScreenName().replace("%", "%25").replace("\\", "")
										.replace("'", "%27").replace("\"", "%22"),
								tweet.getUser().getName().replace("%", "%25").replace("\\", "").replace("'", "%27")
										.replace("\"", "%22"),
								tweet.getUser().getProfileImageUrl(), Integer.toString(tweet.getFavoriteCount()),
								Integer.toString(tweet.getRetweetCount()));

						/* Se inserta el tweet */
						insertInstance(instance);
					}
				}

				try {
					Thread.sleep(150);
				} catch (InterruptedException ex) {
					ex.printStackTrace();
				}
			}
		}

	}

	private Runnable defineMonitoringRunnable() {
		return new ListenerThread(this);
	}
}
