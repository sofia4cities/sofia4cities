package digitaltwin.device.status;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.PostConstruct;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.indracompany.sofia2.digitaltwin.logic.api.DigitalTwinApi;
import com.indracompany.sofia2.raspberry.sensehat.sensehatlibrary.api.SenseHat;
import com.indracompany.sofia2.raspberry.sensehat.sensehatlibrary.api.dto.joystick.Direction;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class SenseHatStatus extends DigitalTwinStatus {

	private ExecutorService exService = Executors.newSingleThreadExecutor();

	@Autowired
	private DigitalTwinApi twinApi;

	private static Invocable invocable;

	@PostConstruct
	public void init() {
		this.twinApi.init();
		super.init();

		ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");
		this.invocable = (Invocable) engine;
		try {
			ClassLoader classLoader = this.getClass().getClassLoader();
			engine.eval(new InputStreamReader(classLoader.getResource("static/js/logic.js").openStream()));

		} catch (ScriptException e1) {
			log.error("Execution logic for action", e1);
		} catch (FileNotFoundException e) {
			log.error("File logic.js not found.", e);
		} catch (IOException e) {
			log.error("File logic.js not found.", e);
		}

		log.info("Wait for JoystickEvent");
		joystickEvent();
	}

	@Override
	public void setPressure(Double pressure) {
		SenseHat senseHat = new SenseHat();
		log.info("setPressure: " + senseHat.environmentalSensor.getPressure());
		super.setPressure((double) senseHat.environmentalSensor.getPressure());
	}

	@Override
	public void setTemperature(Double temp) {
		SenseHat senseHat = new SenseHat();
		log.info("setTemperature: " + senseHat.environmentalSensor.getPressure());
		super.setTemperature((double) senseHat.environmentalSensor.getTemperature());
	}

	@Override
	public void setHumidity(Double hum) {
		SenseHat senseHat = new SenseHat();
		log.info("setHumidity: " + senseHat.environmentalSensor.getPressure());
		super.setHumidity((double) senseHat.environmentalSensor.getHumidity());
	}

	public void joystickEvent() {

		exService.execute(new Runnable() {
			@Override
			public void run() {

				while (true) {
					log.info("JoystickEvent execution");
					SenseHat senseHat = new SenseHat();
					com.indracompany.sofia2.raspberry.sensehat.sensehatlibrary.api.dto.JoystickEvent event = senseHat.joystick
							.waitForEvent();

					log.info("Joystick event: ");

					Direction direction = event.getDirection();

					// Execute javascript logic for the event

					try {

						if (direction.equals(Direction.RIGHT)) {
							invocable.invokeFunction("joystickEventRight", direction.name());
						} else if (direction.equals(Direction.LEFT)) {
							invocable.invokeFunction("joystickEventLeft", direction.name());
						} else if (direction.equals(Direction.UP)) {
							invocable.invokeFunction("joystickEventUp", direction.name());
						} else if (direction.equals(Direction.DOWN)) {
							invocable.invokeFunction("joystickEventDown", direction.name());
						} else if (direction.equals(Direction.MIDDLE)) {
							invocable.invokeFunction("joystickEventMiddle", direction.name());
						}

					} catch (ScriptException e1) {
						log.error("Execution logic for action", e1);
					} catch (NoSuchMethodException e2) {
						log.error("Event joystickEvent not found", e2);
					}
				}
			}
		});
	}

}
