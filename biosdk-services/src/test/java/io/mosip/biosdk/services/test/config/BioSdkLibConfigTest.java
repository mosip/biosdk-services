/*
 * Copyright (c) 2018-2026 MOSIP.
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package io.mosip.biosdk.services.test.config;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import io.mosip.biosdk.services.config.BioSdkLibConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.env.Environment;

import io.mosip.biosdk.services.exceptions.BioSDKException;

/**
 * @copyright 2018-2026 MOSIP
 */
class BioSdkLibConfigTest {

    @Mock
    private Environment env;

    private BioSdkLibConfig config;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        config = new BioSdkLibConfig(env);
    }

    @Test
    void testValidateBioSdkLibWithValidClass() {
        // Simulate a valid class name
        when(env.getProperty("biosdk_bioapi_impl")).thenReturn("java.lang.String");
        assertDoesNotThrow(() -> config.validateBioSdkLib());
    }

    @Test
    void testValidateBioSdkLibWithBlankClass() {
        // Simulate blank class name
        when(env.getProperty("biosdk_bioapi_impl")).thenReturn("");
        assertDoesNotThrow(() -> config.validateBioSdkLib());

        when(env.getProperty("biosdk_bioapi_impl")).thenReturn("   ");
        assertDoesNotThrow(() -> config.validateBioSdkLib());
    }

    @Test
    void testValidateBioSdkLibWithNullClass() {
        when(env.getProperty("biosdk_bioapi_impl")).thenReturn(null);
        assertDoesNotThrow(() -> config.validateBioSdkLib());
    }

    @Test
    void testIBioApiNoClassProvided() {
        when(env.getProperty("biosdk_bioapi_impl")).thenReturn(null);

        BioSDKException exception = assertThrows(BioSDKException.class, () -> config.iBioApi());
        assertTrue(exception.getMessage().contains("NO_BIOSDK_PROVIDER_FOUND"));
    }

    @Test
    void testIBioApiClassNotFound() {
        when(env.getProperty("biosdk_bioapi_impl")).thenReturn("non.existing.ClassName");

        assertThrows(ClassNotFoundException.class, () -> config.iBioApi());
    }

}

