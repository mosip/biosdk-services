package io.mosip.biosdk.services.controller;

import static io.mosip.biosdk.services.constants.AppConstants.LOGGER_IDTYPE;
import static io.mosip.biosdk.services.constants.AppConstants.LOGGER_SESSIONID;

import java.util.ArrayList;
import java.util.Date;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import io.mosip.biosdk.services.config.LoggerConfig;
import io.mosip.biosdk.services.constants.ErrorMessages;
import io.mosip.biosdk.services.dto.ErrorDto;
import io.mosip.biosdk.services.dto.RequestDto;
import io.mosip.biosdk.services.dto.ResponseDto;
import io.mosip.biosdk.services.exceptions.BioSDKException;
import io.mosip.biosdk.services.factory.BioSdkServiceFactory;
import io.mosip.biosdk.services.spi.BioSdkServiceProvider;
import io.mosip.biosdk.services.utils.Utils;
import io.mosip.kernel.core.logger.spi.Logger;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.v3.oas.annotations.Parameter;

/**
 * Main controller class handling incoming requests for the BioSDK service.
 * 
 * This controller class exposes various BioSDK functionalities through RESTful
 * APIs. It utilizes Spring annotations for dependency injection, request
 * mapping, and security.
 * 
 *  CrossOrigin can be enabled for local testing 
 * @since 1.0.0
 */
//@SuppressWarnings({ "java:S5122" })
@RestController
@RequestMapping("/")
@Api(tags = "Sdk")
@CrossOrigin("*")
public class MainController {

	private Logger logger = LoggerConfig.logConfig(MainController.class);

	private Utils serviceUtil;
	private BioSdkServiceFactory bioSdkServiceFactory;
	private Gson gson = null;

	/**
	 * Constructor for MainController.
	 * 
	 * @param serviceUtil          The utility functions dependency.
	 * @param bioSdkServiceFactory The BioSDK service factory dependency.
	 */
	@Autowired
	public MainController(Utils serviceUtil, BioSdkServiceFactory bioSdkServiceFactory) {
		this.serviceUtil = serviceUtil;
		this.bioSdkServiceFactory = bioSdkServiceFactory;
		gson = new GsonBuilder().serializeNulls().create();
	}

	/**
	 * GET endpoint for basic service health check.
	 * 
	 * This API simply returns a message indicating the service is running.
	 * 
	 * @return A ResponseEntity object with status OK and a message indicating
	 *         service status.
	 */
	@GetMapping(path = "/")
	@ApiOperation(value = "Service status")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Service is running...") })
	public ResponseEntity<String> status() {
		Date d = new Date();
		return ResponseEntity.status(HttpStatus.OK).body("Service is running... " + d.toString());
	}

	/**
	 * GET endpoint requiring 'REGISTRATION_PROCESSOR' role for service health
	 * check.
	 * 
	 * This API is similar to the public health check but requires a specific role
	 * for access.
	 * 
	 * @return A ResponseEntity object with status OK and a message indicating
	 *         service status.
	 */
	@PreAuthorize("hasAnyRole('REGISTRATION_PROCESSOR')")
	@GetMapping(path = "/s")
	@ApiOperation(value = "Service role status")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Service is running...") })
	@SuppressWarnings({ "java:S4144" })
	public ResponseEntity<String> status1() {
		Date d = new Date();
		return ResponseEntity.status(HttpStatus.OK).body("Service is running... " + d.toString());
	}

	/**
	 * POST endpoint for BioSDK service initialization.
	 * 
	 * This API expects a request containing initialization parameters and returns a
	 * response containing the initialization result or any errors encountered.
	 * 
	 * @param request The BioSDK service initialization request object.
	 * @param errors  Spring validation errors (intended for internal use).
	 * @return A ResponseEntity object with status OK and a JSON response containing
	 *         the initialization result or errors.
	 * @throws BioSDKException If an error occurs during initialization.
	 */
	@PostMapping(path = "/init", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Initialization")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Initialization successful") })
	public ResponseEntity<String> init(@Validated @RequestBody(required = true) RequestDto request,
			@Parameter(hidden = true) Errors errors) {
		ResponseDto<Object> responseDto = generateResponseTemplate(request.getVersion());
		try {
			responseDto.setVersion(request.getVersion());
			BioSdkServiceProvider bioSdkServiceProviderImpl = null;
			bioSdkServiceProviderImpl = bioSdkServiceFactory.getBioSdkServiceProvider(request.getVersion());
			responseDto.setResponse(bioSdkServiceProviderImpl.init(request));
		} catch (BioSDKException e) {
			logger.error(LOGGER_SESSIONID, LOGGER_IDTYPE, "init: ", e);
			ErrorDto errorDto = new ErrorDto(e.getErrorCode(), e.getErrorText());
			responseDto.getErrors().add(errorDto);
			return ResponseEntity.status(HttpStatus.OK).body(gson.toJson(responseDto));
		}
		return ResponseEntity.status(HttpStatus.OK).body(gson.toJson(responseDto));
	}

	/**
	 * POST endpoint for BioSDK service match operation.
	 * 
	 * This API expects a request containing match parameters and returns a response
	 * containing the match result or any errors encountered.
	 * 
	 * @param request The BioSDK service match request object.
	 * @param errors  Spring validation errors (intended for internal use).
	 * @return A ResponseEntity object with status OK and a JSON response containing
	 *         the match result or errors.
	 * @throws BioSDKException If an error occurs during the match operation.
	 */
	@PostMapping(path = "/match", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Match")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Match successful") })
	public ResponseEntity<String> match(@Validated @RequestBody(required = true) RequestDto request,
			@Parameter(hidden = true) Errors errors) {
		ResponseDto<Object> responseDto = generateResponseTemplate(request.getVersion());
		try {
			responseDto.setVersion(request.getVersion());
			BioSdkServiceProvider bioSdkServiceProviderImpl = null;
			bioSdkServiceProviderImpl = bioSdkServiceFactory.getBioSdkServiceProvider(request.getVersion());
			responseDto.setResponse(bioSdkServiceProviderImpl.match(request));
		} catch (BioSDKException e) {
			logger.error(LOGGER_SESSIONID, LOGGER_IDTYPE, "match: ", e);
			ErrorDto errorDto = new ErrorDto(e.getErrorCode(), e.getErrorText());
			responseDto.getErrors().add(errorDto);
			return ResponseEntity.status(HttpStatus.OK).body(gson.toJson(responseDto));
		}
		return ResponseEntity.status(HttpStatus.OK).body(gson.toJson(responseDto));
	}

	/**
	 * POST endpoint for BioSDK service check quality operation.
	 * 
	 * This API expects a request containing a biometric sample and returns a
	 * response indicating the sample quality or any errors encountered.
	 * 
	 * @param request The BioSDK service check quality request object.
	 * @param errors  Spring validation errors (intended for internal use).
	 * @return A ResponseEntity object with status OK and a JSON response containing
	 *         the quality assessment result or errors.
	 * @throws BioSDKException If an error occurs during the quality check
	 *                         operation.
	 */
	@PostMapping(path = "/check-quality", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Check quality")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Check successful") })
	public ResponseEntity<String> checkQuality(@Validated @RequestBody(required = true) RequestDto request,
			@Parameter(hidden = true) Errors errors) {
		ResponseDto<Object> responseDto = generateResponseTemplate(request.getVersion());
		try {
			responseDto.setVersion(request.getVersion());
			BioSdkServiceProvider bioSdkServiceProviderImpl = null;
			bioSdkServiceProviderImpl = bioSdkServiceFactory.getBioSdkServiceProvider(request.getVersion());
			responseDto.setResponse(bioSdkServiceProviderImpl.checkQuality(request));
		} catch (BioSDKException e) {
			logger.error(LOGGER_SESSIONID, LOGGER_IDTYPE, "checkQuality: ", e);
			ErrorDto errorDto = new ErrorDto(e.getErrorCode(), e.getErrorText());
			responseDto.getErrors().add(errorDto);
			return ResponseEntity.status(HttpStatus.OK).body(gson.toJson(responseDto));
		}
		return ResponseEntity.status(HttpStatus.OK).body(gson.toJson(responseDto));
	}

	/**
	 * POST endpoint for BioSDK service extract template operation.
	 * 
	 * This API expects a request containing a biometric sample and returns a
	 * response containing the extracted biometric template or any errors
	 * encountered.
	 * 
	 * @param request The BioSDK service extract template request object.
	 * @param errors  Spring validation errors (intended for internal use).
	 * @return A ResponseEntity object with status OK and a JSON response containing
	 *         the extracted template or errors.
	 * @throws BioSDKException If an error occurs during the template extraction
	 *                         operation.
	 */
	@PostMapping(path = "/extract-template", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Extract template")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Extract successful") })
	public ResponseEntity<String> extractTemplate(@Validated @RequestBody(required = true) RequestDto request,
			@Parameter(hidden = true) Errors errors) {
		ResponseDto<Object> responseDto = generateResponseTemplate(request.getVersion());
		try {
			responseDto.setVersion(request.getVersion());
			BioSdkServiceProvider bioSdkServiceProviderImpl = null;
			bioSdkServiceProviderImpl = bioSdkServiceFactory.getBioSdkServiceProvider(request.getVersion());
			responseDto.setResponse(bioSdkServiceProviderImpl.extractTemplate(request));
		} catch (BioSDKException e) {
			logger.error(LOGGER_SESSIONID, LOGGER_IDTYPE, "extractTemplate: ", e);
			ErrorDto errorDto = new ErrorDto(e.getErrorCode(), e.getErrorText());
			responseDto.getErrors().add(errorDto);
			return ResponseEntity.status(HttpStatus.OK).body(gson.toJson(responseDto));
		}
		return ResponseEntity.status(HttpStatus.OK).body(gson.toJson(responseDto));
	}

	/**
	 * POST endpoint for BioSDK service format conversion operation.
	 *
	 * This API expects a request containing the biometric sample and conversion
	 * parameters in JSON format. It utilizes the `BioSdkServiceProvider`
	 * implementation corresponding to the requested version to perform the
	 * conversion. The response is a JSON object containing the converted biometric
	 * sample (if successful) or any errors encountered.
	 *
	 * @param request The BioSDK service format conversion request object. (Details
	 *                about the request object can be found in the
	 *                {@link RequestDto} class documentation)
	 * @param errors  Spring validation errors (intended for internal use).
	 * @return A ResponseEntity object with status OK and a JSON response containing
	 *         the converted sample or errors.
	 * @throws BioSDKException If an error occurs during the conversion operation.
	 */
	@PostMapping(path = "/convert-format", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Convert format")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Convert successful") })
	public ResponseEntity<String> convertFormat(@Validated @RequestBody(required = true) RequestDto request,
			@Parameter(hidden = true) Errors errors) {
		ResponseDto<Object> responseDto = generateResponseTemplate(request.getVersion());
		try {
			responseDto.setVersion(request.getVersion());
			BioSdkServiceProvider bioSdkServiceProviderImpl = null;
			bioSdkServiceProviderImpl = bioSdkServiceFactory.getBioSdkServiceProvider(request.getVersion());
			responseDto.setResponse(bioSdkServiceProviderImpl.convertFormat(request));
		} catch (BioSDKException e) {
			logger.error(LOGGER_SESSIONID, LOGGER_IDTYPE, "convertFormat: ", e);
			ErrorDto errorDto = new ErrorDto(e.getErrorCode(), e.getErrorText());
			responseDto.getErrors().add(errorDto);
			return ResponseEntity.status(HttpStatus.OK).body(gson.toJson(responseDto));
		}
		return ResponseEntity.status(HttpStatus.OK).body(gson.toJson(responseDto));
	}

	/**
	 * POST endpoint for BioSDK service segmentation operation.
	 *
	 * This API expects a request containing the biometric sample and segmentation
	 * parameters in JSON format. It utilizes the `BioSdkServiceProvider`
	 * implementation corresponding to the requested version to perform the
	 * segmentation. The response is a JSON object containing the segmented
	 * biometric data (if successful) or any errors encountered.
	 *
	 * @param request The BioSDK service segmentation request object. (Details about
	 *                the request object can be found in the {@link RequestDto}
	 *                class documentation)
	 * @param errors  Spring validation errors (intended for internal use).
	 * @return A ResponseEntity object with status OK and a JSON response containing
	 *         the segmented data or errors.
	 * @throws BioSDKException If an error occurs during the segmentation operation.
	 */
	@PostMapping(path = "/segment", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Segment")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Segment successful") })
	public ResponseEntity<String> segment(@Validated @RequestBody(required = true) RequestDto request,
			@Parameter(hidden = true) Errors errors) {
		ResponseDto<Object> responseDto = generateResponseTemplate(request.getVersion());
		try {
			responseDto.setVersion(request.getVersion());
			BioSdkServiceProvider bioSdkServiceProviderImpl = null;
			bioSdkServiceProviderImpl = bioSdkServiceFactory.getBioSdkServiceProvider(request.getVersion());
			responseDto.setResponse(bioSdkServiceProviderImpl.segment(request));
		} catch (BioSDKException e) {
			logger.error(LOGGER_SESSIONID, LOGGER_IDTYPE, "segment: ", e);
			ErrorDto errorDto = new ErrorDto(e.getErrorCode(), e.getErrorText());
			responseDto.getErrors().add(errorDto);
			return ResponseEntity.status(HttpStatus.OK).body(gson.toJson(responseDto));
		}
		return ResponseEntity.status(HttpStatus.OK).body(gson.toJson(responseDto));
	}

	/**
	 * Generates a base response template object for all API responses.
	 *
	 * This private helper method creates a {@link ResponseDto} object with common
	 * fields pre-populated. The response includes the request version, current
	 * response time, an empty error list, and an empty response field. Specific API
	 * methods can populate the response field with the actual response data.
	 *
	 * @param version The version of the request for which the response is
	 *                generated.
	 * @return A ResponseDto object containing common response fields.
	 */
	private ResponseDto<Object> generateResponseTemplate(String version) {
		ResponseDto<Object> responseDto = new ResponseDto<>();
		responseDto.setVersion(version);
		responseDto.setResponsetime(serviceUtil.getCurrentResponseTime());
		responseDto.setErrors(new ArrayList<>());
		responseDto.setResponse("");
		return responseDto;
	}

	@SuppressWarnings({ "unused" })
	private String getVersion(String request) throws BioSDKException {
		JSONParser parser = new JSONParser();
		try {
			JSONObject js = (JSONObject) parser.parse(request);
			return js.get("version").toString();
		} catch (ParseException e) {
			throw new BioSDKException(ErrorMessages.UNCHECKED_EXCEPTION.toString(), e.getMessage());
		}
	}
}