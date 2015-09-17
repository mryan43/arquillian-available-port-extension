package com.githum.mryan43.arquillian.available.port.extension.impl;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.oneOf;
import static org.junit.Assert.assertThat;

import org.jboss.arquillian.core.api.event.ManagerStarted;
import org.junit.Test;

import com.github.mryan43.arquillian.available.port.extension.impl.AvailablePortFinder;

public class AvailablePortFinderTest {

	@Test
	public void test_find_port(){

		// Given
		AvailablePortFinder portFinder = new AvailablePortFinder();
		ManagerStarted event = new ManagerStarted();
		System.setProperty("available.port", "previous value foo");

		// When
		portFinder.beforeConfigurationIsRead(event);

		// Then
		String[] possibleValues = new String[65537];
		for (int i = 8080; i <= 65535; i++){
			possibleValues[i] = Integer.toString(i);
		}
		assertThat(System.getProperty("available.port"), is(oneOf(possibleValues)));

		// When
		portFinder.afterConfigurationIsRead(event);

		// Then
		assertThat(System.getProperty("available.port"), is("previous value foo"));

	}
}
