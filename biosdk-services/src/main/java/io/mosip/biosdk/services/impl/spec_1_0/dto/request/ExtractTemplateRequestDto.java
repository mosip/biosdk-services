package io.mosip.biosdk.services.impl.spec_1_0.dto.request;

import java.util.List;
import java.util.Map;

import io.mosip.kernel.biometrics.constant.BiometricType;
import io.mosip.kernel.biometrics.entities.BiometricRecord;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Data Transfer Object (DTO) representing a request for biometric template
 * extraction.
 * 
 * This class encapsulates data required for extracting a biometric template
 * from a captured sample. A biometric template is a compact representation of
 * the biometric data that can be used for identification or verification
 * purposes.
 * 
 * @since 1.0.0
 */

@Data
@NoArgsConstructor
@ToString
public class ExtractTemplateRequestDto {

	/**
	 * The captured biometric sample from which to extract the template.
	 * 
	 * This field can hold a {@link BiometricRecord} object representing the
	 * biometric data captured from a user (e.g., fingerprint image, iris image).
	 */
	private BiometricRecord sample;

	/**
	 * A list of biometric modalities for which templates need to be extracted
	 * .
	 * 
	 * This field can be used to specify which biometric modalities (e.g.,
	 * "Fingerprint", "Iris") need to have templates extracted from the provided
	 * sample. If not specified, templates might be extracted for all supported
	 * modalities by default.
	 */
	private List<BiometricType> modalitiesToExtract;

	/**
	 * A map of additional flags or configuration options for the template
	 * extraction process .
	 * 
	 * The specific keys and values in this map depend on the implementation of the
	 * template extraction service. It can be used to provide custom parameters or
	 * control the extraction behavior.
	 */
	private Map<String, String> flags;
}