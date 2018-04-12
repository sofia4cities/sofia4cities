package com.indracompany.sofia2.digitaltwin.logic;

import java.io.InputStreamReader;

import javax.annotation.PostConstruct;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class LogicMainFunctionExecutor {

	@Value("${device.logic.main.loop.delay.seconds:60}")
	private int mainFunctionDelay;

	@PostConstruct
	public void init() {
		new LogicMainFuncionExecutorThread().start();
	}

	class LogicMainFuncionExecutorThread extends Thread {

		@Override
		public void run() {
			ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");
			Invocable invocable = (Invocable) engine;
			try {
				ClassLoader classLoader = getClass().getClassLoader();
				engine.eval(new InputStreamReader(classLoader.getResource("static/js/logic.js").openStream()));
				invocable.invokeFunction("init");
				while (true) {
					try {
						invocable.invokeFunction("main");
					} catch (Exception e) {
						log.error("Error executing main function", e);
					}
					try {
						Thread.sleep(mainFunctionDelay * 1000);
					} catch (Exception e) {
					}
				}

			} catch (Exception e) {
				log.error("Error executing main function", e);
			}

		}

	}

}
