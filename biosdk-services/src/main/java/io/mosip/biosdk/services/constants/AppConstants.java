package io.mosip.biosdk.services.constants;

/**
 * A utility class that holds application-wide constants for the MOSIP Biometric
 * SDK service.
 * <p>
 * This class provides constants used for logging and other application-specific
 * purposes. It is designed to be non-instantiable and contains a private
 * constructor that throws an {@link IllegalStateException} if an attempt is
 * made to instantiate it.
 * </p>
 *
 * <pre>
 * {@code
 * public class AppConstants {
 * 	// Application constants
 * }
 * }
 * </pre>
 *
 * @since 1.0
 */
public class AppConstants {
	/**
	 * Private constructor to prevent instantiation. This ensures that the class
	 * serves only as a container for constants.
	 *
	 * @throws IllegalStateException if an attempt is made to instantiate the class.
	 */
	private AppConstants() {
		throw new IllegalStateException("AppConstants class");
	}

	/**
	 * The session ID used for logging.
	 */
	public static final String LOGGER_SESSIONID = "BIO-SDK-PROVIDER";

	/**
	 * The ID type used for logging, indicating whether the context is registration
	 * or authentication.
	 */
	public static final String LOGGER_IDTYPE = "REGISTRATION / AUTH";
}