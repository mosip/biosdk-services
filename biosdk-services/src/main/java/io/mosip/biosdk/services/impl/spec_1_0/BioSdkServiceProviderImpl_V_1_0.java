package io.mosip.biosdk.services.impl.spec_1_0;

import static io.mosip.biosdk.services.constants.AppConstants.LOGGER_IDTYPE;
import static io.mosip.biosdk.services.constants.AppConstants.LOGGER_SESSIONID;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import io.mosip.biosdk.services.config.LoggerConfig;
import io.mosip.biosdk.services.constants.ErrorMessages;
import io.mosip.biosdk.services.constants.ResponseStatus;
import io.mosip.biosdk.services.dto.RequestDto;
import io.mosip.biosdk.services.exceptions.BioSDKException;
import io.mosip.biosdk.services.impl.spec_1_0.dto.request.CheckQualityRequestDto;
import io.mosip.biosdk.services.impl.spec_1_0.dto.request.ConvertFormatRequestDto;
import io.mosip.biosdk.services.impl.spec_1_0.dto.request.ExtractTemplateRequestDto;
import io.mosip.biosdk.services.impl.spec_1_0.dto.request.InitRequestDto;
import io.mosip.biosdk.services.impl.spec_1_0.dto.request.MatchRequestDto;
import io.mosip.biosdk.services.impl.spec_1_0.dto.request.SegmentRequestDto;
import io.mosip.biosdk.services.spi.BioSdkServiceProvider;
import io.mosip.biosdk.services.utils.Utils;
import io.mosip.kernel.biometrics.entities.BIR;
import io.mosip.kernel.biometrics.entities.BiometricRecord;
import io.mosip.kernel.biometrics.model.Response;
import io.mosip.kernel.biometrics.model.SDKInfo;
import io.mosip.kernel.biometrics.spi.IBioApi;
import io.mosip.kernel.core.logger.spi.Logger;

@Component
public class BioSdkServiceProviderImpl_V_1_0 implements BioSdkServiceProvider {

    private Logger logger = LoggerConfig.logConfig(BioSdkServiceProviderImpl_V_1_0.class);

    private static final String BIOSDK_SERVICE_SPEC_VERSION = "1.0";
    private static final String BIOSDK_SPEC_VERSION = "0.9";
    private static final String publicKey = "";
    private static final String privateKey = "";

    @Autowired
    private IBioApi iBioApi;

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
            logResponse(sdkInfo);
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
            );
            logResponse(response);
        } catch (Throwable e){
            e.printStackTrace();
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
            e.printStackTrace();
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
            boolean isValid = true;
            //Validation
            if (response != null && response.getResponse() != null)
            {
        		BiometricRecord biometricRecord = (BiometricRecord)response.getResponse();
                //Should have Valid BIR Segments
        		List<BIR> segments = biometricRecord.getSegments();
        		if (segments == null || segments.size() == 0)
        		{
        			isValid = false;
        		}
            }
            else
            {
    			isValid = false;
            }
            if (!isValid)
            {
    	        logger.debug(LOGGER_SESSIONID, LOGGER_IDTYPE, "extractTemplate: Response Code  :: ", response.getStatusCode());        			
    	        logger.debug(LOGGER_SESSIONID, LOGGER_IDTYPE, "extractTemplate: Response Status :: ", response.getStatusMessage());        			
    	        logger.debug(LOGGER_SESSIONID, LOGGER_IDTYPE, "extractTemplate: Request :: ", request.getRequest());
    	        logger.debug(LOGGER_SESSIONID, LOGGER_IDTYPE, "extractTemplate: Response :: ", response.getResponse());
				response.setStatusCode(ResponseStatus.UNKNOWN_ERROR.getStatusCode());
	            throw new BioSDKException(ErrorMessages.UNCHECKED_EXCEPTION.toString(), ErrorMessages.UNCHECKED_EXCEPTION.getMessage());
            } 
            
            logResponse(response);
        } catch (Throwable e){
            e.printStackTrace();
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
            boolean isValid = true;
            //Validation
            if (response != null && response.getResponse() != null)
            {
        		BiometricRecord biometricRecord = (BiometricRecord)response.getResponse();
                //Should have Valid BIR Segments
        		List<BIR> segments = biometricRecord.getSegments();
        		if (segments == null || segments.size() == 0)
        		{
        			isValid = false;
        		}
            }
            else
            {
    			isValid = false;
            }
            if (!isValid)
            {
    	        logger.debug(LOGGER_SESSIONID, LOGGER_IDTYPE, "extractTemplate: Response Code  :: ", response.getStatusCode());        			
    	        logger.debug(LOGGER_SESSIONID, LOGGER_IDTYPE, "extractTemplate: Response Status :: ", response.getStatusMessage());        			
    	        logger.debug(LOGGER_SESSIONID, LOGGER_IDTYPE, "extractTemplate: Request :: ", request.getRequest());
    	        logger.debug(LOGGER_SESSIONID, LOGGER_IDTYPE, "extractTemplate: Response :: ", response.getResponse());
				response.setStatusCode(ResponseStatus.UNKNOWN_ERROR.getStatusCode());
            } 
            logResponse(response);
        } catch (Throwable e){
            e.printStackTrace();
            logger.error(LOGGER_SESSIONID, LOGGER_IDTYPE,"segment: ", e.toString()+" "+e.getMessage());
            throw new BioSDKException(ErrorMessages.BIOSDK_LIB_EXCEPTION.toString(), ErrorMessages.BIOSDK_LIB_EXCEPTION.getMessage()+": "+e.toString()+" "+e.getMessage());
        }
        return response;
    }

	@Override
    public Object convertFormat(RequestDto request) {
        BiometricRecord biometricRecord;
        String decryptedRequest = decode(request.getRequest());
        logger.debug(LOGGER_SESSIONID, LOGGER_IDTYPE,"convertFormat: ", "decoding successful");
        ConvertFormatRequestDto convertFormatRequestDto = gson.fromJson(decryptedRequest, ConvertFormatRequestDto.class);
        logger.debug(LOGGER_SESSIONID, LOGGER_IDTYPE,"convertFormat: ", "json to dto successful");
        try {
        	logRequest(convertFormatRequestDto);
            biometricRecord = iBioApi.convertFormat(
                    convertFormatRequestDto.getSample(),
                    convertFormatRequestDto.getSourceFormat(),
                    convertFormatRequestDto.getTargetFormat(),
                    convertFormatRequestDto.getSourceParams(),
                    convertFormatRequestDto.getTargetParams(),
                    convertFormatRequestDto.getModalitiesToConvert()
            );
            logBiometricRecord(biometricRecord);
        } catch (Throwable e){
            e.printStackTrace();
            logger.error(LOGGER_SESSIONID, LOGGER_IDTYPE,"convertFormat: ", e.toString()+" "+e.getMessage());
            throw new BioSDKException(ErrorMessages.BIOSDK_LIB_EXCEPTION.toString(), ErrorMessages.BIOSDK_LIB_EXCEPTION.getMessage()+": "+e.toString()+" "+e.getMessage());
        }
        return biometricRecord;
    }

	private void logRequest(ExtractTemplateRequestDto extractTemplateRequestDto) {
		if(isLogRequestResponse) {
			logger.debug(Utils.toString(extractTemplateRequestDto));
		}
	}
    
    private void logRequest(MatchRequestDto matchRequestDto) {
		if(isLogRequestResponse) {
			logger.debug(Utils.toString(matchRequestDto));
		}
	}
    
    private void logRequest(InitRequestDto initRequestDto) {
    	if(isLogRequestResponse) {
			logger.debug(Utils.toString(initRequestDto));
		}		
	}
    
    private void logRequest(CheckQualityRequestDto checkQualityRequestDto) {
		if(isLogRequestResponse) {
			logger.debug(Utils.toString(checkQualityRequestDto));
		}
	}
    
    private void logRequest(SegmentRequestDto segmentRequestDto) {
    	if(isLogRequestResponse) {
			logger.debug(Utils.toString(segmentRequestDto));
		}		
	}
    
    private void logRequest(ConvertFormatRequestDto convertFormatRequestDto) {
    	if(isLogRequestResponse) {
			logger.debug(Utils.toString(convertFormatRequestDto));
		}			
	}
    
    private <T> void logResponse(T response) {
    	if(isLogRequestResponse) {
			logger.debug(gson.toJson(response));
    	}
	}
    
    private void logBiometricRecord(BiometricRecord biometricRecord) {
    	if(isLogRequestResponse) {
			logger.debug(Utils.toString(biometricRecord));
    	}
	}

    private String decode(String data){
        try {
            return Utils.base64Decode(data);
        } catch (RuntimeException e){
            e.printStackTrace();
            logger.error(LOGGER_SESSIONID, LOGGER_IDTYPE,ErrorMessages.INVALID_REQUEST_BODY.toString(), e.toString()+" "+e.getMessage());
            throw new BioSDKException(ErrorMessages.INVALID_REQUEST_BODY.toString(), ErrorMessages.INVALID_REQUEST_BODY.getMessage()+": "+e.toString()+" "+e.getMessage());
        }
    }
}
