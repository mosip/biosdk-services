/*
 * Copyright (c) 2018-2026 MOSIP.
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package io.mosip.biosdk.services.utils;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.function.BiConsumer;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import io.mosip.biosdk.services.dto.RequestDto;
import io.mosip.biosdk.services.impl.spec_1_0.dto.request.CheckQualityRequestDto;
import io.mosip.biosdk.services.impl.spec_1_0.dto.request.ConvertFormatRequestDto;
import io.mosip.biosdk.services.impl.spec_1_0.dto.request.ExtractTemplateRequestDto;
import io.mosip.biosdk.services.impl.spec_1_0.dto.request.InitRequestDto;
import io.mosip.biosdk.services.impl.spec_1_0.dto.request.MatchRequestDto;
import io.mosip.biosdk.services.impl.spec_1_0.dto.request.SegmentRequestDto;
import io.mosip.kernel.biometrics.entities.BDBInfo;
import io.mosip.kernel.biometrics.entities.BIR;
import io.mosip.kernel.biometrics.entities.BIRInfo;
import io.mosip.kernel.biometrics.entities.BiometricRecord;
import io.mosip.kernel.core.util.DateUtils;

/**
 * Utility class providing methods to convert various DTOs (Data Transfer
 * Objects) to their JSON string representations.
 * <p>
 * This class uses Gson for JSON serialization and provides methods to convert
 * different types of DTOs including {@link ExtractTemplateRequestDto},
 * {@link MatchRequestDto}, {@link InitRequestDto},
 * {@link CheckQualityRequestDto}, {@link SegmentRequestDto},
 * {@link ConvertFormatRequestDto}, {@link BDBInfo}, {@link BIRInfo}, and
 * {@link BiometricRecord}.
 * </p>
 * <p>
 * The conversion methods handle null values gracefully and use helper methods
 * to format dates and perform hashing operations on byte arrays.
 * </p>
 * <p>
 * This class is designed to assist in logging, debugging, or transmitting data
 * in a structured JSON format compatible with the MOSIP Bio SDK services.
 * </p>
 * 
 * @since 1.0.0
 * @copyright 2018-2026 MOSIP
 */
@Component
public class Utils {
	/**
	 * Gson instance that serializes {@code null} fields, used for debug dumps.
	 */
	private Gson gson;

	/**
	 * UTC pattern for {@link #getCurrentResponseTime()} ({@code responsetime} on
	 * {@link io.mosip.biosdk.services.dto.ResponseDto}).
	 */
	private String utcDateTimePattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

	/**
	 * JSON fragment key for vendor flag maps on operation DTOs.
	 */
	private static final String FLAGS = ", \"flags\":";

	/**
	 * JSON fragment key for the probe {@link BiometricRecord}.
	 */
	private static final String SAMPLE = ", \"sample\":";

	/**
	 * Constructs a new Utils instance initializing Gson for JSON serialization.
	 */
	public Utils() {
		gson = new GsonBuilder().serializeNulls().create();
	}

	/**
	 * Retrieves the current timestamp formatted as per UTC date-time pattern.
	 *
	 * @return formatted current timestamp.
	 */
	public String getCurrentResponseTime() {
		return DateUtils.formatDate(new Date(System.currentTimeMillis()), utcDateTimePattern);
	}

	/**
	 * Parses the provided JSON string into a RequestDto object.
	 *
	 * @param request JSON string representation of the request.
	 * @return RequestDto object parsed from the JSON.
	 */
	public RequestDto getRequestInfo(String request) {
		return gson.fromJson(request, RequestDto.class);
	}

	/**
	 * Decodes a Base64 encoded string into its original UTF-8 string
	 * representation.
	 *
	 * @param data Base64 encoded string to decode.
	 * @return decoded UTF-8 string.
	 */
	public static String base64Decode(String data) {
		return new String(Base64.getDecoder().decode(data), StandardCharsets.UTF_8);
	}

	/**
	 * Converts a BiometricRecord object to its string representation. If the
	 * BiometricRecord object is null, returns "null". Otherwise, constructs a
	 * JSON-like string representation including key attributes such as biometric
	 * information, version, and segments.
	 *
	 * @param biometricRecord The BiometricRecord object to convert to string.
	 * @return String representation of the BiometricRecord object.
	 */
	public String toString(BiometricRecord biometricRecord) {
		if (biometricRecord == null) {
			return "null";
		}

		StringBuilder stringBuilder = new StringBuilder();
		appendString(biometricRecord, stringBuilder);
		return stringBuilder.toString();
	}

	/**
	 * Helper method to append the string representation of a BiometricRecord object
	 * to StringBuilder. Constructs a JSON-like format including biometric
	 * information, version, and segments.
	 *
	 * @param biometricRecord The BiometricRecord object to convert to string.
	 * @param stringBuilder   StringBuilder to append the string representation.
	 */
	private void appendString(BiometricRecord biometricRecord, StringBuilder stringBuilder) {
		if (biometricRecord == null) {
			stringBuilder.append("null");
		} else {
			stringBuilder.append("{");
			stringBuilder.append(" \"_modelClass\": \"BiometricRecord\"");
			stringBuilder.append(", \"birInfo\": ");
			stringBuilder.append(toString(biometricRecord.getBirInfo()));
			stringBuilder.append(", \"cbeffversion\":");
			stringBuilder.append(stringOf(biometricRecord.getCbeffversion()));
			stringBuilder.append(", \"version\":");
			stringBuilder.append(stringOf(biometricRecord.getVersion()));
			stringBuilder.append(", \"segments\":");
			List<BIR> segments = biometricRecord.getSegments();
			if (segments == null) {
				stringBuilder.append("null");
			} else {
				appendString(segments.stream().iterator(), stringBuilder, this::appendString);
			}
			stringBuilder.append(" }");
		}
	}

	/**
	 * Converts the provided object to its JSON representation using Gson. If the
	 * object is {@code null}, returns the string {@code "null"}.
	 *
	 * @param obj The object to convert to JSON.
	 * @return JSON representation of the object or {@code "null"} if the object is
	 *         null.
	 */
	private String stringOf(Object obj) {
		return obj == null ? "null" : gson.toJson(obj);
	}

	/**
	 * Appends elements from the provided iterator to the StringBuilder using a
	 * custom BiConsumer for element-specific appending logic.
	 *
	 * @param iterator         The iterator containing elements to append.
	 * @param stringBuilder    The StringBuilder to append elements to.
	 * @param appendBiConsumer The BiConsumer defining how to append each element to
	 *                         the StringBuilder. Typically encapsulates specific
	 *                         logic for each element type.
	 */
	private <T> void appendString(Iterator<T> iterator, StringBuilder stringBuilder,
			BiConsumer<T, StringBuilder> appendBiConsumer) {
		stringBuilder.append("[ ");
		while (iterator.hasNext()) {
			T element = iterator.next();
			appendBiConsumer.accept(element, stringBuilder);
			if (iterator.hasNext()) {
				stringBuilder.append(", ");
			}
		}
		stringBuilder.append(" ]");
	}

	/**
	 * Appends a Biometric Information Record (BIR) object to the StringBuilder in
	 * JSON-like format, including its various attributes and hashes. If the
	 * provided BIR object is null, appends the string "null".
	 *
	 * @param bir           The Biometric Information Record (BIR) object to append.
	 * @param stringBuilder The StringBuilder to which the BIR object is appended.
	 */
	private void appendString(BIR bir, StringBuilder stringBuilder) {
		if (bir == null) {
			stringBuilder.append("null");
		} else {
			stringBuilder.append("{");
			stringBuilder.append(" \"_modelClass\": \"BIR\"");
			stringBuilder.append(", \"bdbInfo\": ");
			stringBuilder.append(toString(bir.getBdbInfo()));
			stringBuilder.append(", \"birInfo\": ");
			stringBuilder.append(toString(bir.getBirInfo()));
			stringBuilder.append(", \"cbeffversion\": ");
			stringBuilder.append(stringOf(bir.getCbeffversion()));
			stringBuilder.append(", \"others\": ");
			stringBuilder.append(stringOf(bir.getOthers()));
			stringBuilder.append(", \"sbHash\": ");
			stringBuilder.append(getHashOfBytes(bir.getSb()));
			stringBuilder.append(", \"sbInfo\": ");
			stringBuilder.append(stringOf(bir.getSbInfo()));
			stringBuilder.append(", \"version\": ");
			stringBuilder.append(stringOf(bir.getVersion()));
			stringBuilder.append(", \"bdbHash\": ");
			stringBuilder.append(getHashOfBytes(bir.getBdb()));
			stringBuilder.append(" }");
		}
	}

	/**
	 * Computes the SHA-256 hash of the provided byte array and returns it as a
	 * formatted string. If the byte array is null, returns the string "null".
	 *
	 * @param byteArray The byte array to compute the SHA-256 hash from.
	 * @return A formatted string representing the SHA-256 hash of the byte array.
	 */
	private static String getHashOfBytes(byte[] byteArray) {
		return byteArray == null ? "null" : "\"" + DigestUtils.sha256Hex(byteArray) + "\"";
	}

	/**
	 * Returns a JSON string representation of the ExtractTemplateRequestDto object.
	 * If the provided object is null, returns the string "null".
	 *
	 * @param extractTemplateRequestDto The ExtractTemplateRequestDto object to
	 *                                  convert to JSON string.
	 * @return JSON string representation of the ExtractTemplateRequestDto object.
	 */
	public String toString(ExtractTemplateRequestDto extractTemplateRequestDto) {
		if (extractTemplateRequestDto == null) {
			return "null";
		}
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("{");
		stringBuilder.append(" \"_modelClass\": \"ExtractTemplateRequestDto\"");
		stringBuilder.append(FLAGS);
		stringBuilder.append(stringOf(extractTemplateRequestDto.getFlags()));
		stringBuilder.append(", \"modalitiesToExtract\": ");
		stringBuilder.append(stringOf(extractTemplateRequestDto.getModalitiesToExtract()));
		stringBuilder.append(SAMPLE);
		appendString(extractTemplateRequestDto.getSample(), stringBuilder);
		stringBuilder.append(" }");
		return stringBuilder.toString();
	}

	/**
	 * Returns a JSON string representation of the MatchRequestDto object. If the
	 * provided object is null, returns the string "null".
	 *
	 * @param matchRequestDto The MatchRequestDto object to convert to JSON string.
	 * @return JSON string representation of the MatchRequestDto object.
	 */
	public String toString(MatchRequestDto matchRequestDto) {
		if (matchRequestDto == null) {
			return "null";
		}
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("{");
		stringBuilder.append(" \"_modelClass\": \"MatchRequestDto\"");
		stringBuilder.append(", " + FLAGS + ":");
		stringBuilder.append(stringOf(matchRequestDto.getFlags()));
		stringBuilder.append(", \"modalitiesToMatch\": ");
		stringBuilder.append(stringOf(matchRequestDto.getModalitiesToMatch()));
		stringBuilder.append(SAMPLE);
		appendString(matchRequestDto.getSample(), stringBuilder);
		stringBuilder.append(", \"gallery\": ");
		appendString(Arrays.stream(matchRequestDto.getGallery()).iterator(), stringBuilder, this::appendString);
		stringBuilder.append(" }");
		return stringBuilder.toString();
	}

	/**
	 * Returns a JSON string representation of the InitRequestDto object. If the
	 * provided object is null, returns the string "null".
	 *
	 * @param initRequestDto The InitRequestDto object to convert to JSON string.
	 * @return JSON string representation of the InitRequestDto object.
	 */
	public String toString(InitRequestDto initRequestDto) {
		if (initRequestDto == null) {
			return "null";
		}
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("{");
		stringBuilder.append(" \"_modelClass\": \"InitRequestDto\"");
		stringBuilder.append(", \"initParams\":");
		stringBuilder.append(stringOf(initRequestDto));
		stringBuilder.append(" }");
		return stringBuilder.toString();
	}

	/**
	 * Returns a JSON string representation of the CheckQualityRequestDto object. If
	 * the provided object is null, returns the string "null".
	 *
	 * @param checkQualityRequestDto The CheckQualityRequestDto object to convert to
	 *                               JSON string.
	 * @return JSON string representation of the CheckQualityRequestDto object.
	 */
	public String toString(CheckQualityRequestDto checkQualityRequestDto) {
		if (checkQualityRequestDto == null) {
			return "null";
		}
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("{");
		stringBuilder.append(" \"_modelClass\": \"CheckQualityRequestDto\"");
		stringBuilder.append(FLAGS);
		stringBuilder.append(stringOf(checkQualityRequestDto.getFlags()));
		stringBuilder.append(", \"modalitiesToCheck\": ");
		stringBuilder.append(stringOf(checkQualityRequestDto.getModalitiesToCheck()));
		stringBuilder.append(SAMPLE);
		appendString(checkQualityRequestDto.getSample(), stringBuilder);
		stringBuilder.append(" }");
		return stringBuilder.toString();
	}

	/**
	 * Returns a JSON string representation of the SegmentRequestDto object. If the
	 * provided object is null, returns the string "null".
	 *
	 * @param segmentRequestDto The SegmentRequestDto object to convert to JSON
	 *                          string.
	 * @return JSON string representation of the SegmentRequestDto object.
	 */
	public String toString(SegmentRequestDto segmentRequestDto) {
		if (segmentRequestDto == null) {
			return "null";
		}
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("{");
		stringBuilder.append(" \"_modelClass\": \"SegmentRequestDto\"");
		stringBuilder.append(FLAGS);
		stringBuilder.append(stringOf(segmentRequestDto.getFlags()));
		stringBuilder.append(", \"modalitiesToSegment\": ");
		stringBuilder.append(stringOf(segmentRequestDto.getModalitiesToSegment()));
		stringBuilder.append(SAMPLE);
		appendString(segmentRequestDto.getSample(), stringBuilder);
		stringBuilder.append(" }");
		return stringBuilder.toString();
	}

	/**
	 * Returns a JSON string representation of the ConvertFormatRequestDto object.
	 * If the provided object is null, returns the string "null".
	 *
	 * @param convertFormatRequestDto The ConvertFormatRequestDto object to convert
	 *                                to JSON string.
	 * @return JSON string representation of the ConvertFormatRequestDto object.
	 */
	public String toString(ConvertFormatRequestDto convertFormatRequestDto) {
		if (convertFormatRequestDto == null) {
			return "null";
		}
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("{");
		stringBuilder.append(" \"_modelClass\": \"ConvertFormatRequestDto\"");
		stringBuilder.append(", \"sourceFormat\":");
		stringBuilder.append(stringOf(convertFormatRequestDto.getSourceFormat()));
		stringBuilder.append(", \"targetFormat\": ");
		stringBuilder.append(stringOf(convertFormatRequestDto.getTargetFormat()));
		stringBuilder.append(", \"modalitiesToConvert\": ");
		stringBuilder.append(stringOf(convertFormatRequestDto.getModalitiesToConvert()));
		stringBuilder.append(SAMPLE);
		appendString(convertFormatRequestDto.getSample(), stringBuilder);
		stringBuilder.append(", \"sourceParams\":");
		stringBuilder.append(stringOf(convertFormatRequestDto.getSourceParams()));
		stringBuilder.append(", \"targetParams\": ");
		stringBuilder.append(stringOf(convertFormatRequestDto.getTargetParams()));
		appendString(convertFormatRequestDto.getSample(), stringBuilder);
		stringBuilder.append(" }");
		return stringBuilder.toString();
	}

	/**
	 * Returns a JSON string representation of the BDBInfo object. If the provided
	 * object is null, returns the string "null".
	 *
	 * @param bdbInfo The BDBInfo object to convert to JSON string.
	 * @return JSON string representation of the BDBInfo object.
	 */
	public String toString(BDBInfo bdbInfo) {
		if (bdbInfo == null) {
			return "null";
		}
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("{");
		stringBuilder.append(" \"_modelClass\": \"BDBInfo\"");
		stringBuilder.append(", \"challengeResponseHash\":");
		stringBuilder.append(getHashOfBytes(bdbInfo.getChallengeResponse()));
		stringBuilder.append(", \"index\": ");
		stringBuilder.append(stringOf(bdbInfo.getIndex()));
		stringBuilder.append(", \"format\": ");
		stringBuilder.append(stringOf(bdbInfo.getFormat()));
		stringBuilder.append(", \"encryption\":");
		stringBuilder.append(booleanAsString(bdbInfo.getEncryption()));
		stringBuilder.append(", \"creationDate\": ");
		stringBuilder.append(stringOf(dateAsString(bdbInfo.getCreationDate())));
		stringBuilder.append(", \"notValidBefore\": ");
		stringBuilder.append(stringOf(dateAsString(bdbInfo.getNotValidBefore())));
		stringBuilder.append(", \"notValidAfter\": ");
		stringBuilder.append(stringOf(dateAsString(bdbInfo.getNotValidAfter())));
		stringBuilder.append(", \"type\": ");
		stringBuilder.append(stringOf(bdbInfo.getType()));
		stringBuilder.append(", \"subtype\": ");
		stringBuilder.append(stringOf(bdbInfo.getSubtype()));
		stringBuilder.append(", \"level\": ");
		stringBuilder.append(stringOf(bdbInfo.getLevel()));
		stringBuilder.append(", \"product\": ");
		stringBuilder.append(stringOf(bdbInfo.getProduct()));
		stringBuilder.append(", \"captureDevice\": ");
		stringBuilder.append(stringOf(bdbInfo.getCaptureDevice()));
		stringBuilder.append(", \"featureExtractionAlgorithm\": ");
		stringBuilder.append(stringOf(bdbInfo.getFeatureExtractionAlgorithm()));
		stringBuilder.append(", \"comparisonAlgorithm\": ");
		stringBuilder.append(stringOf(bdbInfo.getComparisonAlgorithm()));
		stringBuilder.append(", \"compressionAlgorithm\": ");
		stringBuilder.append(stringOf(bdbInfo.getCompressionAlgorithm()));
		stringBuilder.append(", \"purpose\": ");
		stringBuilder.append(stringOf(bdbInfo.getPurpose()));
		stringBuilder.append(", \"quality\": ");
		stringBuilder.append(stringOf(bdbInfo.getQuality()));
		stringBuilder.append(" }");
		return stringBuilder.toString();
	}

	/**
	 * Returns a JSON string representation of the BIRInfo object. If the provided
	 * object is null, returns the string "null".
	 *
	 * @param birInfo The BIRInfo object to convert to JSON string.
	 * @return JSON string representation of the BIRInfo object.
	 */
	public String toString(BIRInfo birInfo) {
		if (birInfo == null) {
			return "null";
		}
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("{");
		stringBuilder.append(" \"_modelClass\": \"BIRInfo\"");
		stringBuilder.append(", \"creator\": ");
		stringBuilder.append(stringOf(birInfo.getCreator()));
		stringBuilder.append(", \"index\": ");
		stringBuilder.append(stringOf(birInfo.getIndex()));
		stringBuilder.append(", \"payloadHash\":");
		stringBuilder.append(getHashOfBytes(birInfo.getPayload()));
		stringBuilder.append(", \"integrity\":");
		stringBuilder.append(booleanAsString(birInfo.getIntegrity()));
		stringBuilder.append(", \"creationDate\": ");
		stringBuilder.append(stringOf(dateAsString(birInfo.getCreationDate())));
		stringBuilder.append(", \"notValidBefore\": ");
		stringBuilder.append(stringOf(dateAsString(birInfo.getNotValidBefore())));
		stringBuilder.append(", \"notValidAfter\": ");
		stringBuilder.append(stringOf(dateAsString(birInfo.getNotValidAfter())));
		stringBuilder.append(" }");
		return stringBuilder.toString();
	}

	/**
	 * Converts a LocalDateTime object to its ISO-8601 string representation. If the
	 * provided LocalDateTime object is null, returns the string "null".
	 *
	 * @param localDateTime The LocalDateTime object to convert.
	 * @return ISO-8601 string representation of the LocalDateTime object.
	 */
	private String dateAsString(LocalDateTime localDateTime) {
		return localDateTime == null ? "null" : DateUtils.formatToISOString(localDateTime);
	}

	/**
	 * Converts a Boolean object to its string representation. If the provided
	 * Boolean object is null, returns the string "null".
	 *
	 * @param bool The Boolean object to convert.
	 * @return String representation of the Boolean object.
	 */
	private static String booleanAsString(Boolean bool) {
		return bool == null ? "null" : Boolean.toString(bool);
	}
}