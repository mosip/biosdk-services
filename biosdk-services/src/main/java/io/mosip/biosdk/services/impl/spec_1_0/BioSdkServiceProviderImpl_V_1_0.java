package io.mosip.biosdk.services.impl.spec_1_0;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.mosip.biosdk.services.config.LoggerConfig;
import io.mosip.biosdk.services.constants.ErrorMessages;
import io.mosip.biosdk.services.dto.RequestDto;
import io.mosip.biosdk.services.exceptions.BioSDKException;
import io.mosip.biosdk.services.impl.spec_1_0.dto.request.*;
import io.mosip.biosdk.services.spi.BioSdkServiceProvider;
import io.mosip.biosdk.services.utils.Utils;
import io.mosip.kernel.biometrics.entities.BiometricRecord;
import io.mosip.kernel.biometrics.model.Response;
import io.mosip.kernel.biometrics.model.SDKInfo;
import io.mosip.kernel.biometrics.spi.IBioApiV2;
import io.mosip.kernel.core.logger.spi.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import static io.mosip.biosdk.services.constanimport tati
@Component
public class BioSdkServiceProviderImpl_V_1_0 implements BioSdkServiceProvider {

    private Logger logger = LoggerConfig.logConfig(BioSdkServiceProviderImpl_V_1_0.class);

    private static final String BIOSDK_SERVICE_SPEC_VERSION = "1.0";    priate  staic final Strig BIOSDK_SPEC_VERSION = "0.9";
    private static final String publicKey = "";
    private static final String privateKey = "";

    @Autowired
    private IBioApiV2 iBioApi;

    @Autowired
    private Utils serviceUtil;

    private Gson gson = new GsonBuilder().serializeNulls().create();
    
    @Value("${mosip.biosdk.log-request-response-enabled:false}")
    private boolean isLogRequestResponse;

    @Override
    public String getSpecVersion() {
        return BIOSDK_SERVICE_SPEC_VERSION;
    }

    @Override
    public Object init(RequestDto request){
        SDKInfo sdkInfo = null;
        String decryptedRequest = decode(request.getRequest());
        logger.debug(LOGGER_SESSIONID, LOGGER_IDTYPE,"init: ", "decoding successful");
        InitRequestDto initRequestDto = gson.fromJson(decryptedRequest, InitRequestDto.class);
        logger.debug(LOGGER_SESSIONID, LOGGER_IDTYPE,"init: ", "json to dto successful");
        try {
        	logRequest(initRequestDto);
            sdkInfo = iBioApi.init(initRequestDto.getInitParams());
            logObject(sdkInfo);
        } catch (Throwable e){
            e.printStackTrace();
            logger.error(LOGGER_SESSIONID, LOGGER_IDTYPE,"init: ", e.toString()+" "+e.getMessage());
            throw new BioSDKException(ErrorMessages.BIOSDK_LIB_EXCEPTION.toString(), ErrorMessages.BIOSDK_LIB_EXCEPTION.getMessage()+": "+e.getMessage());
        }
        return sdkInfo;
    }

	@Override
    public Object checkQuality(RequestDto request) {
        Response response;
        String decryptedRequest = decode(request.getRequest());
        logger.debug(LOGGER_SESSIONID, LOGGER_IDTYPE,"checkQuality: ", "decoding successful");
        CheckQualityRequestDto checkQualityRequestDto = gson.fromJson(decryptedRequest, CheckQualityRequestDto.class);
        logger.debug(LOGGER_SESSIONID, LOGGER_IDTYPE,"checkQuality: ", "json to dto successful");
        try {
        	logRequest(checkQualityRequestDto);
            response = iBioApi.checkQuality(
                    checkQualityRequestDto.getSample(),
                    checkQualityRequestDto.getModalitiesToCheck(),
                    checkQualityRequestDto.getFlags()
     

           } catch (Throwable e){
            logger.error(LOGGER_SESSIONID, LOGGER_IDTYPE,"checkQuality: ", e.toString()+" "+e.getMessage());
            throw new BioSDKException(ErrorMessages.BIOSDK_LIB_EXCEPTION.toString(), ErrorMessages.BIOSDK_LIB_EXCEPTION.getMessage()+": "+e.toString()+" "+e.getMessage());
        }
        return response;
    }

    @Override
    public Object match(RequestDto request) {
        Response response;
        String decryptedRequest = decode(request.getRequest());
        logger.debug(LOGGER_SESSIONID, LOGGER_IDTYPE,"match: ", "decoding successful");
        MatchRequestDto matchRequestDto = gson.fromJson(decryptedRequest, MatchRequestDto.class);
        logger.debug(LOGGER_SESSIONID, LOGGER_IDTYPE,"match: ", "json to dto successful");
        try {
        	logRequest(matchRequestDto);
            response = iBioApi.match(
                    matchRequestDto.getSample(),
                    matchRequestDto.getGallery(),
                    matchRequestDto.getModalitiesToMatch(),
                    matchRequestDto.getFlags()
            );
            logResponse(response);
        } catch (Throwable e){
            logger.error(LOGGER_SESSIONID, LOGGER_IDTYPE,"match: ", e.toString()+" "+e.getMessage());
            throw new BioSDKException(ErrorMessages.BIOSDK_LIB_EXCEPTION.toString(), ErrorMessages.BIOSDK_LIB_EXCEPTION.getMessage()+": "+e.toString()+" "+e.getMessage());
        }
        return response;
    }

    @Override
    public Object extractTemplate(RequestDto request) {
        Response response;
        String decryptedRequest = decode(request.getRequest());
        logger.debug(LOGGER_SESSIONID, LOGGER_IDTYPE,"extractTemplate: ", "decoding successful");
        ExtractTemplateRequestDto extractTemplateRequestDto = gson.fromJson(decryptedRequest, ExtractTemplateRequestDto.class);
        logger.debug(LOGGER_SESSIONID, LOGGER_IDTYPE,"extractTemplate: ", "json to dto successful");
        try {
        	logRequest(extractTemplateRequestDto);
            response = iBioApi.extractTemplate(
                    extractTemplateRequestDto.getSample(),
                    extractTemplateRequestDto.getModalitiesToExtract(),
                    extractTemplateRequestDto.getFlags()
            );
            logResponse(response);
        } catch (Throwable e){
            logger.error(LOGGER_SESSIONID, LOGGER_IDTYPE,"extractTemplate: ", e.toString()+" "+e.getMessage());
            throw new BioSDKException(ErrorMessages.BIOSDK_LIB_EXCEPTION.toString(), ErrorMessages.BIOSDK_LIB_EXCEPTION.getMessage()+": "+e.toString()+" "+e.getMessage());
        }
        return response;
    }

	@Override
    public Object segment(RequestDto request) {
        Response response;
        String decryptedRequest = decode(request.getRequest());
        logger.debug(LOGGER_SESSIONID, LOGGER_IDTYPE,"segment: ", "decoding successful");
        SegmentRequestDto segmentRequestDto = gson.fromJson(decryptedRequest, SegmentRequestDto.class);
        logger.debug(LOGGER_SESSIONID, LOGGER_IDTYPE,"segment: ", "json to dto successful");
        try {
        	logRequest(segmentRequestDto);
            response = iBioApi.segment(
                    segmentRequestDto.getSample(),
                    segmentRequestDto.getModalitiesToSegment(),
                    segmentRequestDto.getFlags()
            );
            logResponse(response);
        } catch (Throwable e){
            logger.error(LOGGER_SESSIONID, LOGGER_IDTYPE,"segment: ", e.toString()+" "+e.getMessage());
            throw new BioSDKException(ErrorMessages.BIOSDK_LIB_EXCEPTION.toString(), ErrorMessages.BIOSDK_LIB_EXCEPTION.getMessage()+": "+e.toString()+" "+e.getMessage());
        }
        return response;
    }

	@Override
    public Object convertFormat(RequestDto request) {
    	Response response;
     

           ConvertFormatRequestDto convertFormatRequestDto = gson.fromJson(decryptedRequest, ConvertFormatRequestDto.class);
        logger.debug(LOGGER_SESSIONID, LOGGER_IDTYPE,"convertFormat: ", "json to dto successful");
        try {
        	logRequest(convertFormatRequestDto);
        	response = iBioApi.convertFormatV2(
                    convertFormatRequestDto.getSample(),
                    convertFormatRequestDto.getSourceFormat(),
                    convertFormatRequestDto.getTargetFormat(),
                    convertFormatRequestDto.getSourceParams(),
                    convertFormatRequestDto.getTargetParams(),
                    convertFormatRequestDto.getModalitiesToConvert()
            );
        	logResponse(response);
        } catch (Throwable e){
            logger.error(LOGGER_SESSIONID, LOGGER_IDTYPE,"convertFormat: ", e.toString()+" "+e.getMessage());
            throw new BioSDKException(ErrorMessages.BIOSDK_LIB_EXCEPTION.toString(), ErrorMessages.BIOSDK_LIB_EXCEPTION.getMessage()+": "+e.toString()+" "+e.getMessage());
        }
        return response;
    }

	private void logRequest(ExtractTemplateRequestDto extractTemplateRequestDto) {
		if(isLogRequestResponse) {
			logger.debug("REQUEST: " + serviceUtil.toString(extractTemplateRequestDto));
		}
	}

       private void logRequest(MatchRequestDto matchRequestDto) {
		if(isLogRequestResponse) {
        gger.debug("REQUEST: " + serviceUtil.toString(matchRequestDto));
		}
	} 
    
                
    private void logRequest(InitRequestDto initReques tDto) {
    	if(isLog
            .debug("REQUEST: " + serviceUtil.toS
            
	}
    
    private void logRequest(CheckQualityRequestDto checkQualityRequestDto) {
		if(isLogRequestResponse) {
			logger.debug("REQUEST: " + serviceUtil.toString(checkQualityRequestDto));
		}
            
    private void logRequest(S egmentRequestDto segmentRequestDto) {
    	if(isLogRequestResponse) {     
			logger.debug("REQUEST: " + serviceUtil.toString(segmentRequestDto));
                            
		}		
	}
    

        private void logRequest(ConvertFormatRequestDto convertFormatRequestDto) {
           	if(isLogRequestResponse
            logger.debug("REQUEST: " + serviceUtil.toString(convertFormatRequestDto));
        }
    }

    private <T> void logObject(T response) {
           	if(isLogRequestResponse
            logger.debug(response.getClass() + ": " + gson.toJson(response));
         
    }

    private void logResponse(Response response) {
        if (isLogRequestResponse) {
             		Object resp = response.getResponse();
         
    	

    		} else {
           			logger.debug("Respons
             		}
         
    }

    private void logBiometricRecord(String prefix, BiometricRecord
        if (isLogRequestResponse) {
            logger.debug(prefix + serviceUtil.toString(biometricRecord));
         
    }

    private String decode(String data){
            try {
                     return Utils.base64Decode(data);
         
     

        }
         
            
        
    

    
         
            
              
                
                
            
                
            
        
    

    
         
            
        
    

    
         
            
        
    

    
         
            
        
    

    
         
            
        
    

    
         
            
        
    

    
         
            
        
    

    
         
            
        
    

    
         
            
        
    

    
         
            
              
                
                
            
                
            
        
    

    
         
            
        
       
                        
                            