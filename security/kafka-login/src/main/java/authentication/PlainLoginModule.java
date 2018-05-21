package authentication;

import java.util.Map;

import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;

import authentication.server.PlainSaslServerProvider;

public class PlainLoginModule implements LoginModule {

	private static final String USERNAME_CONFIG = "username";
	private static final String PASSWORD_CONFIG = "password";

	public static String URL = "";

	static {
		PlainSaslServerProvider.initialize();
	}

	@Override
	public void initialize(Subject subject, CallbackHandler callbackHandler, Map<String, ?> sharedState,
			Map<String, ?> options) {

		System.out.println("PlainModule Initialize");
		System.out.println(options.toString());
		String url = (String) options.get("url");
		if (url != null) {
			System.out.println(url);
			PlainLoginModule.URL = url;
		}

		String username = (String) options.get(USERNAME_CONFIG);
		if (username != null)
			subject.getPublicCredentials().add(username);
		String password = (String) options.get(PASSWORD_CONFIG);
		if (password != null)
			subject.getPrivateCredentials().add(password);
	}

	@Override
	public boolean login() throws LoginException {
		return true;
	}

	@Override
	public boolean logout() throws LoginException {
		return true;
	}

	@Override
	public boolean commit() throws LoginException {
		return true;
	}

	@Override
	public boolean abort() throws LoginException {
		return false;
	}
}