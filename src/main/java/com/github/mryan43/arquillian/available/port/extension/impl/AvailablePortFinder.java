package com.github.mryan43.arquillian.available.port.extension.impl;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.HashMap;
import java.util.Map;

import org.jboss.arquillian.core.api.annotation.Observes;
import org.jboss.arquillian.core.api.event.ManagerStarted;

public class AvailablePortFinder {

	private static final String SYSTEM_PROPERTY_NAME = "available.port";

	private static final Integer MAX_PORT_NUMBER = 65535;
	private static final Integer MIN_PORT_NUMBER = 8080;

	private final Map<ManagerStarted, String> previousValues = new HashMap<ManagerStarted, String>();

	// it's important that this method is invoked before the arquillian.xml file is read, which is typically triggered by the
	// ConfigurationRegistar observer, which is a ManagerStarted event observer.
	// Since the ManagerStarted event is the very first one fired by Arquillian, we need to use the same, but with a higher precedence.
	public void beforeConfigurationIsRead(@Observes(precedence = 50) ManagerStarted event) {

		for (int port = MIN_PORT_NUMBER; port <= MAX_PORT_NUMBER; port++) {
			try {
				ServerSocket serverSocket = new ServerSocket(port);
				serverSocket.close();
				previousValues.put(event, System.getProperty(SYSTEM_PROPERTY_NAME));
				System.setProperty(SYSTEM_PROPERTY_NAME, Integer.toString(port));
				return;
			}
			catch (IOException ioe) {
				// Tried binding port without success. Trying next port
			}
		}

		System.clearProperty("available.port");
		throw new IllegalStateException("Unable to find an available port between " + MIN_PORT_NUMBER
				+ " and " + MAX_PORT_NUMBER);

	}

	// Restore any previous value of the system property after the configuration has been read
	public void afterConfigurationIsRead(@Observes(precedence = -50) ManagerStarted event) {

		String previousValue = previousValues.get(event);
		if (previousValue == null){
			System.clearProperty(SYSTEM_PROPERTY_NAME);
		} else {
			System.setProperty(SYSTEM_PROPERTY_NAME, previousValue);
		}
	}

}
