/*
 * Copyright (c) 2018-2026 MOSIP.
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package io.mosip.biosdk.services.config;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.logger.logback.factory.Logfactory;

/**
 * Configuration class for setting up the logger for the MOSIP Biometric SDK
 * service. This class provides a method to configure and retrieve a logger
 * instance for a given class.
 * <p>
 * This class is {@code final} and is not instantiable. Call {@link #logConfig(Class)}
 * when declaring {@code private static final Logger logger} on service types.
 * </p>
 *
 * @since 1.0.0
 * @copyright 2018-2026 MOSIP
 */
public final class LoggerConfig {
	/**
	 * Private constructor to prevent instantiation. This ensures that the class
	 * serves only as a utility for configuring loggers.
	 */
	private LoggerConfig() {
	}

	/**
	 * Configures and returns a logger instance for the specified class.
	 * <p>
	 * This method uses the {@link Logfactory#getSlf4jLogger(Class)} method to
	 * create a logger that adheres to the SLF4J logging facade.
	 * </p>
	 *
	 * @param clazz the class for which the logger is to be configured.
	 * @return a {@link Logger} instance for the specified class.
	 */
	public static Logger logConfig(Class<?> clazz) {
		return Logfactory.getSlf4jLogger(clazz);
	}
}