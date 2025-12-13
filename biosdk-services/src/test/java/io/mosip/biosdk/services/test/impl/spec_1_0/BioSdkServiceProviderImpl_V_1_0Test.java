package io.mosip.biosdk.services.test.impl.spec_1_0;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;
import com.google.gson.*;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.biosdk.services.dto.RequestDto;
import io.mosip.biosdk.services.exceptions.BioSDKException;
import io.mosip.biosdk.services.impl.spec_1_0.BioSdkServiceProviderImpl_V_1_0;
import io.mosip.biosdk.services.impl.spec_1_0.dto.request.*;
import io.mosip.biosdk.services.utils.Utils;
import io.mosip.kernel.biometrics.constant.BiometricType;
import io.mosip.kernel.biometrics.entities.BiometricRecord;
import io.mosip.kernel.biometrics.model.MatchDecision;
import io.mosip.kernel.biometrics.model.QualityCheck;
import io.mosip.kernel.biometrics.model.SDKInfo;
import io.mosip.kernel.biometrics.spi.IBioApiV2;
import org.junit.Before;
import org.junit.Test;
import org.mockito.*;

import io.mosip.kernel.biometrics.model.Response;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.*;
import java.nio.charset.StandardCharsets;

public class BioSdkServiceProviderImpl_V_1_0Test {

    @Mock
    private IBioApiV2 mockBioApiV2;

    @Mock
    private Utils mockUtils;

    @InjectMocks
    private BioSdkServiceProviderImpl_V_1_0 bioSdkServiceProvider;

    @Mock
    private RequestDto mockRequestDto;

    @Mock
    private SDKInfo mockSdkInfo;

    private Gson gson;

    @Before
    public void setup() throws Exception {
        MockitoAnnotations.openMocks(this);

        gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class,
                        (JsonDeserializer<LocalDateTime>) (json, type, ctx) ->
                                LocalDateTime.parse(json.getAsString()))
                .serializeNulls()
                .create();

        bioSdkServiceProvider = new BioSdkServiceProviderImpl_V_1_0(mockBioApiV2, mockUtils);

        // Inject gson into service
        java.lang.reflect.Field gsonField = BioSdkServiceProviderImpl_V_1_0.class.getDeclaredField("gson");
        gsonField.setAccessible(true);
        gsonField.set(bioSdkServiceProvider, gson);
    }

    @Test
    public void testInit() {
        // Arrange: build a realistic InitRequestDto payload
        String json = "{ \"initParams\": { \"param1\": \"value1\", \"param2\": \"value2\" } }";

        // Encode JSON to Base64 (standard)
        String base64Request = Base64.getEncoder()
                .encodeToString(json.getBytes(StandardCharsets.UTF_8));

        when(mockRequestDto.getRequest()).thenReturn(base64Request);
        when(mockBioApiV2.init(any())).thenReturn(mockSdkInfo);

        // Act
        Object response = bioSdkServiceProvider.init(mockRequestDto);

        // Assert
        assertNotNull(response);
        assertEquals(mockSdkInfo, response);
        verify(mockBioApiV2).init(any());
    }


    @Test(expected = BioSDKException.class)
    public void testInitException() {
        // Arrange
        String base64Request = "mockBase64Request"; // Example Base64 request
        when(mockRequestDto.getRequest()).thenReturn(base64Request);
        when(mockBioApiV2.init(any())).thenThrow(new RuntimeException("SDK initialization failed")); // Simulate an exception

        // Act
        bioSdkServiceProvider.init(mockRequestDto); // This should throw a BioSDKException
    }

    /**
     * Helper: build a Base64-encoded JSON that matches CheckQualityRequestDto.
     * Adjust field names/types as per your actual DTO if needed.
     * Here: sample is a base64 string, modalities and flags are arrays of strings.
     */
    private String buildValidBase64CheckQualityRequestJson() {
        Gson localGson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class,
                        (JsonDeserializer<LocalDateTime>) (json, type, ctx) ->
                                LocalDateTime.parse(json.getAsString()))
                .create();

        // BiometricRecord sample
        BiometricRecord sampleRecord = new BiometricRecord();
        sampleRecord.getOthers().put("biometricData", "fakeBiometricBytes");

        // CheckQualityRequestDto
        CheckQualityRequestDto dto = new CheckQualityRequestDto();
        dto.setSample(sampleRecord);
        dto.setModalitiesToCheck(Arrays.asList(BiometricType.FINGER, BiometricType.FACE));

        // Correct flags map
        Map<String, String> flags = new HashMap<>();
        flags.put("QUALITY", "true");
        flags.put("LIVENESS", "true");
        dto.setFlags(flags);

        // Convert DTO to JSON and then Base64
        String json = localGson.toJson(dto);
        return Base64.getEncoder().encodeToString(json.getBytes(StandardCharsets.UTF_8));
    }

    @Test
    public void testCheckQualitySuccess() {
        // Arrange: valid Base64 → JSON
        String base64Request = buildValidBase64CheckQualityRequestJson();
        when(mockRequestDto.getRequest()).thenReturn(base64Request);

        // Mock SDK response
        Response<?> mockResponse = mock(Response.class);
        when(mockBioApiV2.checkQuality(any(), any(), any())).thenReturn((Response<QualityCheck>) mockResponse);

        // Act
        Object response = bioSdkServiceProvider.checkQuality(mockRequestDto);

        // Assert
        assertNotNull(response);
        assertEquals(mockResponse, response);
        verify(mockBioApiV2, times(1)).checkQuality(any(), any(), any());
    }

    @Test(expected = BioSDKException.class)
    public void testCheckQualityInvalidBase64ThrowsBioSDKException() {

        when(mockRequestDto.getRequest()).thenReturn("not_base64!");

        // Act
        bioSdkServiceProvider.checkQuality(mockRequestDto);

        // Assert handled by expected exception
    }

    @Test(expected = BioSDKException.class)
    public void testCheckQualitySdkThrowsWrappedAsBioSDKException() {
        // Arrange: valid Base64 → JSON
        String base64Request = buildValidBase64CheckQualityRequestJson();
        when(mockRequestDto.getRequest()).thenReturn(base64Request);

        // SDK throws a runtime exception
        when(mockBioApiV2.checkQuality(any(), any(), any()))
                .thenThrow(new RuntimeException("SDK failure"));

        // Act
        bioSdkServiceProvider.checkQuality(mockRequestDto);

        // Assert handled by expected exception
    }

    private String buildBase64MatchRequest() {
        MatchRequestDto dto = new MatchRequestDto();
        dto.setSample(new BiometricRecord());
        dto.setGallery(Collections.singletonList(new BiometricRecord()).toArray(new BiometricRecord[0]));
        dto.setModalitiesToMatch(Arrays.asList(BiometricType.FINGER));
        dto.setFlags(Collections.singletonMap("QUALITY", "true"));
        return Base64.getEncoder().encodeToString(gson.toJson(dto).getBytes(StandardCharsets.UTF_8));
    }

    @Test
    public void testMatchSuccess() {
        String base64 = buildBase64MatchRequest();
        when(mockRequestDto.getRequest()).thenReturn(base64);

        Response<?> mockResponse = mock(Response.class);
        when(mockBioApiV2.match(any(), any(), any(), any())).thenReturn((Response<MatchDecision[]>) mockResponse);

        Object response = bioSdkServiceProvider.match(mockRequestDto);

        assertEquals(mockResponse, response);
        verify(mockBioApiV2).match(any(), any(), any(), any());
    }

    @Test(expected = BioSDKException.class)
    public void testMatchSdkThrows() {
        String base64 = buildBase64MatchRequest();
        when(mockRequestDto.getRequest()).thenReturn(base64);
        when(mockBioApiV2.match(any(), any(), any(), any())).thenThrow(new RuntimeException());

        bioSdkServiceProvider.match(mockRequestDto);
    }

    private String buildBase64ExtractTemplateRequest() {
        ExtractTemplateRequestDto dto = new ExtractTemplateRequestDto();
        dto.setSample(new BiometricRecord());
        dto.setModalitiesToExtract(Arrays.asList(BiometricType.FACE));
        dto.setFlags(Collections.singletonMap("QUALITY", "true"));
        return Base64.getEncoder().encodeToString(gson.toJson(dto).getBytes(StandardCharsets.UTF_8));
    }

    @Test
    public void testExtractTemplateSuccess() {
        String base64 = buildBase64ExtractTemplateRequest();
        when(mockRequestDto.getRequest()).thenReturn(base64);

        Response<?> mockResponse = mock(Response.class);
        when(mockBioApiV2.extractTemplate(any(), any(), any())).thenReturn((Response<BiometricRecord>) mockResponse);

        Object response = bioSdkServiceProvider.extractTemplate(mockRequestDto);

        assertEquals(mockResponse, response);
        verify(mockBioApiV2).extractTemplate(any(), any(), any());
    }

    @Test(expected = BioSDKException.class)
    public void testExtractTemplateSdkThrows() {
        String base64 = buildBase64ExtractTemplateRequest();
        when(mockRequestDto.getRequest()).thenReturn(base64);
        when(mockBioApiV2.extractTemplate(any(), any(), any())).thenThrow(new RuntimeException());

        bioSdkServiceProvider.extractTemplate(mockRequestDto);
    }

    private String buildBase64SegmentRequest() {
        SegmentRequestDto dto = new SegmentRequestDto();
        dto.setSample(new BiometricRecord());
        dto.setModalitiesToSegment(Arrays.asList(BiometricType.FINGER, BiometricType.FACE));
        dto.setFlags(Collections.singletonMap("QUALITY", "true"));
        return Base64.getEncoder().encodeToString(gson.toJson(dto).getBytes(StandardCharsets.UTF_8));
    }

    @Test
    public void testSegmentSuccess() {
        String base64 = buildBase64SegmentRequest();
        when(mockRequestDto.getRequest()).thenReturn(base64);

        Response<?> mockResponse = mock(Response.class);
        when(mockBioApiV2.segment(any(), any(), any())).thenReturn((Response<BiometricRecord>) mockResponse);

        Object response = bioSdkServiceProvider.segment(mockRequestDto);

        assertEquals(mockResponse, response);
        verify(mockBioApiV2).segment(any(), any(), any());
    }

    @Test(expected = BioSDKException.class)
    public void testSegmentSdkThrows() {
        String base64 = buildBase64SegmentRequest();
        when(mockRequestDto.getRequest()).thenReturn(base64);
        when(mockBioApiV2.segment(any(), any(), any())).thenThrow(new RuntimeException());

        bioSdkServiceProvider.segment(mockRequestDto);
    }

    private String buildBase64ConvertFormatRequest() {
        ConvertFormatRequestDto dto = new ConvertFormatRequestDto();
        dto.setSample(new BiometricRecord());
        dto.setSourceFormat("ISO");
        dto.setTargetFormat("ANSI");
        dto.setSourceParams(Collections.singletonMap("p", "v"));
        dto.setTargetParams(Collections.singletonMap("p", "v"));
        dto.setModalitiesToConvert(Arrays.asList(BiometricType.FINGER));
        return Base64.getEncoder().encodeToString(gson.toJson(dto).getBytes(StandardCharsets.UTF_8));
    }

    @Test
    public void testConvertFormatSuccess() {
        String base64 = buildBase64ConvertFormatRequest();
        when(mockRequestDto.getRequest()).thenReturn(base64);

        Response<?> mockResponse = mock(Response.class);
        when(mockBioApiV2.convertFormatV2(any(), any(), any(), any(), any(), any())).thenReturn((Response<BiometricRecord>) mockResponse);

        Object response = bioSdkServiceProvider.convertFormat(mockRequestDto);

        assertEquals(mockResponse, response);
        verify(mockBioApiV2).convertFormatV2(any(), any(), any(), any(), any(), any());
    }

    @Test(expected = BioSDKException.class)
    public void testConvertFormatSdkThrows() {
        String base64 = buildBase64ConvertFormatRequest();
        when(mockRequestDto.getRequest()).thenReturn(base64);
        when(mockBioApiV2.convertFormatV2(any(), any(), any(), any(), any(), any())).thenThrow(new RuntimeException());

        bioSdkServiceProvider.convertFormat(mockRequestDto);
    }

    private void invokePrivate(String methodName, Class<?>[] paramTypes, Object... args) throws Exception {
        Method m = BioSdkServiceProviderImpl_V_1_0.class.getDeclaredMethod(methodName, paramTypes);
        m.setAccessible(true);
        m.invoke(bioSdkServiceProvider, args);
    }

    private void enableLoggingAndMockLogger() throws Exception {
        Field flag = BioSdkServiceProviderImpl_V_1_0.class.getDeclaredField("isLogRequestResponse");
        flag.setAccessible(true);
        flag.set(bioSdkServiceProvider, true);

        Logger mockLogger = mock(Logger.class);
        Field loggerField = BioSdkServiceProviderImpl_V_1_0.class.getDeclaredField("logger");
        loggerField.setAccessible(true);
        loggerField.set(bioSdkServiceProvider, mockLogger);

        this.mockedLogger = mockLogger;
    }

    private Logger mockedLogger;

    @Test
    public void testPrivateLogRequestMatch() throws Exception {

        Field flag = BioSdkServiceProviderImpl_V_1_0.class.getDeclaredField("isLogRequestResponse");
        flag.setAccessible(true);
        flag.set(bioSdkServiceProvider, true);

        Logger mockLogger = mock(Logger.class);
        Field loggerField = BioSdkServiceProviderImpl_V_1_0.class.getDeclaredField("logger");
        loggerField.setAccessible(true);
        loggerField.set(bioSdkServiceProvider, mockLogger);

        MatchRequestDto dto = new MatchRequestDto();
        when(mockUtils.toString(dto)).thenReturn("mock-value");

        Method m = BioSdkServiceProviderImpl_V_1_0.class.getDeclaredMethod("logRequest", MatchRequestDto.class);
        ((java.lang.reflect.Method) m).setAccessible(true);
        m.invoke(bioSdkServiceProvider, dto);

        verify(mockLogger).debug(any(), any(), eq("REQUEST:: MatchRequestDto"), eq("mock-value"));
    }

    @Test
    public void testLogRequestInitRequestDto() throws Exception {
        enableLoggingAndMockLogger();

        InitRequestDto dto = new InitRequestDto();
        when(mockUtils.toString(dto)).thenReturn("init-json");

        invokePrivate("logRequest",
                new Class[]{InitRequestDto.class}, dto);

        verify(mockedLogger).debug(any(), any(), eq("REQUEST:: InitRequestDto"), eq("init-json"));
    }

    @Test
    public void testLogRequestSegmentRequestDto() throws Exception {
        enableLoggingAndMockLogger();

        SegmentRequestDto dto = new SegmentRequestDto();
        when(mockUtils.toString(dto)).thenReturn("segment-json");

        invokePrivate("logRequest",
                new Class[]{SegmentRequestDto.class}, dto);

        verify(mockedLogger).debug(any(), any(), eq("REQUEST:: SegmentRequestDto"), eq("segment-json"));
    }

    @Test
    public void testLogRequestConvertFormatRequestDto() throws Exception {
        enableLoggingAndMockLogger();

        ConvertFormatRequestDto dto = new ConvertFormatRequestDto();
        when(mockUtils.toString(dto)).thenReturn("convert-json");

        invokePrivate("logRequest",
                new Class[]{ConvertFormatRequestDto.class}, dto);

        verify(mockedLogger).debug(any(), any(), eq("REQUEST:: ConvertFormatRequestDto"), eq("convert-json"));
    }

    @Test
    public void testLogRequestCheckQualityRequestDto() throws Exception {
        enableLoggingAndMockLogger();

        CheckQualityRequestDto dto = new CheckQualityRequestDto();
        when(mockUtils.toString(dto)).thenReturn("quality-json");

        invokePrivate("logRequest",
                new Class[]{CheckQualityRequestDto.class}, dto);

        verify(mockedLogger).debug(any(), any(), eq("REQUEST:: CheckQualityRequestDto"), eq("quality-json"));
    }

    @Test
    public void testLogBiometricRecord() throws Exception {
        enableLoggingAndMockLogger();

        BiometricRecord record = new BiometricRecord();
        when(mockUtils.toString(record)).thenReturn("bio-json");

        invokePrivate("logBiometricRecord",
                new Class[]{String.class, BiometricRecord.class},
                "Prefix: ", record);

        verify(mockedLogger).debug(any(), any(),
                eq("Prefix: bio-json"));
    }

    @Test
    public void testLogResponseWithBiometricRecord() throws Exception {
        enableLoggingAndMockLogger();

        BiometricRecord record = new BiometricRecord();
        Response<Object> response = mock(Response.class);

        when(response.getResponse()).thenReturn(record);
        when(mockUtils.toString(record)).thenReturn("bio-json");

        invokePrivate("logResponse",
                new Class[]{Response.class}, response);

        verify(mockedLogger).debug(any(), any(),
                eq("Response BiometricRecord: bio-json"));
    }

    @Test
    public void testInitShouldThrowBioSDKExceptionWhenIBioApiFails() {

        String json = "{ \"initParams\": {} }";

        RequestDto req = new RequestDto();
        req.setRequest(Base64.getEncoder().encodeToString(json.getBytes()));

        when(mockBioApiV2.init(any())).thenThrow(new RuntimeException("SDK failed"));

        BioSDKException ex = assertThrows(
                BioSDKException.class,
                () -> bioSdkServiceProvider.init(req)
        );

        assertTrue(ex.getMessage().contains("SDK failed"));
    }

}
