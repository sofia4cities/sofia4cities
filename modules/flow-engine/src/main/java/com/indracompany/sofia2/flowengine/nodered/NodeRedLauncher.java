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
package com.indracompany.sofia2.flowengine.nodered;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecuteResultHandler;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteException;
import org.apache.commons.exec.ExecuteWatchdog;
import org.apache.commons.exec.PumpStreamHandler;
import org.eclipse.jetty.util.RolloverFileOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.indracompany.sofia2.flowengine.nodered.communication.NodeRedAdminClient;
import com.indracompany.sofia2.flowengine.nodered.sync.NodeRedDomainSyncMonitor;

@Component
public class NodeRedLauncher {

	private static final Logger log = LoggerFactory.getLogger(NodeRedLauncher.class);

	@Value("${sofia2.flowengine.node.path}")
	private String nodePath;
	@Value("${sofia2.flowengine.launcher.path}")
	private String nodeRedLauncherPath;
	@Value("${sofia2.flowengine.launcher.job}")
	private String nodeRedJob;
	@Value("${sofia2.flowengine.launcher.failsbeforestop.max:10}")
	private int maxFailsNumber;
	@Value("${sofia2.flowengine.launcher.failsbeforestop.time.millis:60000}")
	private int failsBeforeStopMillis;
	@Value("${sofia2.flowengine.launcher.reboot.delay.seconds:10}")
	private int rebootDelay;
	@Value("${sofia2.flowengine.launcher.debbugin.active:true}")
	private Boolean debuggMode;
	@Value("${sofia2.flowengine.launcher.debbugin.log:/tmp/log/flowEngine/}")
	private String debugLog;

	private ExecutorService exService = Executors.newSingleThreadExecutor();

	@Autowired
	private NodeRedAdminClient nodeRedAdminClient;

	@Autowired
	private NodeRedDomainSyncMonitor nodeRedMonitor;

	@PostConstruct
	public void init() {

		// Stop the flow engine in case it is still up
		try {
			nodeRedAdminClient.stopFlowEngine();
		} catch (Exception e) {
			log.warn("Could not stop Flow Engine.");
		}

		if (null != nodePath && null != nodeRedLauncherPath) {
			NodeRedLauncherExecutionThread launcherThread = new NodeRedLauncherExecutionThread(this.nodePath,
					this.nodeRedJob, this.nodeRedLauncherPath, this.maxFailsNumber, this.failsBeforeStopMillis,
					this.debuggMode, this.debugLog);

			exService.execute(launcherThread);
		}
	}

	private class NodeRedLauncherExecutionThread implements Runnable {

		private String nodePath;
		private String workingPath;
		private String launcherJob;
		private int maxFailsNumber;
		private int failsBeforeStopMillis;

		private boolean stop;
		private long lastFailTimestamp;
		private int consecutiveFails;
		private Boolean debugMode;
		private String logPath;
		private ExecuteWatchdog watchDog;

		public NodeRedLauncherExecutionThread(String nodePath, String launcherJob, String workingPath,
				int maxFailsNumber, int failsBeforeStopMillis, Boolean enableDebugging, String logPath) {
			this.nodePath = nodePath;
			this.workingPath = workingPath;
			this.launcherJob = launcherJob;
			this.maxFailsNumber = maxFailsNumber;
			this.failsBeforeStopMillis = failsBeforeStopMillis;

			this.stop = false;
			this.lastFailTimestamp = 0;
			this.consecutiveFails = 0;
			this.debugMode = enableDebugging;
			this.logPath = logPath;
		}

		public void stop() {
			this.stop = true;
			nodeRedMonitor.stopMonitor();
		}

		@Override
		public void run() {

			CommandLine commandLine = new CommandLine(this.nodePath);
			commandLine.addArgument(this.launcherJob);
			DefaultExecutor executor = new DefaultExecutor();
			executor.setExitValue(0);
			executor.setWorkingDirectory(new File(this.workingPath));

			while (!stop) {
				try {
					nodeRedAdminClient.resetSynchronizedWithBDC();
					nodeRedMonitor.stopMonitor();
					TimeUnit.SECONDS.sleep(rebootDelay);

					this.watchDog = new ExecuteWatchdog(ExecuteWatchdog.INFINITE_TIMEOUT);
					executor.setWatchdog(this.watchDog);

					DefaultExecuteResultHandler handler = new DefaultExecuteResultHandler() {
						@Override
						public void onProcessComplete(final int exitValue) {
							super.onProcessComplete(exitValue);
						}

						@Override
						public void onProcessFailed(final ExecuteException e) {
							super.onProcessFailed(e);
							processFail();
						}
					};
					if (!debugMode) {
						executor.setStreamHandler(new PumpStreamHandler(
								new RolloverFileOutputStream(logPath + File.separator + "yyyy_mm_dd.debug.log")));
					}
					executor.execute(commandLine, handler);
					nodeRedMonitor.startMonitor();
					handler.waitFor();
				} catch (Exception e) {
					log.error("Error arrancando NodeRED", e);
					this.processFail();
				}
			}

		}

		private void processFail() {
			long currentTimestamp = System.currentTimeMillis();
			// Hace mas de 1 minuto del ultimo fallo
			if (currentTimestamp > lastFailTimestamp + this.failsBeforeStopMillis) {
				this.consecutiveFails = 1;
			} else {
				this.consecutiveFails++;
			}
			lastFailTimestamp = System.currentTimeMillis();
			if (this.consecutiveFails > this.maxFailsNumber) {
				this.stop();
			}
		}

	}

}
