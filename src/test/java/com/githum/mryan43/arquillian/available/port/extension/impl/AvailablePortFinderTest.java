package com.githum.mryan43.arquillian.available.port.extension.impl;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.oneOf;
import static org.junit.Assert.assertThat;

import org.jboss.arquillian.core.api.event.ManagerStarted;
import org.junit.Test;

import com.github.mryan43.arquillian.available.port.extension.impl.AvailablePortFinder;

import java.io.IOException;

public class AvailablePortFinderTest {

	@Test
	public void test_find_port() throws IOException{

		// Given
		AvailablePortFinder portFinder = new AvailablePortFinder();
		ManagerStarted event = new ManagerStarted();
		System.setProperty("available.port", "previous value foo");

		// When
		portFinder.findAvailableNetworkPort(event);

		// Then
		String[] possibleValues = new String[65537];
		for (int i = 8080; i <= 65535; i++){
			possibleValues[i] = Integer.toString(i);
		}
		assertThat(System.getProperty("available.port"), is(oneOf(possibleValues)));
		System.out.println("Found available port : "+System.getProperty("available.port"));

		// When
		portFinder.cleanup(event);

		// Then
		assertThat(System.getProperty("available.port"), is("previous value foo"));

	}
}
