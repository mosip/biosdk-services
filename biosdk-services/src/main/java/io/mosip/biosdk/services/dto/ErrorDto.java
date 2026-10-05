/*
 * Copyright (c) 2018-2026 MOSIP.
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package io.mosip.biosdk.services.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Data Transfer Object (DTO) for encapsulating error details in the MOSIP
 * Biometric SDK service. This class holds an error code and an error message.
 * <p>
 * The {@code ErrorDto} class uses Lombok annotations for boilerplate code
 * reduction.
 * </p>
 *
 *
 * @since 1.0.0
 * @copyright 2018-2026 MOSIP
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ErrorDto {
	/**
	 * Error code (for example {@link io.mosip.biosdk.services.constants.ErrorMessages}
	 * name or {@link io.mosip.biosdk.services.utils.ErrorCode#getErrorCode()}).
	 */
	private String code;

	/**
	 * Human-readable explanation of {@link #code}.
	 */
	private String message;
}