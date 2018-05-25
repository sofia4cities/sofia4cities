package com.indracompany.sofia2.raspberry.sensehat.sensehatlibrary.api;

import com.indracompany.sofia2.raspberry.sensehat.sensehatlibrary.api.dto.CommandResult;
import com.indracompany.sofia2.raspberry.sensehat.sensehatlibrary.connector.Command;
import com.indracompany.sofia2.raspberry.sensehat.sensehatlibrary.connector.CommandExecutor;
import com.indracompany.sofia2.raspberry.sensehat.sensehatlibrary.connector.CommandExecutorFactory;

/**
 * Created by jcincera on 22/06/2017.
 */
public abstract class APIBase {

	private CommandExecutor commandExecutor;

	protected APIBase() {
		this.commandExecutor = CommandExecutorFactory.get();
	}

	protected CommandResult execute(Command command, String... args) {
		return commandExecutor.execute(command, args);
	}
}
