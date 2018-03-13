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
package com.indracompany.sofia2.iotbroker.processor;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.function.Function;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

import com.indracompany.sofia2.ssap.SSAPMessage;
import com.indracompany.sofia2.ssap.body.SSAPBodyCommandMessage;
import com.indracompany.sofia2.ssap.body.SSAPBodyIndicationMessage;
import com.indracompany.sofia2.ssap.body.SSAPBodyReturnMessage;

@Component
public class GatewayNotifierDelegate implements GatewayNotifier {

	ConcurrentHashMap<String, Consumer<SSAPMessage<SSAPBodyIndicationMessage>>> subscriptions = new ConcurrentHashMap<>(10);
	ConcurrentHashMap<String,Function<SSAPMessage<SSAPBodyCommandMessage>, SSAPMessage<SSAPBodyReturnMessage>>> commands = new ConcurrentHashMap<>(10);

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
	public void addCommandListener(String key, Function<SSAPMessage<SSAPBodyCommandMessage>, SSAPMessage<SSAPBodyReturnMessage>> command) {
		commands.put(key, command);

	}

	@Override
	public void notify(SSAPMessage<SSAPBodyIndicationMessage> indication) {
		executor.submit(
				() -> subscriptions.values().stream().forEach(s -> s.accept(indication))
				);
	}

	@Override
	public void sendCommandAsync(SSAPMessage<SSAPBodyCommandMessage> command) {
		executor.submit(
				() -> commands.values().stream().forEach(s -> s.apply(command)));
	}



	//	@Override
	//	public SSAPMessage<SSAPBodyReturnMessage> sendCommandSync(SSAPMessage<SSAPBodyCommandMessage> cmd) {
	//
	//		final List<CompletableFuture<SSAPMessage<SSAPBodyReturnMessage>>> f = commands.values().stream().map(
	//				c -> CompletableFuture.supplyAsync(() -> c.apply(cmd)))
	//				.collect(Collectors.toList());
	//
	//		final CompletableFuture<Object> future = CompletableFuture.anyOf((CompletableFuture<?>[]) f.toArray());
	//
	//		try {
	//			final SSAPMessage<SSAPBodyReturnMessage> result = (SSAPMessage<SSAPBodyReturnMessage>) future.get(5, TimeUnit.SECONDS);
	//			return result;
	//		} catch (InterruptedException | ExecutionException | TimeoutException e) {
	//			// TODO Auto-generated catch block
	//			e.printStackTrace();
	//		}
	//		return null;
	//	}
}
