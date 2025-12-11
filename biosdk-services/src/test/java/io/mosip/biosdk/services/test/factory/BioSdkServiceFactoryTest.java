package io.mosip.biosdk.services.test.factory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.Arrays;

import io.mosip.biosdk.services.factory.BioSdkServiceFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import io.mosip.biosdk.services.exceptions.BioSDKException;
import io.mosip.biosdk.services.spi.BioSdkServiceProvider;
import io.mosip.biosdk.services.utils.ErrorCode;

public class BioSdkServiceFactoryTest {

    @Mock
    private BioSdkServiceProvider providerV1;

    @Mock
    private BioSdkServiceProvider providerV2;

    private BioSdkServiceFactory factory;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        when(providerV1.getSpecVersion()).thenReturn("1.0");
        when(providerV2.getSpecVersion()).thenReturn("2.0");

        factory = new BioSdkServiceFactory(Arrays.asList(providerV1, providerV2));
    }

    @Test
    void testGetProviderSuccess() {
        BioSdkServiceProvider provider = factory.getBioSdkServiceProvider("1.0");
        assertEquals(providerV1, provider);
    }

    @Test
    void testGetProviderShouldThrowExceptionWhenNoMatch() {
        BioSDKException ex = assertThrows(BioSDKException.class,
                () -> factory.getBioSdkServiceProvider("3.0"));

        assertEquals(ErrorCode.NO_PROVIDERS.getErrorCode(), ex.getErrorCode());
    }
}

