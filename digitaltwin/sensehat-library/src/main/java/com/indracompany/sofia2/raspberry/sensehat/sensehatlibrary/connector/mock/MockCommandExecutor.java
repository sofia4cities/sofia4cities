package com.indracompany.sofia2.raspberry.sensehat.sensehatlibrary.connector.mock;

import com.indracompany.sofia2.raspberry.sensehat.sensehatlibrary.api.dto.CommandResult;
import com.indracompany.sofia2.raspberry.sensehat.sensehatlibrary.connector.Command;
import com.indracompany.sofia2.raspberry.sensehat.sensehatlibrary.connector.CommandExecutor;

/**
 * Created by jcincera on 04/07/2017.
 */
public class MockCommandExecutor implements CommandExecutor {

	@Override
	public CommandResult execute(Command command, String... args) {
		System.out.println("Mocking command: " + command.getCommand());

		MockCommandResult result = new MockCommandResult("");
		result.setCommand(command);

		return result;
	}

}
