package io.mosip.biosdk.services.dto;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

/**
 * Data Transfer Object (DTO) for holding authorized roles configuration in the
 * MOSIP Biometric SDK service. This class represents roles authorized to access
 * specific functionalities or endpoints.
 * <p>
 * The {@code AuthorizedRolesDTO} class is annotated with Spring's
 * {@link Component} and {@link ConfigurationProperties} annotations to indicate
 * it as a component managing configuration properties. It uses Lombok
 * annotations for boilerplate code reduction.
 * </p>
 *
 *
 * @since 1.0.0
 */
@Component("authorizedRoles")
@ConfigurationProperties(prefix = "mosip.role.biosdk")
@Data
public class AuthorizedRolesDTO {

	/**
	 * List of roles authorized to access the 'getservicestatus' functionality.
	 */
	private List<String> getservicestatus;
}