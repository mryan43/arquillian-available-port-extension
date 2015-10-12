package com.github.mryan43.arquillian.available.port.extension.impl;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.SocketException;
import java.util.*;

import org.jboss.arquillian.core.api.annotation.Observes;
import org.jboss.arquillian.core.api.event.ManagerStarted;

public class AvailablePortFinder {

	private static final String SYSTEM_PROPERTY_NAME = "available.port";

	private static final Integer MAX_PORT_NUMBER = 0xFFFF; //65535
	private static final Integer MIN_PORT_NUMBER = 8080;

	private final Map<ManagerStarted, String> previousValues = new HashMap<ManagerStarted, String>();

	// it's important that this method is invoked before the arquillian.xml file is read, which is typically triggered by the
	// ConfigurationRegistar observer, which is a ManagerStarted event observer.
	// Since the ManagerStarted event is the very first one fired by Arquillian, we need to use the same, but with a higher precedence.
	public void findAvailableNetworkPort(@Observes(precedence = 50) ManagerStarted event) {

		try{
			Set<InetAddress> addresses = getAllNetworkAddresses();

			for (int port = MIN_PORT_NUMBER; port <= MAX_PORT_NUMBER; port++) {
				boolean failed = false;
				for (InetAddress address : addresses){
					try{
						ServerSocket serverSocket = new ServerSocket(port,5,address);
						serverSocket.close();
					}
					catch (IOException ioe) {
						// Port binding failed on one of the interfaces, let's try next port
						failed = true;
						break;
					}
				}
				// managed to bind on all interfaces ?
				if (!failed){
					previousValues.put(event, System.getProperty(SYSTEM_PROPERTY_NAME));
					System.setProperty(SYSTEM_PROPERTY_NAME, Integer.toString(port));
					return;
				}
			}
		} catch (SocketException e){
			// Failed to list all network addresses, we fallback and try to bind on the default port
			for (int port = MIN_PORT_NUMBER; port <= MAX_PORT_NUMBER; port++) {
				try {
					ServerSocket serverSocket = new ServerSocket(port);
					serverSocket.close();
					previousValues.put(event, System.getProperty(SYSTEM_PROPERTY_NAME));
					System.setProperty(SYSTEM_PROPERTY_NAME, Integer.toString(port));
					return;
				}
				catch (IOException ioe) {
					// Port binding failed let's try next port
				}
			}
		}

		throw new IllegalStateException("Unable to find an available port between " + MIN_PORT_NUMBER
				+ " and " + MAX_PORT_NUMBER);

	}

	// Restore any previous value of the system property after the configuration has been read
	public void cleanup(@Observes(precedence = -50) ManagerStarted event) {

		String previousValue = previousValues.get(event);
		if (previousValue == null){
			System.clearProperty(SYSTEM_PROPERTY_NAME);
		} else {
			System.setProperty(SYSTEM_PROPERTY_NAME, previousValue);
		}
	}

	private static Set<InetAddress> getAllNetworkAddresses() throws SocketException {
		Set<InetAddress> result = new HashSet<InetAddress>();
		Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
		for (NetworkInterface networkInterface : Collections.list(networkInterfaces)) {
			Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses();
			for (InetAddress inetAddress : Collections.list(inetAddresses)) {
				result.add(inetAddress);
			}
		}
		return result;
	}

}
