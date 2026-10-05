package io.mosip.biosdk.services.impl.spec_1_0.dto.request;

import java.util.Map;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Data Transfer Object (DTO) representing a request for initialization of a
 * biometric service.
 * 
 * This class encapsulates data required for initializing a biometric service.
 * Initialization typically involves loading configuration, establishing
 * connections, and preparing resources needed for biometric operations.
 * 
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@ToString
public class InitRequestDto {

	/**
	 * A map containing initialization parameters for the biometric service.
	 * 
	 * The specific keys and values in this map depend on the implementation of the
	 * biometric service. It can be used to provide configuration options or
	 * resources needed for initialization.
	 */
	private Map<String, String> initParams;
}