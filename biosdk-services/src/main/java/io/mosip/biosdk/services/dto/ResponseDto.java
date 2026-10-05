package io.mosip.biosdk.services.dto;

import java.util.List;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Data Transfer Object (DTO) for encapsulating responses in the MOSIP Biometric
 * SDK service. This generic class can hold response data, version information,
 * response time, and error details.
 * <p>
 * The {@code ResponseDto} class uses Lombok annotations for boilerplate code
 * reduction.
 * </p>
 *
 * <pre>
 * {
 * 	&#64;code
 * 	&#64;Data
 * 	&#64;NoArgsConstructor
 * 	@ToString
 * 	public class ResponseDto<T> {
 * 		// Response DTO details
 * 	}
 * }
 * </pre>
 *
 * @param <T> the type of the response data.
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@ToString
public class ResponseDto<T> {

	/**
	 * Spec version echoed from the request ({@code "1.0"}).
	 */
	@ApiModelProperty(value = "request version", position = 2)
	private String version;

	/**
	 * UTC timestamp when this response was built ({@code yyyy-MM-dd'T'HH:mm:ss.SSS'Z'}).
	 */
	@ApiModelProperty(value = "Response Time", position = 3)
	private String responsetime;

	/**
	 * Operation payload from the vendor SDK (for example {@code SDKInfo} or
	 * {@code Response}). Empty string in the template before the provider runs.
	 */
	@ApiModelProperty(value = "Response", position = 4)
	private T response;

	/**
	 * Top-level error list. Empty on success; HTTP status remains 200 when this is
	 * populated.
	 */
	@ApiModelProperty(value = "Error Details", position = 5)
	private List<ErrorDto> errors;
}
