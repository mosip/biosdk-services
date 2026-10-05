package io.mosip.biosdk.services.constants;

/**
 * Enum representing various response statuses for the MOSIP Biometric SDK service.
 * Each enum constant has an associated HTTP status code and a status message.
 * <p>
 * This enum is used to standardize the response status codes and messages returned by the service.
 * </p>
 *
 * <pre>
 * {@code
 * public enum ResponseStatus {
 *     SUCCESS(200, "OK"),
 *     INVALID_INPUT(401, "Invalid Input Parameter - %s"),
 *     MISSING_INPUT(402, "Missing Input Parameter - %s"),
 *     QUALITY_CHECK_FAILED(403, "Quality check of Biometric data failed"),
 *     POOR_DATA_QUALITY(406, "Data provided is of poor quality"),
 *     UNKNOWN_ERROR(500, "UNKNOWN_ERROR");
 * }
 * </pre>
 *
 * @since 1.0
 */
public enum ResponseStatus {
	 /**
     * Indicates that the request was successful.
     */
    SUCCESS(200, "OK"),
    
    /**
     * Indicates that the request had an invalid input parameter.
     */
    INVALID_INPUT(401, "Invalid Input Parameter - %s"),
    
    /**
     * Indicates that the request was missing a required input parameter.
     */
    MISSING_INPUT(402, "Missing Input Parameter - %s"),
    
    /**
     * Indicates that the quality check of the biometric data failed.
     */
    QUALITY_CHECK_FAILED(403, "Quality check of Biometric data failed"),
    
    /**
     * Indicates that the data provided is of poor quality.
     */
    POOR_DATA_QUALITY(406, "Data provided is of poor quality"),
    
    /**
     * Indicates that an unknown error occurred.
     */
    UNKNOWN_ERROR(500, "UNKNOWN_ERROR");

    /**
     * Numeric status associated with this outcome (not always an HTTP status).
     */
    private final int statusCode;

    /**
     * Message template; some values contain {@code %s} for a field name.
     */
    private final String statusMessage;

    /**
     * Constructs a new {@code ResponseStatus} with the specified status code and status message.
     *
     * @param statusCode the HTTP status code associated with the response status.
     * @param statusMessage the status message associated with the response status.
     */
    ResponseStatus(int statusCode, String statusMessage) {
        this.statusCode = statusCode;
        this.statusMessage = statusMessage;
    }

    /**
     * Returns the HTTP status code associated with the response status.
     *
     * @return the HTTP status code.
     */
    public int getStatusCode() {
        return statusCode;
    }

    /**
     * Returns the status message associated with the response status.
     *
     * @return the status message.
     */
    public String getStatusMessage() {
        return statusMessage;
    }
}