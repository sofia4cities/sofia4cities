package authentication.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.AppConfigurationEntry;

import org.apache.kafka.common.KafkaException;

import authentication.AuthenticateCallbackHandler;
import authentication.PlainAuthenticateCallback;
import authentication.PlainLoginModule;

public class PlainServerCallbackHandler implements AuthenticateCallbackHandler {

	private static final String JAAS_USER_PREFIX = "user_";
	private List<AppConfigurationEntry> jaasConfigEntries;
	private final String USER_AGENT = "Mozilla/5.0";

	private static String BASE = "http://localhost:18000/controlpanel/api-ops/validate/";
	private static String BASE_KEY = "OPEN_PLATFORM_VALIDATE_CLIENT_TOKEN";

	@Override
	public void configure(Map<String, ?> configs, String mechanism, List<AppConfigurationEntry> jaasConfigEntries) {
		this.jaasConfigEntries = jaasConfigEntries;
	}

	@Override
	public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
		String username = null;
		for (Callback callback : callbacks) {
			if (callback instanceof NameCallback)
				username = ((NameCallback) callback).getDefaultName();
			else if (callback instanceof PlainAuthenticateCallback) {
				PlainAuthenticateCallback plainCallback = (PlainAuthenticateCallback) callback;
				boolean authenticated = authenticate(username, plainCallback.password());
				plainCallback.authenticated(authenticated);
			} else
				throw new UnsupportedCallbackException(callback);
		}
	}

	protected boolean authenticate(String username, char[] password) throws IOException {
		if (username == null) {
			return false;
		} else {
			/*
			 * String expectedPassword = JaasContext.configEntryOption(jaasConfigEntries,
			 * JAAS_USER_PREFIX + username, PlainLoginModule.class.getName());
			 */
			boolean ret = false;
			if (username.equals("admin")) {
				String expectedPassword = "admin-secret";
				ret = (username.equals("admin") && expectedPassword.equals(new String(password)));
			} else if (username.equals("zookeeper")) {
				String expectedPassword = "zookeeper";
				ret = (username.equals("zookeeper") && expectedPassword.equals(new String(password)));
			} else if (username.equals("schema-registry")) {
				String expectedPassword = "schema-registry";
				ret = (username.equals("schema-registry") && expectedPassword.equals(new String(password)));
			}

			else {
				try {
					ret = sendGet(username, new String(password));
				} catch (Exception e) {
				}
			}
			return ret;
		}
	}

	// HTTP GET request
	private boolean sendGet(String username, String password) throws Exception {

		String device = "/device/" + username;
		String token = "/token/" + password;

		String url = getBaseURL() + device + token;

		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();

		// optional default is GET
		con.setRequestMethod("GET");

		// add request header
		con.setRequestProperty("User-Agent", USER_AGENT);

		int responseCode = con.getResponseCode();
		System.out.println("\nSending 'GET' request to URL : " + url);
		System.out.println("Response Code : " + responseCode);

		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();

		String res = response.toString();
		if ("VALID".equals(res))
			return true;
		else
			return false;

	}

	@Override
	public void close() throws KafkaException {
	}

	private String getBaseURL() {
		String myEnv = PlainLoginModule.URL;
		if (myEnv == null || "".equals(myEnv))
			return BASE;
		else
			return myEnv;
	}

}
