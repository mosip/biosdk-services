/*
 * Copyright (c) 2018-2026 MOSIP.
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package io.mosip.biosdk.services.spi;

import io.mosip.biosdk.services.dto.RequestDto;

/**
 * Service Provider Interface (SPI) defining operations for the MOSIP Biometric
 * SDK service.
 * <p>
 * Implementing classes provide functionality related to biometric operations
 * such as initialization, quality checking, matching, template extraction,
 * segmentation, and format conversion.
 * </p>
 *
 *
 * @since 1.0.0
 * @copyright 2018-2026 MOSIP
 */
public interface BioSdkServiceProvider {

	/**
	 * Retrieves the specific version of the BioSDK service provider.
	 *
	 * @return the version of the BioSDK service provider.
	 */
	Object getSpecVersion();

	/**
	 * Initializes the BioSDK service provider with the provided request data.
	 *
	 * @param request the request data encapsulated in a {@link RequestDto}.
	 * @return the result of initialization operation.
	 */
	Object init(RequestDto request);

	/**
	 * Checks the quality of biometric data provided in the request.
	 *
	 * @param request the request data encapsulated in a {@link RequestDto}.
	 * @return the result of quality checking operation.
	 */
	Object checkQuality(RequestDto request);

	/**
	 * Matches biometric data provided in the request.
	 *
	 * @param request the request data encapsulated in a {@link RequestDto}.
	 * @return the result of matching operation.
	 */
	Object match(RequestDto request);

	/**
	 * Extracts biometric template from the provided data in the request.
	 *
	 * @param request the request data encapsulated in a {@link RequestDto}.
	 * @return the result of template extraction operation.
	 */
	Object extractTemplate(RequestDto request);

	/**
	 * Segments biometric data provided in the request.
	 *
	 * @param request the request data encapsulated in a {@link RequestDto}.
	 * @return the result of segmentation operation.
	 */
	Object segment(RequestDto request);

	/**
	 * Converts biometric data format. Spec {@code "1.0"} delegates to
	 * {@link io.mosip.kernel.biometrics.spi.IBioApiV2#convertFormatV2}.
	 *
	 * @param request the request data encapsulated in a {@link RequestDto}.
	 * @return the result of format conversion operation.
	 */
	Object convertFormat(RequestDto request);
}