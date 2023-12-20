package io.mosip.biosdk.services.config;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.logger.logback.factory.Logfactory;

public final class LoggerConfig {
	
	/**
	 * Instantiates a new biosdk service  logger.
	 */
	private LoggerConfig() {

	}

	public static Logger logConfig(Class<?> clazz) {
		return Logfactory.getSlf4jLogger(clazz);
	}

}
