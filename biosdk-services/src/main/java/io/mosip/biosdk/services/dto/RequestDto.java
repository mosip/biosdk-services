package io.mosip.biosdk.services.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Data Transfer Object (DTO) for encapsulating requests in the MOSIP Biometric
 * SDK service. This class holds version information and the request data.
 * <p>
 * The {@code RequestDto} class uses Lombok annotations for boilerplate code
 * reduction.
 * </p>
 *
 *
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@ToString
public class RequestDto {
	/**
	 * Spec version of the inner operation DTO (must be {@code "1.0"} for
	 * {@link io.mosip.biosdk.services.impl.spec_1_0.BioSdkServiceProviderImpl_V_1_0}).
	 */
	private String version;

	/**
	 * Base64 of the operation DTO JSON (init, match, quality, extract, segment, or
	 * convert). Not the outer envelope itself.
	 */
	private String request;
}