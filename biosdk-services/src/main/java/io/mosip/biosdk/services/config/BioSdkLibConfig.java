/*
 * Copyright (c) 2018-2026 MOSIP.
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package io.mosip.biosdk.services.config;

import static io.mosip.biosdk.services.constants.AppConstants.LOGGER_IDTYPE;
import static io.mosip.biosdk.services.constants.AppConstants.LOGGER_SESSIONID;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.env.Environment;

import io.mosip.biosdk.services.constants.ErrorMessages;
import io.mosip.biosdk.services.exceptions.BioSDKException;
import io.mosip.kernel.biometrics.spi.IBioApiV2;
import jakarta.annotation.PostConstruct;

/**
 * Configuration class for setting up the MOSIP Biometric SDK library. This
 * class is responsible for validating and initializing the Biometric SDK
 * implementation.
 * <p>
 * It reads the SDK implementation class name from the application environment
 * properties and ensures that the class is available and can be instantiated.
 * </p>
 *
 * <pre>
 * {@code
 * @Configuration
 * public class BioSdkLibConfig {
 * 	// Configuration details
 * }
 * }
 * </pre>
 *
 * @since 1.0.0
 * @copyright 2018-2026 MOSIP
 */
@Configuration
public class BioSdkLibConfig {
	/**
	 * SLF4J logger for SDK class-name validation and bean creation.
	 */
	private static final Logger logger = LoggerFactory.getLogger(BioSdkLibConfig.class);

	/**
	 * Spring environment used to read {@code biosdk_bioapi_impl}.
	 */
	private Environment env;

	/**
	 * Constructs a new {@code BioSdkLibConfig} with the specified environment.
	 *
	 * @param env the environment containing the application properties.
	 */
	@Autowired
	public BioSdkLibConfig(Environment env) {
		this.env = env;
	}

	/**
	 * Validates the Bio SDK library class specified in the application properties.
	 * <p>
	 * This method is called after the bean's properties have been set. It checks if
	 * the {@code biosdk_bioapi_impl} property is specified and if the corresponding
	 * class is available.
	 * </p>
	 *
	 * @throws ClassNotFoundException if the specified class is not found.
	 */
	@PostConstruct
	public void validateBioSdkLib() throws ClassNotFoundException {
		String sdkClass = this.env.getProperty("biosdk_bioapi_impl");
		logger.info(LOGGER_SESSIONID, LOGGER_IDTYPE, "validateBioSdkLib::Biosdk class: ", sdkClass);
		if (StringUtils.isNotBlank(sdkClass)) {
			logger.debug(LOGGER_SESSIONID, LOGGER_IDTYPE, "validating Bio SDK Class is present or not");
			Class.forName(sdkClass);
		}

		logger.debug(LOGGER_SESSIONID, LOGGER_IDTYPE, "validateBioSdkLib: Bio SDK Class is not provided");
	}

	/**
	 * Creates and returns an instance of the Bio SDK implementation.
	 * <p>
	 * This method retrieves the class name of the Bio SDK implementation from the
	 * environment properties, instantiates it using reflection, and returns the
	 * instance. If the class name is not specified or if instantiation fails, an
	 * exception is thrown.
	 * </p>
	 *
	 * @return an instance of {@link IBioApiV2}.
	 * @throws ClassNotFoundException    if the specified class is not found.
	 * @throws InstantiationException    if the class cannot be instantiated.
	 * @throws IllegalAccessException    if the class or its nullary constructor is
	 *                                   not accessible.
	 * @throws NoSuchMethodException     if the class does not have a nullary
	 *                                   constructor.
	 * @throws InvocationTargetException if the underlying constructor throws an
	 *                                   exception.
	 * @throws BioSDKException           if no Bio SDK provider is found.
	 */
	@Bean
	@Lazy
	public IBioApiV2 iBioApi() throws ClassNotFoundException, InstantiationException, IllegalAccessException,
			NoSuchMethodException, SecurityException, IllegalArgumentException, InvocationTargetException {
		String sdkClass = this.env.getProperty("biosdk_bioapi_impl");
		logger.info(LOGGER_SESSIONID, LOGGER_IDTYPE, "iBioApi::Biosdk class:", sdkClass);
		if (StringUtils.isNotBlank(sdkClass)) {
			logger.debug(LOGGER_SESSIONID, LOGGER_IDTYPE, "instance of Bio SDK is created");
			Constructor<?> constructor = Class.forName(sdkClass).getDeclaredConstructor();
			return (IBioApiV2) constructor.newInstance();
		} else {
			logger.debug(LOGGER_SESSIONID, LOGGER_IDTYPE, "no Bio SDK is provided");
			throw new BioSDKException(ErrorMessages.NO_BIOSDK_PROVIDER_FOUND.toString(),
					ErrorMessages.NO_BIOSDK_PROVIDER_FOUND.getMessage());
		}
	}
}
