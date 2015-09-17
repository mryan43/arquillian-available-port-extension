package com.github.mryan43.arquillian.available.port.extension;

import org.jboss.arquillian.core.spi.LoadableExtension;

import com.github.mryan43.arquillian.available.port.extension.impl.AvailablePortFinder;

public class AvailablePortExtension implements LoadableExtension {
	public void register(ExtensionBuilder builder) {
		builder.observer(AvailablePortFinder.class);
	}
}
