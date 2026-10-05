package io.mosip.biosdk.services.impl.spec_1_0.dto.request;

import java.util.List;
import java.util.Map;

import io.mosip.kernel.biometrics.constant.BiometricType;
import io.mosip.kernel.biometrics.entities.BiometricRecord;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Data Transfer Object (DTO) representing a request for biometric data format
 * conversion.
 * 
 * This class encapsulates data required for converting a captured biometric
 * sample from one format to another.
 * 
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@ToString
public class ConvertFormatRequestDto {

	/**
	 * The biometric sample to be converted (optional).
	 * 
	 * This field can hold a {@link BiometricRecord} object representing the
	 * biometric data (e.g., fingerprint image, iris image) that needs to be
	 * converted. If not provided, the conversion might be based on source and
	 * target format specifications alone (depending on implementation).
	 */
	private BiometricRecord sample;

	/**
	 * The source format of the biometric data (e.g., "ISO19794_4_2011", "ISO19794_5_2011", "ISO19794_6_2011").
	 */
	private String sourceFormat;

	/**
	 * The target format to which the biometric data needs to be converted (e.g.,
	 * "ISO19794_5_2011", "ISO19794_4_2011/PNG", "ISO19794_5_2011/PNG". etc ).
	 */
	private String targetFormat;

	/**
	 * A map of additional parameters specific to the source format (optional).
	 * 
	 * The specific keys and values in this map depend on the source format and
	 * implementation. It can be used to provide additional information required for
	 * processing the source data.
	 */
	private Map<String, String> sourceParams;

	/**
	 * A map of additional parameters specific to the target format (optional).
	 * 
	 * The specific keys and values in this map depend on the target format and
	 * implementation. It can be used to provide configuration options for the
	 * output data.
	 */
	private Map<String, String> targetParams;

	/**
	 * A list of biometric modalities to be converted within the sample (optional).
	 * 
	 * This field can be used to specify which biometric modalities (e.g.,
	 * "Fingerprint", "Iris") need to be converted within the provided sample. If
	 * not specified, all modalities present in the sample data might be converted
	 * by default (depending on implementation).
	 */
	private List<BiometricType> modalitiesToConvert;
}