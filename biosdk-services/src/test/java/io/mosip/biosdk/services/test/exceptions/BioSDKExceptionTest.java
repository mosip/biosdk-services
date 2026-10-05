/*
 * Copyright (c) 2018-2026 MOSIP.
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package io.mosip.biosdk.services.test.exceptions;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import io.mosip.biosdk.services.exceptions.BioSDKException;

/**
 * @copyright 2018-2026 MOSIP
 */
class BioSDKExceptionTest {

    @Test
    void testDefaultConstructor() {
        BioSDKException ex = new BioSDKException();
        assertNotNull(ex);
    }

    @Test
    void testConstructorWithErrorCodeAndMessage() {
        BioSDKException ex = new BioSDKException("ERROR_CODE", "Error message");
        assertNotNull(ex);
        assertEquals("ERROR_CODE", ex.getErrorCode());
    }

    @Test
    void testConstructorWithCause() {
        Throwable cause = new RuntimeException("cause");
        BioSDKException ex = new BioSDKException("ERROR_CODE", "Error message", cause);
        assertNotNull(ex);
        assertEquals(cause, ex.getCause());
    }
}

