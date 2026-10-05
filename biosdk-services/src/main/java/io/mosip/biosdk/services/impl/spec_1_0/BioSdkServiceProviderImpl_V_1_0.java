/*
 * Copyright (c) 2018-2026 MOSIP.
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package io.mosip.biosdk.services.impl.spec_1_0;

import static io.mosip.biosdk.services.constants.AppConstants.LOGGER_IDTYPE;
import static io.mosip.biosdk.services.constants.AppConstants.LOGGER_SESSIONID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import io.mosip.biosdk.services.config.LoggerConfig;
import io.mosip.biosdk.services.constants.ErrorMessages;
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
import io.mosip.kernel.biometrics.entities.BiometricRecord;
import io.mosip.kernel.biometrics.model.Response;
import io.mosip.kernel.biometrics.model.SDKInfo;
import io.mosip.kernel.biometrics.spi.IBioApiV2;
import io.mosip.kernel.core.logger.spi.Logger;

/**
 * This class {@code BioSdkServiceProviderImpl_V_1_0} implements the
 * {@code BioSdkServiceProvider} interface, providing functionality to interact
 * with the biometric SDK API version 1.0.
 * <p>
 * It supports operations such as initialization, quality checking, template
 * extraction, matching, segmentation, and format conversion using the
 * {@code IBioApiV2} interface. The class handles decoding of requests, logging,
 * and exception handling for biometric operations.
 * <p>
 * The class uses Gson for JSON serialization and deserialization, and
 * integrates with {@code Utils} for utility functions. It logs request and
 * response details based on configuration, facilitating debugging and
 * traceability of biometric operations.
 *
 * @see BioSdkServiceProvider
 * @see IBioApiV2
 * @see Gson
 * @see Utils
 * @see InitRequestDto
 * @see CheckQualityRequestDto
 * @see ExtractTemplateRequestDto
 * @see MatchRequestDto
 * @see SegmentRequestDto
 * @see ConvertFormatRequestDto
 * @copyright 2018-2026 MOSIP
 */
@Component
@SuppressWarnings({ "java:S101" })
public class BioSdkServiceProviderImpl_V_1_0 implements BioSdkServiceProvider {

	/**
	 * MOSIP kernel logger for spec {@code "1.0"} operations. Shared across calls.
	 */
	private static final Logger logger = LoggerConfig.logConfig(BioSdkServiceProviderImpl_V_1_0.class);

	/**
	 * Spec version this provider reports to {@link io.mosip.biosdk.services.factory.BioSdkServiceFactory}.
	 */
	private static final String BIOSDK_SERVICE_SPEC_VERSION = "1.0";

	/** Log id for {@link #init(RequestDto)}. */
	private static final String INIT = "init";

	/** Log id for {@link #checkQuality(RequestDto)}. */
	private static final String CHECK_QUALITY = "checkQuality";

	/** Log id for {@link #extractTemplate(RequestDto)}. */
	private static final String EXTRACT_TEMPLATE = "extractTemplate";

	/** Log id for {@link #match(RequestDto)}. */
	private static final String MATCH = "match";

	/** Log id for {@link #segment(RequestDto)}. */
	private static final String SEGMENT = "segment";

	/** Log id for {@link #convertFormat(RequestDto)}. */
	private static final String CONVERT_FORMAT = "convertFormat";

	/** Debug message after Base64 {@code request} decode succeeds. */
	private static final String DECODE_SUCCESS = "decoding successful";

	/** Debug message after Gson maps the decoded JSON onto a typed DTO. */
	private static final String JSON_TO_DTO_SUCCESS = "json to dto successful";

	/**
	 * Vendor or mock {@link IBioApiV2} loaded by {@code biosdk_bioapi_impl}.
	 */
	private IBioApiV2 iBioApi;

	/**
	 * Helpers for Base64 decode logging of biometric payloads (hashes, not raw BDB).
	 */
	private Utils utils;

	/**
	 * JSON mapper for inner request DTOs. Null fields are serialized.
	 */
	private Gson gson;

	/**
	 * When {@code true}, request/response DTOs are written at DEBUG. Default
	 * {@code false} via {@code mosip.biosdk.log-request-response-enabled}.
	 */
	@Value("${mosip.biosdk.log-request-response-enabled:false}")
	private boolean isLogRequestResponse;

	/**
	 * Constructor for BioSdkServiceProviderImpl_V_1_0.
	 * 
	 * @param iBioApi The Biometric API (version 2) dependency.
	 * @param utils   The utility functions dependency.
	 */
	@Autowired
	public BioSdkServiceProviderImpl_V_1_0(IBioApiV2 iBioApi, Utils utils) {
		this.iBioApi = iBioApi;
		this.utils = utils;
		gson = new GsonBuilder().serializeNulls().create();
	}

	/**
	 * Retrieves the version of the BioSDK service specification this implementation
	 * adheres to.
	 * 
	 * @return The BioSDK service specification version.
	 */
	@Override
	public String getSpecVersion() {
		return BIOSDK_SERVICE_SPEC_VERSION;
	}

	/**
	 * Initializes the biometric SDK with the provided initialization parameters.
	 * <p>
	 * This method decodes the encrypted request, converts it to an
	 * {@code InitRequestDto} object using Gson, and invokes the {@code init} method
	 * of {@code IBioApiV2} to initialize the SDK. It logs the request details and
	 * the SDKInfo response object.
	 * <p>
	 * If any exception occurs during the initialization process, it logs the error
	 * and throws a {@code BioSDKException} with an appropriate error message.
	 *
	 * @param request The {@code RequestDto} containing the encrypted initialization
	 *                request.
	 * @return An {@code SDKInfo} object containing SDK initialization information.
	 * @throws BioSDKException If an error occurs during SDK initialization.
	 * 
	 * @see IBioApiV2
	 * @see InitRequestDto
	 * @see SDKInfo
	 * @see RequestDto
	 * @see Gson
	 */
	@Override
	public Object init(RequestDto request) {
		SDKInfo sdkInfo = null;
		String decryptedRequest = decode(request.getRequest());
		logger.debug(LOGGER_SESSIONID, LOGGER_IDTYPE, INIT, DECODE_SUCCESS);
		InitRequestDto initRequestDto = gson.fromJson(decryptedRequest, InitRequestDto.class);
		logger.debug(LOGGER_SESSIONID, LOGGER_IDTYPE, INIT, JSON_TO_DTO_SUCCESS);
		try {
			logRequest(initRequestDto);
			sdkInfo = iBioApi.init(initRequestDto.getInitParams());
			logObject(sdkInfo);
		} catch (Exception e) {
			logger.error(LOGGER_SESSIONID, LOGGER_IDTYPE, INIT, e);
			throw new BioSDKException(ErrorMessages.BIOSDK_LIB_EXCEPTION.toString(),
					ErrorMessages.BIOSDK_LIB_EXCEPTION.getMessage() + ": " + e.getMessage());
		}
		return sdkInfo;
	}

	/**
	 * Checks the quality of biometric samples based on the provided request.
	 * <p>
	 * This method decodes the encrypted request, converts it to a
	 * {@code CheckQualityRequestDto} object using Gson, and invokes the
	 * {@code checkQuality} method of {@code IBioApiV2} to assess the quality of
	 * biometric samples. It logs the request details and the response object.
	 * <p>
	 * If any exception occurs during the quality check process, it logs the error
	 * and throws a {@code BioSDKException} with an appropriate error message.
	 *
	 * @param request The {@code RequestDto} containing the encrypted quality check
	 *                request.
	 * @return A {@code Response<?>} object representing the result of the quality
	 *         check operation.
	 * @throws BioSDKException If an error occurs during the quality check
	 *                         operation.
	 * 
	 * @see IBioApiV2
	 * @see CheckQualityRequestDto
	 * @see Response
	 * @see RequestDto
	 * @see Gson
	 */
	@Override
	public Object checkQuality(RequestDto request) {
		Response<?> response;
		String decryptedRequest = decode(request.getRequest());
		logger.debug(LOGGER_SESSIONID, LOGGER_IDTYPE, CHECK_QUALITY, DECODE_SUCCESS);
		CheckQualityRequestDto checkQualityRequestDto = gson.fromJson(decryptedRequest, CheckQualityRequestDto.class);
		logger.debug(LOGGER_SESSIONID, LOGGER_IDTYPE, CHECK_QUALITY, JSON_TO_DTO_SUCCESS);
		try {
			logRequest(checkQualityRequestDto);
			response = iBioApi.checkQuality(checkQualityRequestDto.getSample(),
					checkQualityRequestDto.getModalitiesToCheck(), checkQualityRequestDto.getFlags());
			logResponse(response);
		} catch (Exception e) {
			logger.error(LOGGER_SESSIONID, LOGGER_IDTYPE, CHECK_QUALITY, e);
			throw new BioSDKException(ErrorMessages.BIOSDK_LIB_EXCEPTION.toString(),
					ErrorMessages.BIOSDK_LIB_EXCEPTION.getMessage() + ": " + e.toString() + " " + e.getMessage());
		}
		return response;
	}

	/**
	 * Matches biometric samples against a gallery using the provided request.
	 * <p>
	 * This method decodes the encrypted request, converts it to a
	 * {@code MatchRequestDto} object using Gson, and invokes the {@code match}
	 * method of {@code IBioApiV2} to match biometric samples against a gallery. It
	 * logs the request details and the response object.
	 * <p>
	 * If any exception occurs during the matching process, it logs the error and
	 * throws a {@code BioSDKException} with an appropriate error message.
	 *
	 * @param request The {@code RequestDto} containing the encrypted match request.
	 * @return A {@code Response<?>} object representing the result of the matching
	 *         operation.
	 * @throws BioSDKException If an error occurs during the matching operation.
	 * 
	 * @see IBioApiV2
	 * @see MatchRequestDto
	 * @see Response
	 * @see RequestDto
	 * @see Gson
	 */
	@Override
	public Object match(RequestDto request) {
		Response<?> response;
		String decryptedRequest = decode(request.getRequest());
		logger.debug(LOGGER_SESSIONID, LOGGER_IDTYPE, MATCH, DECODE_SUCCESS);
		MatchRequestDto matchRequestDto = gson.fromJson(decryptedRequest, MatchRequestDto.class);
		logger.debug(LOGGER_SESSIONID, LOGGER_IDTYPE, MATCH, JSON_TO_DTO_SUCCESS);
		try {
			logRequest(matchRequestDto);
			response = iBioApi.match(matchRequestDto.getSample(), matchRequestDto.getGallery(),
					matchRequestDto.getModalitiesToMatch(), matchRequestDto.getFlags());
			logResponse(response);
		} catch (Exception e) {
			logger.error(LOGGER_SESSIONID, LOGGER_IDTYPE, MATCH, e);
			throw new BioSDKException(ErrorMessages.BIOSDK_LIB_EXCEPTION.toString(),
					ErrorMessages.BIOSDK_LIB_EXCEPTION.getMessage() + ": " + e.toString() + " " + e.getMessage());
		}
		return response;
	}

	/**
	 * Extracts biometric templates from samples based on the provided request.
	 * <p>
	 * This method decodes the encrypted request, converts it to an
	 * {@code ExtractTemplateRequestDto} object using Gson, and invokes the
	 * {@code extractTemplate} method of {@code IBioApiV2} to extract biometric
	 * templates from samples. It logs the request details and the response object.
	 * <p>
	 * If any exception occurs during the template extraction process, it logs the
	 * error and throws a {@code BioSDKException} with an appropriate error message.
	 *
	 * @param request The {@code RequestDto} containing the encrypted template
	 *                extraction request.
	 * @return A {@code Response<?>} object representing the result of the template
	 *         extraction operation.
	 * @throws BioSDKException If an error occurs during the template extraction
	 *                         operation.
	 * 
	 * @see IBioApiV2
	 * @see ExtractTemplateRequestDto
	 * @see Response
	 * @see RequestDto
	 * @see Gson
	 */
	@Override
	public Object extractTemplate(RequestDto request) {
		Response<?> response;
		String decryptedRequest = decode(request.getRequest());
		logger.debug(LOGGER_SESSIONID, LOGGER_IDTYPE, EXTRACT_TEMPLATE, DECODE_SUCCESS);
		ExtractTemplateRequestDto extractTemplateRequestDto = gson.fromJson(decryptedRequest,
				ExtractTemplateRequestDto.class);
		logger.debug(LOGGER_SESSIONID, LOGGER_IDTYPE, EXTRACT_TEMPLATE, JSON_TO_DTO_SUCCESS);
		try {
			logRequest(extractTemplateRequestDto);
			response = iBioApi.extractTemplate(extractTemplateRequestDto.getSample(),
					extractTemplateRequestDto.getModalitiesToExtract(), extractTemplateRequestDto.getFlags());
			logResponse(response);
		} catch (Exception e) {
			logger.error(LOGGER_SESSIONID, LOGGER_IDTYPE, EXTRACT_TEMPLATE, e);
			throw new BioSDKException(ErrorMessages.BIOSDK_LIB_EXCEPTION.toString(),
					ErrorMessages.BIOSDK_LIB_EXCEPTION.getMessage() + ": " + e.toString() + " " + e.getMessage());
		}
		return response;
	}

	/**
	 * Segments biometric samples into modalities based on the provided request.
	 * <p>
	 * This method decodes the encrypted request, converts it to a
	 * {@code SegmentRequestDto} object using Gson, and invokes the {@code segment}
	 * method of {@code IBioApiV2} to segment biometric samples into modalities. It
	 * logs the request details and the response object.
	 * <p>
	 * If any exception occurs during the segmentation process, it logs the error
	 * and throws a {@code BioSDKException} with an appropriate error message.
	 *
	 * @param request The {@code RequestDto} containing the encrypted segmentation
	 *                request.
	 * @return A {@code Response<?>} object representing the result of the
	 *         segmentation operation.
	 * @throws BioSDKException If an error occurs during the segmentation operation.
	 * 
	 * @see IBioApiV2
	 * @see SegmentRequestDto
	 * @see Response
	 * @see RequestDto
	 * @see Gson
	 */
	@Override
	public Object segment(RequestDto request) {
		Response<?> response;
		String decryptedRequest = decode(request.getRequest());
		logger.debug(LOGGER_SESSIONID, LOGGER_IDTYPE, SEGMENT, DECODE_SUCCESS);
		SegmentRequestDto segmentRequestDto = gson.fromJson(decryptedRequest, SegmentRequestDto.class);
		logger.debug(LOGGER_SESSIONID, LOGGER_IDTYPE, SEGMENT, JSON_TO_DTO_SUCCESS);
		try {
			logRequest(segmentRequestDto);
			response = iBioApi.segment(segmentRequestDto.getSample(), segmentRequestDto.getModalitiesToSegment(),
					segmentRequestDto.getFlags());
			logResponse(response);
		} catch (Exception e) {
			logger.error(LOGGER_SESSIONID, LOGGER_IDTYPE, SEGMENT, e);
			throw new BioSDKException(ErrorMessages.BIOSDK_LIB_EXCEPTION.toString(),
					ErrorMessages.BIOSDK_LIB_EXCEPTION.getMessage() + ": " + e.toString() + " " + e.getMessage());
		}
		return response;
	}

	/**
	 * Converts biometric data from one format to another based on the provided
	 * request.
	 * <p>
	 * This method decodes the encrypted request, converts it to a
	 * {@code ConvertFormatRequestDto} object using Gson, and invokes the
	 * {@code convertFormatV2} method of {@code IBioApiV2} to convert biometric data
	 * from the source format to the target format. It logs the request details and
	 * the response object.
	 * <p>
	 * If any exception occurs during the conversion process, it logs the error and
	 * throws a {@code BioSDKException} with an appropriate error message.
	 *
	 * @param request The {@code RequestDto} containing the encrypted conversion
	 *                request.
	 * @return A {@code Response<?>} object representing the result of the
	 *         conversion operation.
	 * @throws BioSDKException If an error occurs during the conversion operation.
	 * 
	 * @see IBioApiV2
	 * @see ConvertFormatRequestDto
	 * @see Response
	 * @see RequestDto
	 * @see Gson
	 */
	@Override
	public Object convertFormat(RequestDto request) {
		Response<?> response;
		String decryptedRequest = decode(request.getRequest());
		logger.debug(LOGGER_SESSIONID, LOGGER_IDTYPE, CONVERT_FORMAT, DECODE_SUCCESS);
		ConvertFormatRequestDto convertFormatRequestDto = gson.fromJson(decryptedRequest,
				ConvertFormatRequestDto.class);
		logger.debug(LOGGER_SESSIONID, LOGGER_IDTYPE, CONVERT_FORMAT, JSON_TO_DTO_SUCCESS);
		try {
			logRequest(convertFormatRequestDto);
			response = iBioApi.convertFormatV2(convertFormatRequestDto.getSample(),
					convertFormatRequestDto.getSourceFormat(), convertFormatRequestDto.getTargetFormat(),
					convertFormatRequestDto.getSourceParams(), convertFormatRequestDto.getTargetParams(),
					convertFormatRequestDto.getModalitiesToConvert());
			logResponse(response);
		} catch (Exception e) {
			logger.error(LOGGER_SESSIONID, LOGGER_IDTYPE, CONVERT_FORMAT, e);
			throw new BioSDKException(ErrorMessages.BIOSDK_LIB_EXCEPTION.toString(),
					ErrorMessages.BIOSDK_LIB_EXCEPTION.getMessage() + ": " + e.toString() + " " + e.getMessage());
		}
		return response;
	}

	/**
	 * Logs the details of an ExtractTemplateRequestDto if logging of request and
	 * response is enabled.
	 * 
	 * @param extractTemplateRequestDto The ExtractTemplateRequestDto object to log.
	 */
	private void logRequest(ExtractTemplateRequestDto extractTemplateRequestDto) {
		if (isLogRequestResponse) {
			logger.debug(LOGGER_SESSIONID, LOGGER_IDTYPE, "REQUEST::ExtractTemplateRequestDto",
					utils.toString(extractTemplateRequestDto));
		}
	}

	/**
	 * Logs the details of a MatchRequestDto if logging of request and response is
	 * enabled.
	 * 
	 * @param matchRequestDto The MatchRequestDto object to log.
	 */
	private void logRequest(MatchRequestDto matchRequestDto) {
		if (isLogRequestResponse) {
			logger.debug(LOGGER_SESSIONID, LOGGER_IDTYPE, "REQUEST:: MatchRequestDto", utils.toString(matchRequestDto));
		}
	}

	/**
	 * Logs the details of an InitRequestDto if logging of request and response is
	 * enabled.
	 * 
	 * @param initRequestDto The InitRequestDto object to log.
	 */
	private void logRequest(InitRequestDto initRequestDto) {
		if (isLogRequestResponse) {
			logger.debug(LOGGER_SESSIONID, LOGGER_IDTYPE, "REQUEST:: InitRequestDto", utils.toString(initRequestDto));
		}
	}

	/**
	 * Logs the details of a CheckQualityRequestDto if logging of request and
	 * response is enabled.
	 * 
	 * @param checkQualityRequestDto The CheckQualityRequestDto object to log.
	 */
	private void logRequest(CheckQualityRequestDto checkQualityRequestDto) {
		if (isLogRequestResponse) {
			logger.debug(LOGGER_SESSIONID, LOGGER_IDTYPE, "REQUEST:: CheckQualityRequestDto",
					utils.toString(checkQualityRequestDto));
		}
	}

	/**
	 * Logs the details of a SegmentRequestDto if logging of request and response is
	 * enabled.
	 * 
	 * @param segmentRequestDto The SegmentRequestDto object to log.
	 */
	private void logRequest(SegmentRequestDto segmentRequestDto) {
		if (isLogRequestResponse) {
			logger.debug(LOGGER_SESSIONID, LOGGER_IDTYPE, "REQUEST:: SegmentRequestDto",
					utils.toString(segmentRequestDto));
		}
	}

	/**
	 * Logs the details of a ConvertFormatRequestDto if logging of request and
	 * response is enabled.
	 * 
	 * @param convertFormatRequestDto The ConvertFormatRequestDto object to log.
	 */
	private void logRequest(ConvertFormatRequestDto convertFormatRequestDto) {
		if (isLogRequestResponse) {
			logger.debug(LOGGER_SESSIONID, LOGGER_IDTYPE, "REQUEST:: ConvertFormatRequestDto",
					utils.toString(convertFormatRequestDto));
		}
	}

	/**
	 * Logs the object details as JSON if logging of request and response is
	 * enabled.
	 * 
	 * @param response The response object to log.
	 * @param <T>      The type of the response object.
	 */
	private <T> void logObject(T response) {
		if (isLogRequestResponse) {
			logger.debug(LOGGER_SESSIONID, LOGGER_IDTYPE, response.getClass(), gson.toJson(response));
		}
	}

	/**
	 * Logs the details of a Response object if logging of request and response is
	 * enabled. If the response contains a BiometricRecord, logs it using
	 * {@link #logBiometricRecord(String, BiometricRecord)}. Otherwise, logs the
	 * response as JSON.
	 * 
	 * @param response The Response object to log.
	 */
	private void logResponse(Response<?> response) {
		if (isLogRequestResponse) {
			Object resp = response.getResponse();
			if (resp instanceof BiometricRecord biometricRecord) {
				logBiometricRecord("Response BiometricRecord: ", biometricRecord);
			} else {
				logger.debug(LOGGER_SESSIONID, LOGGER_IDTYPE, "Response: ", gson.toJson(resp));
			}
		}
	}

	/**
	 * Logs the details of a BiometricRecord object if logging of request and
	 * response is enabled.
	 * 
	 * @param prefix          The prefix message to prepend to the logged details.
	 * @param biometricRecord The BiometricRecord object to log.
	 */
	private void logBiometricRecord(String prefix, BiometricRecord biometricRecord) {
		if (isLogRequestResponse) {
			logger.debug(LOGGER_SESSIONID, LOGGER_IDTYPE, prefix + utils.toString(biometricRecord));
		}
	}

	/**
	 * Decodes Base64-encoded data.
	 * 
	 * @param data The Base64-encoded data to decode.
	 * @return The decoded string.
	 * @throws BioSDKException If an error occurs during decoding.
	 */
	private String decode(String data) {
		try {
			return Utils.base64Decode(data);
		} catch (RuntimeException e) {
			logger.error(LOGGER_SESSIONID, LOGGER_IDTYPE, ErrorMessages.INVALID_REQUEST_BODY.toString(), e);
			throw new BioSDKException(ErrorMessages.INVALID_REQUEST_BODY.toString(),
					ErrorMessages.INVALID_REQUEST_BODY.getMessage() + ": " + e.toString() + " " + e.getMessage());
		}
	}
}