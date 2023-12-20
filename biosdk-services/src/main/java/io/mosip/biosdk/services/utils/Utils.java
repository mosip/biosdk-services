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
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;

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

@Component
public class Utils {
    @Autowired
    private Gson gson;

    private String utcDateTimePattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

    public String getCurrentResponseTime() {
        return DateUtils.formatDate(new Date(System.currentTimeMillis()), utcDateTimePattern);
    }

    public RequestDto getRequestInfo(String request) throws ParseException {
        return gson.fromJson(request, RequestDto.class);
    }

	public static String base64Decode(String data){
        return new String(Base64.getDecoder().decode(data), StandardCharsets.UTF_8);
    }
	
	public String toString(BiometricRecord biometricRecord) {
		if(biometricRecord == null) {
			return "null";
		}
		
		StringBuilder stringBuilder = new StringBuilder();
		appendString(biometricRecord, stringBuilder);
		return stringBuilder.toString();
	}

    private void appendString(BiometricRecord biometricRecord, StringBuilder stringBuilder) {
    	if(biometricRecord == null) {
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
			if(segments == null) {
	    		stringBuilder.append("null");
			} else {
				appendString(segments.stream().iterator(), stringBuilder, this::appendString);
			}
			stringBuilder.append(" }");
		}
	}
    
    private String stringOf(Object obj) {
    	return obj == null ? "null" : gson.toJson(obj);
    }

	private <T> void appendString(Iterator<T> iterator, StringBuilder stringBuilder, BiConsumer<T, StringBuilder> appendBiConsumer) {
		stringBuilder.append("[ ");
		while (iterator.hasNext()) {
			T element = iterator.next();
			appendBiConsumer.accept(element, stringBuilder);
			if(iterator.hasNext()) {
				stringBuilder.append(", ");
			}
		}
		stringBuilder.append(" ]");
	}

	private void appendString(BIR bir, StringBuilder stringBuilder) {
		if(bir == null) {
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

	private static String getHashOfBytes(byte[] byteArray) {
		return byteArray == null ? "null" : "\""+ DigestUtils.sha256Hex(byteArray) + "\"";
	}
	
	public String toString(ExtractTemplateRequestDto extractTemplateRequestDto) {
		if(extractTemplateRequestDto == null) {
			return "null";
		}
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("{");
		stringBuilder.append(" \"_modelClass\": \"ExtractTemplateRequestDto\"");
		stringBuilder.append(", \"flags\":");
		stringBuilder.append(stringOf(extractTemplateRequestDto.getFlags()));
		stringBuilder.append(", \"modalitiesToExtract\": ");
		stringBuilder.append(stringOf(extractTemplateRequestDto.getModalitiesToExtract()));
		stringBuilder.append(", \"sample\": ");
		appendString(extractTemplateRequestDto.getSample(), stringBuilder);
		stringBuilder.append(" }");
		return stringBuilder.toString();
	}

	public String toString(MatchRequestDto matchRequestDto) {
		if(matchRequestDto == null) {
			return "null";
		}
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("{");
		stringBuilder.append(" \"_modelClass\": \"MatchRequestDto\"");
		stringBuilder.append(", \"flags\":");
		stringBuilder.append(stringOf(matchRequestDto.getFlags()));
		stringBuilder.append(", \"modalitiesToMatch\": ");
		stringBuilder.append(stringOf(matchRequestDto.getModalitiesToMatch()));
		stringBuilder.append(", \"sample\": ");
		appendString(matchRequestDto.getSample(), stringBuilder);
		stringBuilder.append(", \"gallery\": ");
		appendString(Arrays.stream(matchRequestDto.getGallery()).iterator(), stringBuilder, this::appendString);
		stringBuilder.append(" }");
		return stringBuilder.toString();
	}

	public String toString(InitRequestDto initRequestDto) {
		if(initRequestDto == null) {
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

	public String toString(CheckQualityRequestDto checkQualityRequestDto) {
		if(checkQualityRequestDto == null) {
			return "null";
		}
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("{");
		stringBuilder.append(" \"_modelClass\": \"CheckQualityRequestDto\"");
		stringBuilder.append(", \"flags\":");
		stringBuilder.append(stringOf(checkQualityRequestDto.getFlags()));
		stringBuilder.append(", \"modalitiesToCheck\": ");
		stringBuilder.append(stringOf(checkQualityRequestDto.getModalitiesToCheck()));
		stringBuilder.append(", \"sample\": ");
		appendString(checkQualityRequestDto.getSample(), stringBuilder);
		stringBuilder.append(" }");
		return stringBuilder.toString();
	}

	public String toString(SegmentRequestDto segmentRequestDto) {
		if(segmentRequestDto == null) {
			return "null";
		}
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("{");
		stringBuilder.append(" \"_modelClass\": \"SegmentRequestDto\"");
		stringBuilder.append(", \"flags\":");
		stringBuilder.append(stringOf(segmentRequestDto.getFlags()));
		stringBuilder.append(", \"modalitiesToSegment\": ");
		stringBuilder.append(stringOf(segmentRequestDto.getModalitiesToSegment()));
		stringBuilder.append(", \"sample\": ");
		appendString(segmentRequestDto.getSample(), stringBuilder);
		stringBuilder.append(" }");
		return stringBuilder.toString();
	}

	public String toString(ConvertFormatRequestDto convertFormatRequestDto) {
		if(convertFormatRequestDto == null) {
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
		stringBuilder.append(", \"sample\": ");
		appendString(convertFormatRequestDto.getSample(), stringBuilder);
		stringBuilder.append(", \"sourceParams\":");
		stringBuilder.append(stringOf(convertFormatRequestDto.getSourceParams()));
		stringBuilder.append(", \"targetParams\": ");
		stringBuilder.append(stringOf(convertFormatRequestDto.getTargetParams()));
		appendString(convertFormatRequestDto.getSample(), stringBuilder);
		stringBuilder.append(" }");
		return stringBuilder.toString();
	}
	
	public String toString(BDBInfo bdbInfo) {
		if(bdbInfo == null) {
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
	
	public String toString(BIRInfo birInfo) {
		if(birInfo == null) {
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

	private String dateAsString(LocalDateTime localDateTime) {
		return localDateTime == null ? "null" : DateUtils.formatToISOString(localDateTime);
	}

	private static String booleanAsString(Boolean bool) {
		return bool == null ? "null" : Boolean.toString(bool);
	}
}
