package io.mosip.biosdk.services.impl.spec_1_0.dto.request;

import java.util.List;
import java.util.Map;

import io.mosip.kernel.biometrics.constant.BiometricType;
import io.mosip.kernel.biometrics.entities.BiometricRecord;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Data Transfer Object (DTO) representing a request for biometric segmentation.
 * 
 * This class encapsulates data required for the segmentation of a captured
 * biometric sample. Segmentation is the process of dividing a biometric sample
 * into meaningful regions for further processing.
 * 
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@ToString
public class SegmentRequestDto {

	/**
	 * The captured biometric sample to be segmented.
	 * 
	 * This field can hold a {@link BiometricRecord} object representing the
	 * biometric data captured from a user (e.g., fingerprint image, iris image).
	 */
	private BiometricRecord sample;

	/**
	 * A list of biometric modalities to be segmented within the sample .
	 * 
	 * This field can be used to specify which biometric modalities (e.g.,
	 * "FINGERPRINT", "IRIS") need to be segmented from the provided sample. If not
	 * specified, all supported modalities might be segmented by default.
	 */
	private List<BiometricType> modalitiesToSegment;

	/**
	 * A map of additional flags or configuration options for the segmentation
	 * process.
	 * 
	 * The specific keys and values in this map depend on the implementation of the
	 * segmentation service. It can be used to provide custom parameters or control
	 * the segmentation behavior.
	 */
	private Map<String, String> flags;
}