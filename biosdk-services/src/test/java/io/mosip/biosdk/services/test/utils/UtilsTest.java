package io.mosip.biosdk.services.test.utils;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;

import io.mosip.biosdk.services.impl.spec_1_0.dto.request.*;
import io.mosip.biosdk.services.utils.Utils;
import io.mosip.kernel.biometrics.constant.BiometricType;
import io.mosip.kernel.biometrics.entities.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.mosip.biosdk.services.dto.RequestDto;

public class UtilsTest {

    private Utils utils;

    @BeforeEach
    void init() {
        utils = new Utils();
    }

    @Test
    void testBase64Decode() {
        String encoded = Base64.getEncoder().encodeToString("hello".getBytes(StandardCharsets.UTF_8));
        String decoded = Utils.base64Decode(encoded);
        assertEquals("hello", decoded);
    }

    @Test
    void testGetCurrentResponseTime() {
        String result = utils.getCurrentResponseTime();
        assertNotNull(result);
        assertTrue(result.contains("T")); // basic ISO check
        assertTrue(result.endsWith("Z"));
    }

    @Test
    void testGetRequestInfo() {
        String json = "{\"id\":\"123\",\"request\":\"abc\"}";
        RequestDto dto = utils.getRequestInfo(json);
        assertEquals("abc", dto.getRequest());
    }

    @Test
    void testToStringBiometricRecordNull() {
        assertEquals("null", utils.toString((BiometricRecord) null));
    }

    @Test
    void testToStringBiometricRecordNonNull() {
        BiometricRecord rec = new BiometricRecord();
        rec.setBirInfo(new BIRInfo());
        rec.setSegments(Collections.emptyList());
        String s = utils.toString(rec);
        assertTrue(s.contains("\"_modelClass\": \"BiometricRecord\""));
        assertTrue(s.contains("\"segments\""));
        assertTrue(s.contains("\"version\""));
        assertTrue(s.contains("\"cbeffversion\""));
    }

    @Test
    void testToStringBIR() {
        BIR bir = new BIR();
        bir.setBdb("data".getBytes());
        bir.setSb("sb".getBytes());
        bir.setBdbInfo(new BDBInfo());
        bir.setBirInfo(new BIRInfo());

        String str = utils.toString((BIRInfo) bir.getBirInfo());
        assertTrue(str.contains("\"_modelClass\": \"BIRInfo\""));
    }

    @Test
    void testToStringBDBInfo() {
        BDBInfo info = new BDBInfo();
        info.setChallengeResponse("abcd".getBytes());
        info.setCreationDate(LocalDateTime.now());
        info.setEncryption(true);

        String s = utils.toString(info);
        assertTrue(s.contains("\"_modelClass\": \"BDBInfo\""));
        assertTrue(s.contains("\"challengeResponseHash\""));
    }

    @Test
    void testToStringBIRInfo() {
        BIRInfo info = new BIRInfo();
        info.setPayload("xyz".getBytes());
        info.setIntegrity(true);
        info.setCreationDate(LocalDateTime.now());

        String s = utils.toString(info);
        assertTrue(s.contains("\"_modelClass\": \"BIRInfo\""));
        assertTrue(s.contains("\"payloadHash\""));
    }

    @Test
    void testToStringExtractTemplateRequestDto() {
        ExtractTemplateRequestDto dto = new ExtractTemplateRequestDto();
        Map<String, String> flags = new HashMap<>();
        flags.put("flag1", "true");

        dto.setFlags(flags);
        String s = utils.toString(dto);
        assertTrue(s.contains("\"_modelClass\": \"ExtractTemplateRequestDto\""));
        assertTrue(s.contains("\"flags\""));
    }

    @Test
    void testToStringInitRequestDto() {
        InitRequestDto dto = new InitRequestDto();

        String s = utils.toString(dto);
        assertTrue(s.contains("\"_modelClass\": \"InitRequestDto\""));
        assertTrue(s.contains("\"initParams\""));
    }

    @Test
    void testToStringMatchRequestDto() {
        MatchRequestDto dto = new MatchRequestDto();

        // Flags
        Map<String, String> flags = new HashMap<>();
        flags.put("flag1", "true");
        dto.setFlags(flags);

        // --- Build BiometricRecord properly ---
        // Create BIR segment
        BIR bir = new BIR();
        bir.setBdb("a".getBytes());   // biometric sample bytes

        List<BIR> segments = new ArrayList<>();
        segments.add(bir);

        // Create BiometricRecord
        BiometricRecord record = new BiometricRecord();
        record.setSegments(segments);

        dto.setGallery(new BiometricRecord[]{ record });

        // Call util
        String s = utils.toString(dto);

        // Assertions
        assertTrue(s.contains("\"_modelClass\": \"MatchRequestDto\""));
        assertTrue(s.contains("\"gallery\""));
    }


    @Test
    void testBooleanAsStringAndDateAsStringCoverage() throws Exception {
        // Via public methods
        BIRInfo info = new BIRInfo();
        info.setIntegrity(null);
        info.setNotValidAfter(null);

        String s = utils.toString(info);
        assertTrue(s.contains("\"integrity\":null"));
        assertFalse(s.contains("\"notValidAfter\":\"null\""));
    }

    @Test
    void testToStringCheckQualityRequestDto() {
        CheckQualityRequestDto dto = new CheckQualityRequestDto();

        // Flags
        Map<String, String> flags = new HashMap<>();
        flags.put("flag1", "true");
        dto.setFlags(flags);

        // Modalities
        dto.setModalitiesToCheck(List.of(BiometricType.FACE, BiometricType.FINGER));

        // Sample BiometricRecord
        BIR bir = new BIR();
        bir.setBdb("sample".getBytes());
        BiometricRecord record = new BiometricRecord();
        record.setOthers(new HashMap<>());
        record.setSegments(new ArrayList<>());
        dto.setSample(record);

        String s = utils.toString(dto);
        assertTrue(s.contains("\"_modelClass\": \"CheckQualityRequestDto\""));
        assertTrue(s.contains("\"modalitiesToCheck\""));
        assertTrue(s.contains("\"sample\""));
    }

    @Test
    void testToStringSegmentRequestDto() {
        SegmentRequestDto dto = new SegmentRequestDto();

        // Flags
        Map<String, String> flags = new HashMap<>();
        flags.put("flag2", "true");
        dto.setFlags(flags);

        // Modalities
        dto.setModalitiesToSegment(List.of(BiometricType.FACE, BiometricType.FINGER));

        // Sample BiometricRecord
        BIR bir = new BIR();
        bir.setBdb("sample2".getBytes());
        BiometricRecord record = new BiometricRecord();
        record.setSegments(List.of(bir));
        dto.setSample(record);

        String s = utils.toString(dto);
        assertTrue(s.contains("\"_modelClass\": \"SegmentRequestDto\""));
        assertTrue(s.contains("\"modalitiesToSegment\""));
        assertTrue(s.contains("\"sample\""));
    }

    @Test
    void testToStringConvertFormatRequestDto() {
        ConvertFormatRequestDto dto = new ConvertFormatRequestDto();

        // Flags
        Map<String, String> flags = new HashMap<>();
        flags.put("flag3", "true");
        flags.put("flag1", "true");
        flags.put("flag2", "false");


        // Modalities
        dto.setModalitiesToConvert(List.of(BiometricType.FACE, BiometricType.FINGER));

        // Source and target formats
        dto.setSourceFormat("ISO");
        dto.setTargetFormat("ANSI");

        // Source and target params
        dto.setSourceParams(Map.of("param1", "value1"));
        dto.setTargetParams(Map.of("param2", "value2"));

        // Sample BiometricRecord
        BIR bir = new BIR();
        bir.setBdb("sample3".getBytes());
        BiometricRecord record = new BiometricRecord();
        record.setSegments(List.of(bir));
        dto.setSample(record);

        String s = utils.toString(dto);
        assertTrue(s.contains("\"_modelClass\": \"ConvertFormatRequestDto\""));
        assertTrue(s.contains("\"sourceFormat\""));
        assertTrue(s.contains("\"targetFormat\""));
        assertTrue(s.contains("\"sample\""));
    }

    @Test
    public void testToStringConvertFormatRequestDtoNull() {
        ConvertFormatRequestDto dto = null;

        String result = utils.toString(dto);

        assertEquals("null", result);
    }

    @Test
    public void testToStringSegmentRequestDtoNull() {
        SegmentRequestDto dto = null;

        String result = utils.toString(dto);

        assertEquals("null", result);
    }

    @Test
    public void testToStringCheckQualityRequestDtoNull() {
        CheckQualityRequestDto dto = null;

        String result = utils.toString(dto);

        assertEquals("null", result);
    }

    @Test
    public void testToStringInitRequestDtoNull() {
        InitRequestDto dto = null;

        String result = utils.toString(dto);

        assertEquals("null", result);
    }

    @Test
    public void testToStringMatchRequestDtoNull() {
        MatchRequestDto dto = null;

        String result = utils.toString(dto);

        assertEquals("null", result);
    }

    @Test
    public void testToStringExtractTemplateRequestDtoNull() {
        ExtractTemplateRequestDto dto = null;

        String result = utils.toString(dto);

        assertEquals("null", result);
    }

}

