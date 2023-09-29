package io.mosip.biosdk.services.config;

import io.mosip.kernel.biometrics.spi.IBioApiV2;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.env.Environment;

import javax.annotation.PostConstruct;

@Configuration
public class BioSdkLibConfig {
    private static final Logger logger = LoggerFactory.getLogger(BioSdkLibConfig.class);
    @Autowired
    private Environment env;

    public BioSdkLibConfig() {
    }

    @PostConstruct
    public void validateBioSdkLib() throws ClassNotFoundException {
    	String sdkClass = this.env.getProperty("biosdk_bioapi_impl");
		logger.info("Biosdk class: " + sdkClass);
        if (StringUtils.isNotBlank(sdkClass)) {
            logger.debug("validating Bio SDK Class is present or not");
            Class.forName(this.env.getProperty("biosdk_bioapi_impl"));
        }

        logger.debug("validateBioSdkLib: Bio SDK Class is not provided");
    }

    @Bean
    @Lazy
    public IBioApiV2 iBioApi() throws ClassNotFoundException, InstantiationException, IllegalAccessException {
    	String sdkClass = this.env.getProperty("biosdk_bioapi_impl");
		logger.info("Biosdk class: " + sdkClass);
    	if (StringUtils.isNotBlank(sdkClass)) {
            logger.debug("instance of Bio SDK is created");
            return (IBioApiV2)Class.forName(sdkClass).newInstance();
        } else {
            logger.debug("no Bio SDK is provided");
            throw new RuntimeException("No Bio SDK is provided");
        }
    }
}
