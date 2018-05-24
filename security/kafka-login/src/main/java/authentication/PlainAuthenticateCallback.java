package authentication;

import javax.security.auth.callback.Callback;

public class PlainAuthenticateCallback implements Callback {
	private final char[] password;
	private boolean authenticated;

	/**
	 * Creates a callback with the password provided by the client
	 * 
	 * @param password
	 *            The password provided by the client during SASL/PLAIN
	 *            authentication
	 */
	public PlainAuthenticateCallback(char[] password) {
		this.password = password;
	}

	/**
	 * Returns the password provided by the client during SASL/PLAIN
	 * authentication
	 */
	public char[] password() {
		return password;
	}

	/**
	 * Returns true if client password matches expected password, false
	 * otherwise. This state is set the server-side callback handler.
	 */
	public boolean authenticated() {
		return this.authenticated;
	}

	/**
	 * Sets the authenticated state. This is set by the server-side callback
	 * handler by matching the client provided password with the expected
	 * password.
	 *
	 * @param authenticated
	 *            true indicates successful authentication
	 */
	public void authenticated(boolean authenticated) {
		this.authenticated = authenticated;
	}
}
