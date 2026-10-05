package io.mosip.biosdk.services.impl.spec_1_0.dto.request;

import java.util.List;
import java.util.Map;

import io.mosip.kernel.biometrics.constant.BiometricType;
import io.mosip.kernel.biometrics.entities.BiometricRecord;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Data Transfer Object (DTO) representing a request for biometric sample
 * quality check.
 * 
 * This class encapsulates data required for assessing the quality of a captured
 * biometric sample. Biometric quality refers to the suitability of the sample
 * for further processing (e.g., matching, identification).
 * 
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@ToString
public class CheckQualityRequestDto {

	/**
	 * The captured biometric sample to be assessed for quality.
	 * 
	 * This field can hold a {@link BiometricRecord} object representing the
	 * biometric data captured from a user (e.g., fingerprint image, iris image).
	 */
	private BiometricRecord sample;

	/**
	 * A list of biometric modalities for which quality needs to be checked
	 * (optional).
	 * 
	 * This field can be used to specify which biometric modalities (e.g.,
	 * "Fingerprint", "Iris") need to have their quality assessed. If not specified,
	 * quality might be checked for all supported modalities present in the sample
	 * by default.
	 */
	private List<BiometricType> modalitiesToCheck;

	/**
	 * A map of additional flags or configuration options for the quality check
	 * process (optional).
	 * 
	 * The specific keys and values in this map depend on the implementation of the
	 * quality check service. It can be used to provide custom parameters or control
	 * the quality assessment behavior.
	 */
	private Map<String, String> flags;
}