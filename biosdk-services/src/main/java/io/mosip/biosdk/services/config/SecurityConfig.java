package io.mosip.biosdk.services.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.firewall.DefaultHttpFirewall;
import org.springframework.security.web.firewall.HttpFirewall;

import jakarta.servlet.http.HttpServletResponse;

/**
 * Configuration class for setting up security configurations for the MOSIP
 * Biometric SDK service. This class defines beans for configuring HTTP
 * security, a custom HTTP firewall, and an authentication entry point.
 *
 * <pre>
 * {@code
 * @Configuration
 * public class SecurityConfig {
 * 	// Security configuration details
 * }
 * }
 * </pre>
 *
 * @since 1.0.0
 */
@Configuration
public class SecurityConfig {

	/**
	 * Configures and returns the default HTTP firewall.
	 * <p>
	 * This method returns a {@link DefaultHttpFirewall} bean to handle HTTP request
	 * firewalls.
	 * </p>
	 *
	 * @return a {@link HttpFirewall} instance configured as a default HTTP
	 *         firewall.
	 */
	@Bean
	public HttpFirewall defaultHttpFirewall() {
		return new DefaultHttpFirewall();
	}

	/**
	 * Configures and returns the security filter chain.
	 * <p>
	 * This method disables HTTP basic authentication and CSRF protection, as the
	 * service is designed to be stateless and use token-based authentication. All
	 * incoming HTTP requests are permitted without authentication.
	 * </p>
	 *
	 * @param httpSecurity the {@link HttpSecurity} to configure.
	 * @return a {@link SecurityFilterChain} instance configured with the specified
	 *         security settings.
	 * @throws Exception if an error occurs while configuring the security filter
	 *                   chain.
	 */
	//@SuppressWarnings({ "java:S4502" })
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
		httpSecurity.httpBasic(AbstractHttpConfigurer::disable);
		/*
		 * Disabling CSRF protection because this is a stateless API that uses
		 * token-based authentication
		 */
		httpSecurity.csrf(AbstractHttpConfigurer::disable);
		httpSecurity.authorizeHttpRequests(http -> http.anyRequest().permitAll());
		return httpSecurity.build();
	}

	/**
	 * Configures and returns an authentication entry point for handling
	 * unauthorized access attempts.
	 * <p>
	 * This method returns an {@link AuthenticationEntryPoint} bean that sends an
	 * HTTP 401 Unauthorized response when an unauthenticated request is received.
	 * </p>
	 *
	 * @return an {@link AuthenticationEntryPoint} instance configured to handle
	 *         unauthorized access attempts.
	 */
	@Bean
	public AuthenticationEntryPoint unauthorizedEntryPoint() {
		return (request, response, authException) -> response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
	}
}