package io.mosip.biosdk.services;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Spring Boot entry point for the MOSIP BioSDK HTTP service.
 * <p>
 * This process is a REST wrapper around {@link io.mosip.kernel.biometrics.spi.IBioApiV2}.
 * The vendor (or mock) implementation is loaded at runtime from {@code -Dloader.path}
 * using the class named by {@code biosdk_bioapi_impl}. The packaged artifact uses
 * Spring Boot ZIP layout ({@code PropertiesLauncher}) so that extra SDK JARs can
 * be added without rebuilding this service.
 * </p>
 * <p>
 * There is no JDBC datasource. Vendor or mock-sdk JARs on {@code loader.path}
 * may register Hibernate/Hikari; those auto-configurations are excluded in
 * {@code application.properties} so Boot does not require a database URL.
 * </p>
 *
 * @since 1.0
 * @see io.mosip.biosdk.services.controller.MainController
 * @see io.mosip.biosdk.services.config.BioSdkLibConfig
 */
@SpringBootApplication
public class SdkApplication {

	/**
	 * Launches the BioSDK service. Typical local flags:
	 * {@code -Dloader.path}, {@code -Dbiosdk_bioapi_impl},
	 * {@code -Dspring.cloud.config.enabled=false},
	 * {@code -Dspring.profiles.active=local}.
	 *
	 * @param args command-line arguments forwarded to Spring Boot
	 */
	public static void main(String[] args) {
		SpringApplication.run(SdkApplication.class, args);
	}
}