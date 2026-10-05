/*
 * Copyright (c) 2018-2026 MOSIP.
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package io.mosip.biosdk.services.impl.spec_1_0.dto.request;

import java.util.List;
import java.util.Map;

import io.mosip.kernel.biometrics.constant.BiometricType;
import io.mosip.kernel.biometrics.entities.BiometricRecord;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Data Transfer Object (DTO) representing a request for biometric matching.
 * 
 * This class encapsulates data required for comparing a captured biometric
 * sample with one or more reference samples stored in a gallery.
 * 
 * @since 1.0.0
 * @copyright 2018-2026 MOSIP
 */

@Data
@NoArgsConstructor
@ToString
public class MatchRequestDto {
	/**
	 * The captured biometric sample to be compared against the gallery.
	 * 
	 * This field can hold a {@link BiometricRecord} object representing the
	 * biometric data captured from a user (e.g., fingerprint image, iris image).
	 */
	private BiometricRecord sample;

	/**
	 * An array of reference biometric samples stored in a gallery to be compared
	 * with.
	 * 
	 * This field can contain one or more {@link BiometricRecord} objects
	 * representing the reference biometric data against which the captured sample
	 * should be matched.
	 */
	private BiometricRecord[] gallery;

	/**
	 * A list of biometric modalities to be matched within the sample and gallery
	 * .
	 * 
	 * This field can be used to specify which biometric modalities (e.g.,
	 * "Fingerprint", "Iris") need to be matched. If not specified, all modalities
	 * present in both the sample and gallery might be matched by default.
	 */
	private List<BiometricType> modalitiesToMatch;

	/**
	 * A map of additional flags or configuration options for the matching process
	 * .
	 * 
	 * The specific keys and values in this map depend on the implementation of the
	 * matching service. It can be used to provide custom parameters or control the
	 * matching behavior.
	 */
	private Map<String, String> flags;
}