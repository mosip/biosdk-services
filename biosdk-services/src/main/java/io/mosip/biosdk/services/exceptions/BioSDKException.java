/*
 * Copyright (c) 2018-2026 MOSIP.
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package io.mosip.biosdk.services.exceptions;

import io.mosip.kernel.core.exception.BaseUncheckedException;

/**
 * Custom unchecked exception for representing errors specific to the MOSIP Biometric SDK service.
 * <p>
 * This exception extends {@link BaseUncheckedException}, providing structured error handling
 * capabilities with error code, error message, and optional cause.
 * </p>
 *
 *
 * @since 1.0
 * @copyright 2018-2026 MOSIP
 */
public class BioSDKException extends BaseUncheckedException {
    /**
     * Serializable Version Id
     */
    private static final long serialVersionUID = 276197701640260133L;

    /**
     * Constructs a new unchecked exception
     */
    public BioSDKException() {
        super();
    }

    /**
     * Constructor
     *
     * @param errorCode
     *            the Error Code Corresponds to Particular Exception
     * @param errorMessage
     *            the Message providing the specific context of the error
     */
    public BioSDKException(String errorCode, String errorMessage) {
        super(errorCode, errorMessage);
    }

    /**
     * Constructor
     *
     * @param errorCode
     *            the Error Code Corresponds to Particular Exception
     * @param errorMessage
     *            the Message providing the specific context of the error
     * @param throwable
     *            the Cause of exception
     */
    public BioSDKException(String errorCode, String errorMessage, Throwable throwable) {
        super(errorCode, errorMessage, throwable);
    }
}
