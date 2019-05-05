/*
 * Copyright from 2019 Nhut Do <mr.nhut@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package fi.donhut.simplemonitorclient;

import fi.donhut.simplemonitorclient.ui.AppTrayIcon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.awt.*;

/**
 * Main application.
 *
 * @author Nhut Do (mr.nhut@gmail.com)
 */
@SpringBootApplication
@EnableScheduling
public class SimpleMonitorClientApplication {

	private static final Logger LOG = LoggerFactory.getLogger(SimpleMonitorClientApplication.class);

	private static ConfigurableApplicationContext applicationContext;
	private static AppTrayIcon appTrayIcon;

	public static void main(String[] args) throws AWTException {
		final SpringApplicationBuilder builder = new SpringApplicationBuilder(SimpleMonitorClientApplication.class);
		builder.headless(false);
		applicationContext = builder.run(args);

		appToRunInSystemTrayIfSupported(applicationContext);
	}

	private static void appToRunInSystemTrayIfSupported(
			final ConfigurableApplicationContext applicationContext) throws AWTException {
		if (SystemTray.isSupported()) {
			LOG.debug("----System tray is supported. Creating system tray...");
			appTrayIcon = new AppTrayIcon(applicationContext);
			LOG.info("-----Created system tray for this application.");
			return;
		}
		LOG.debug("-----System tray is NOT supported. Skip create system tray for the application.");
	}
}
