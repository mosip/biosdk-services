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
    void testValidateBioSdkLib_WithValidClass() {
        // Simulate a valid class name
        when(env.getProperty("biosdk_bioapi_impl")).thenReturn("java.lang.String");
        assertDoesNotThrow(() -> config.validateBioSdkLib());
    }

    @Test
    void testValidateBioSdkLib_WithBlankClass() {
        // Simulate blank class name
        when(env.getProperty("biosdk_bioapi_impl")).thenReturn("");
        assertDoesNotThrow(() -> config.validateBioSdkLib());

        when(env.getProperty("biosdk_bioapi_impl")).thenReturn("   ");
        assertDoesNotThrow(() -> config.validateBioSdkLib());
    }

    @Test
    void testValidateBioSdkLib_WithNullClass() {
        when(env.getProperty("biosdk_bioapi_impl")).thenReturn(null);
        assertDoesNotThrow(() -> config.validateBioSdkLib());
    }

    @Test
    void testIBioApi_NoClassProvided() {
        when(env.getProperty("biosdk_bioapi_impl")).thenReturn(null);

        BioSDKException exception = assertThrows(BioSDKException.class, () -> config.iBioApi());
        assertTrue(exception.getMessage().contains("NO_BIOSDK_PROVIDER_FOUND"));
    }

    @Test
    void testIBioApi_ClassNotFound() {
        when(env.getProperty("biosdk_bioapi_impl")).thenReturn("non.existing.ClassName");

        assertThrows(ClassNotFoundException.class, () -> config.iBioApi());
    }

}

