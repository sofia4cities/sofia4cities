package authentication.server;

import java.security.Provider;
import java.security.Security;

import authentication.server.PlainSaslServer.PlainSaslServerFactory;

public class PlainSaslServerProvider extends Provider {

	private static final long serialVersionUID = 1L;

	@SuppressWarnings("deprecation")
	protected PlainSaslServerProvider() {
		super("Simple SASL/PLAIN Server Provider", 1.0, "Simple SASL/PLAIN Server Provider for Kafka");
		put("SaslServerFactory." + PlainSaslServer.PLAIN_MECHANISM, PlainSaslServerFactory.class.getName());
	}

	public static void initialize() {
		Security.addProvider(new PlainSaslServerProvider());
	}

}
