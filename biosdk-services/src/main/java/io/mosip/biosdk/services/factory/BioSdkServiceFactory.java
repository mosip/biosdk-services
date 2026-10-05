package io.mosip.biosdk.services.factory;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.mosip.biosdk.services.exceptions.BioSDKException;
import io.mosip.biosdk.services.spi.BioSdkServiceProvider;
import io.mosip.biosdk.services.utils.ErrorCode;

/**
 * Factory class responsible for providing instances of
 * {@link BioSdkServiceProvider} based on version.
 * <p>
 * The {@code BioSdkServiceFactory} uses Spring's {@link Component} annotation
 * to be managed by the Spring container. It maintains a list of
 * {@link BioSdkServiceProvider}s injected via constructor to dynamically fetch
 * the appropriate service provider based on the specified version.
 * </p>
 *
 *
 * @since 1.0.0
 */
@Component
public class BioSdkServiceFactory {
	/**
	 * All Spring {@link BioSdkServiceProvider} beans. Spec {@code "1.0"} is
	 * {@link io.mosip.biosdk.services.impl.spec_1_0.BioSdkServiceProviderImpl_V_1_0}.
	 */
	private List<BioSdkServiceProvider> bioSdkServiceProviders;

	/**
	 * Constructs a new BioSdkServiceFactory with a list of BioSdkServiceProviders.
	 *
	 * @param bioSdkServiceProviders the list of BioSdkServiceProviders to be
	 *                               managed by the factory.
	 */
	@Autowired
	public BioSdkServiceFactory(List<BioSdkServiceProvider> bioSdkServiceProviders) {
		this.bioSdkServiceProviders = bioSdkServiceProviders;
	}

	/**
	 * Retrieves the BioSdkServiceProvider instance for the specified version.
	 *
	 * @param version the version of the BioSDK service provider to retrieve.
	 * @return the BioSdkServiceProvider instance corresponding to the given
	 *         version.
	 * @throws BioSDKException if no BioSdkServiceProvider is found for the
	 *                         specified version.
	 */
	public BioSdkServiceProvider getBioSdkServiceProvider(String version) {
		for (BioSdkServiceProvider provider : bioSdkServiceProviders) {
			if (provider.getSpecVersion().equals(version)) {
				return provider;
			}
		}
		throw new BioSDKException(ErrorCode.NO_PROVIDERS.getErrorCode(), ErrorCode.NO_PROVIDERS.getErrorMessage());
	}
}