package streaming.twitter.service;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.junit.Assert;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.social.twitter.api.Stream;
import org.springframework.social.twitter.api.Tweet;
import org.springframework.test.context.junit4.SpringRunner;

import com.indracompany.sofia2.config.components.TwitterConfiguration;
import com.indracompany.sofia2.config.model.Configuration;
import com.indracompany.sofia2.config.services.configuration.ConfigurationService;


import lombok.extern.slf4j.Slf4j;
import streaming.twitter.application.StreamingTwitterApp;
import streaming.twitter.listener.TwitterStreamListener;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = StreamingTwitterApp.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@Slf4j
public class TwitterStreamServiceIntegrationTest {

	@Autowired
	TwitterStreamService twitterStreamService;
	@MockBean
	ConfigurationService configurationService;

	private TwitterConfiguration twitterConfiguration;

	@Spy
	private TwitterStreamListener twitterStreamListener;
	private Stream stream;
	private String accessToken = "74682827-D6cX2uurqpxy6yWlg6wioRl49f9Rtt2pEXUu6YNUy";
	private String accessTokenSecret = "Cmd9XOX9N8xMRvlYUz3Wg49ZCGFnanMJvJPI9QMfTXix2";
	private String consumerKey = "PWgCyepuon5U8X9HqfUtNpntq";
	private String consumerSecret = "zo6rbSh6J470t7CCz4ZtXhHEFhpt36TMPKYolJgIiLOpEW9oc4";

	@Before
	public void setUp() {
		this.twitterConfiguration = new TwitterConfiguration();
		this.twitterConfiguration.setAccessToken(accessToken);
		this.twitterConfiguration.setAccessTokenSecret(accessTokenSecret);
		this.twitterConfiguration.setConsumerKey(consumerKey);
		this.twitterConfiguration.setConsumerSecret(consumerSecret);

		List<String> keywords = new ArrayList<String>();
		keywords.add("Helsinki");
		keywords.add("Borbones");

		twitterStreamListener = Mockito.spy(new TwitterStreamListener());
		twitterStreamListener.setId(UUID.randomUUID().toString());
		twitterStreamListener.setOntology("TwitterOntology");
		twitterStreamListener.setClientPlatform("clientPlatform");
		twitterStreamListener.setToken(UUID.randomUUID().toString());
		twitterStreamListener.setKeywords(keywords);
		twitterStreamListener.setGeolocation(false);
		twitterStreamListener.setTimeout(1000);
		twitterStreamListener.setConfigurationId(UUID.randomUUID().toString());
	}

	@Test
	public void test_1_subscribe() throws Exception {
		when(configurationService.getConfiguration(any())).thenReturn(new Configuration());
		when(configurationService.getTwitterConfiguration(any(), any())).thenReturn(twitterConfiguration);
		doNothing().when(twitterStreamListener).getSibSessionKey();

		Assert.assertNotNull(this.twitterStreamService.suscribe(twitterStreamListener));

	}

	@Test
	public void test_2_isSubscribe() throws Exception {
		when(configurationService.getConfiguration(any())).thenReturn(new Configuration());
		when(configurationService.getTwitterConfiguration(any(), any())).thenReturn(twitterConfiguration);
		doNothing().when(twitterStreamListener).getSibSessionKey();
		this.twitterStreamService.suscribe(twitterStreamListener);
		Assert.assertTrue(this.twitterStreamService.isSuscribe(twitterStreamListener.getId()));
	}

	@Test
	public void test_3_unsubscribe() throws Exception {
		when(configurationService.getConfiguration(any())).thenReturn(new Configuration());
		when(configurationService.getTwitterConfiguration(any(), any())).thenReturn(twitterConfiguration);
		doNothing().when(twitterStreamListener).getSibSessionKey();
		this.twitterStreamService.suscribe(twitterStreamListener);

		doNothing().when(twitterStreamListener).deleteSibSessionKey();

	}

	@Test
	public void test_4_onTweet() throws Exception {
		when(configurationService.getConfiguration(any())).thenReturn(new Configuration());
		when(configurationService.getTwitterConfiguration(any(), any())).thenReturn(twitterConfiguration);
		doNothing().when(twitterStreamListener).getSibSessionKey();

		stream = this.twitterStreamService.suscribe(twitterStreamListener);

		doNothing().when(twitterStreamListener).insertInstance(any());

		while (twitterStreamListener.getTweetsQueue().size() == 0) {
			//wait until tweet
		}
		Tweet lastTweet = twitterStreamListener.getTweetsQueue().poll();
		log.info("Last tweet by user:" + lastTweet.getFromUser() + ", text: "
				+ lastTweet.getText());
		
		Assert.assertTrue(lastTweet.getText() != null && !lastTweet.getText().equals(""));
	}
}
