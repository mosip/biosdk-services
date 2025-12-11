package io.mosip.biosdk.services.test.exceptions;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import io.mosip.biosdk.services.exceptions.BioSDKException;

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

