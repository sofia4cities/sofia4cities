package com.indracompany.sofia2.iotbroker.processor;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

import com.indracompany.sofia2.ssap.SSAPMessage;
import com.indracompany.sofia2.ssap.body.SSAPBodyIndicationMessage;

@Component
public class GatewayNotifierDelegate implements GatewayNotifier {

	Map<String, Consumer<SSAPMessage<SSAPBodyIndicationMessage>>> subscriptions = new HashMap<>();

	private ExecutorService executor;

	@PostConstruct
	private void init() {
		executor = Executors.newFixedThreadPool(10);

	}

	@Override
	public void addSubscriptionListener(String key, Consumer<SSAPMessage<SSAPBodyIndicationMessage>> consumer) {
		subscriptions.put(key, consumer);

	}

	@Override
	public void notify(SSAPMessage<SSAPBodyIndicationMessage> indication) {
		executor.submit(
				() -> subscriptions.values().stream().forEach(s -> s.accept(indication))
				);

	}

}
