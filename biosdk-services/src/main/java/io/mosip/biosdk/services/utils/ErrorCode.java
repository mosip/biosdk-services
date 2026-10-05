/*
 * Copyright (c) 2018-2026 MOSIP.
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package io.mosip.biosdk.services.utils;

/**
 * Enumeration of error codes and messages used in the MOSIP Biometric SDK
 * service.
 * <p>
 * This enum provides predefined error codes and corresponding error messages to
 * handle specific error scenarios within the Biometric SDK.
 * </p>
 *
 *
 * @since 1.0.0
 * @copyright 2018-2026 MOSIP
 */
public enum ErrorCode {

	/**
	 * Error code indicating no Bio SDK service provider implementations found for a
	 * given version.
	 */
	NO_PROVIDERS("BIO_SDK_001", "No Bio SDK service provider implementations found for given version");

	/**
	 * Machine-readable code returned to clients (for example {@code BIO_SDK_001}).
	 */
	@SuppressWarnings({ "java:S1700" })
	private String errorCode;

	/**
	 * Human-readable description paired with {@link #errorCode}.
	 */
	private String errorMessage;

	/**
	 * Constructs an ErrorCode enum with the specified error code and error message.
	 *
	 * @param errorCode    the error code.
	 * @param errorMessage the corresponding error message.
	 */
	ErrorCode(String errorCode, String errorMessage) {
		this.errorCode = errorCode;
		this.errorMessage = errorMessage;
	}

	/**
	 * Retrieves the error code.
	 *
	 * @return the error code.
	 */
	public String getErrorCode() {
		return errorCode;
	}

	/**
	 * Retrieves the error message.
	 *
	 * @return the error message.
	 */
	public String getErrorMessage() {
		return errorMessage;
	}
}