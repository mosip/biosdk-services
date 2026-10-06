package io.mosip.biosdk.services.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;

/**
 * Configuration class for Swagger/OpenAPI documentation generation.
 * <p>
 * This class defines beans to configure the OpenAPI specification based on the
 * provided {@link OpenApiProperties}. It initializes an {@link OpenAPI} bean
 * and a {@link GroupedOpenApi} bean to customize and group API documentation
 * according to specified properties.
 * @since 1.0.0
 */
@Configuration
public class SwaggerConfig {
	/**
	 * SLF4J logger used when the OpenAPI bean has been built.
	 */
	private static final Logger logger = LoggerFactory.getLogger(SwaggerConfig.class);

	/**
	 * Bound {@code openapi.*} properties (title, servers, group, paths).
	 */
	private OpenApiProperties openApiProperties;

	/**
	 * Servlet context path. Used when {@code mosipbox.public.url} is unset so Try-it-out
	 * stays on the current host instead of the literal {@code ${mosipbox.public.url}}.
	 */
	@Value("${server.servlet.context-path:/}")
	private String contextPath;

	/**
	 * Constructs a {@code SwaggerConfig} instance with the provided
	 * {@link OpenApiProperties}.
	 *
	 * @param openApiProperties The properties containing OpenAPI configuration
	 *                          details
	 */
	@Autowired
	public SwaggerConfig(OpenApiProperties openApiProperties) {
		this.openApiProperties = openApiProperties;
	}

	/**
	 * Creates an {@link OpenAPI} bean configured with title, version, description,
	 * and license information.
	 *
	 * @return Configured {@link OpenAPI} instance representing the OpenAPI
	 *         specification
	 */
	@Bean
	public OpenAPI openApi() {
		OpenAPI api = new OpenAPI().components(new Components())
				.info(new Info().title(openApiProperties.getInfo().getTitle())
						.version(openApiProperties.getInfo().getVersion())
						.description(openApiProperties.getInfo().getDescription())
						.license(new License().name(openApiProperties.getInfo().getLicense().getName())
								.url(openApiProperties.getInfo().getLicense().getUrl())));

		openApiProperties.getService().getServers().forEach(server -> api
				.addServersItem(new Server().description(server.getDescription()).url(resolveServerUrl(server.getUrl()))));
		logger.info("swagger open api bean is ready");
		return api;
	}

	/**
	 * Uses the configured public URL when it is a real URL. An unresolved
	 * {@code ${mosipbox.public.url}} placeholder is treated as the context path so
	 * Swagger Try-it-out calls this host, not {@code /v3/api-docs/${mosipbox.public.url}/...}.
	 *
	 * @param url OpenAPI server URL from {@code openapi.service.servers}
	 * @return usable server URL
	 */
	private String resolveServerUrl(String url) {
		if (url == null || url.isBlank() || url.contains("${")) {
			return contextPath;
		}
		return url;
	}

	/**
	 * Creates a {@link GroupedOpenApi} bean to group API paths based on configured
	 * properties.
	 *
	 * @return {@link GroupedOpenApi} instance representing grouped API paths
	 */
	@Bean
	public GroupedOpenApi groupedOpenApi() {
		return GroupedOpenApi.builder().group(openApiProperties.getGroup().getName())
				.pathsToMatch(openApiProperties.getGroup().getPaths().stream().toArray(String[]::new)).build();
	}
}