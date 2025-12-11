package io.mosip.biosdk.services.test.controller;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.lang.reflect.Method;
import java.time.LocalDateTime;

import io.mosip.biosdk.services.controller.MainController;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;

import io.mosip.biosdk.services.dto.RequestDto;
import io.mosip.biosdk.services.exceptions.BioSDKException;
import io.mosip.biosdk.services.factory.BioSdkServiceFactory;
import io.mosip.biosdk.services.spi.BioSdkServiceProvider;
import io.mosip.biosdk.services.utils.Utils;

public class MainControllerTest {

    @Mock
    private Utils mockUtils;

    @Mock
    private BioSdkServiceFactory mockFactory;

    @Mock
    private BioSdkServiceProvider mockProvider;

    @Mock
    private Errors mockErrors;

    private MainController controller;

    @Before
    public void setup() {
        MockitoAnnotations.openMocks(this);
        controller = new MainController(mockUtils, mockFactory);

        when(mockUtils.getCurrentResponseTime()).thenReturn(LocalDateTime.now().toString());
    }

    // -------------------------------------------------------------------
    // GET /   STATUS
    // -------------------------------------------------------------------
    @Test
    public void testStatus() {
        ResponseEntity<String> response = controller.status();
        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody().contains("Service is running"));
    }

    // -------------------------------------------------------------------
    // GET /s (role-based) — same logic
    // -------------------------------------------------------------------
    @Test
    public void testStatus1() {
        ResponseEntity<String> response = controller.status1();
        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody().contains("Service is running"));
    }

    // -------------------------------------------------------------------
    // POST /init  SUCCESS FLOW
    // -------------------------------------------------------------------
    @Test
    public void testInitSuccess() {
        RequestDto req = new RequestDto();
        req.setVersion("1.0");

        when(mockFactory.getBioSdkServiceProvider("1.0")).thenReturn(mockProvider);
        when(mockProvider.init(req)).thenReturn("OK");

        ResponseEntity<String> resp = controller.init(req, mockErrors);

        assertEquals(200, resp.getStatusCodeValue());
        assertTrue(resp.getBody().contains("OK"));
    }

    // -------------------------------------------------------------------
    // POST /init  ERROR FLOW
    // -------------------------------------------------------------------
    @Test
    public void testInitFailure() {
        RequestDto req = new RequestDto();
        req.setVersion("1.0");

        when(mockFactory.getBioSdkServiceProvider("1.0")).thenReturn(mockProvider);
        when(mockProvider.init(req)).thenThrow(new BioSDKException("ERR01", "Init failed"));

        ResponseEntity<String> resp = controller.init(req, mockErrors);

        assertEquals(200, resp.getStatusCodeValue());
        assertTrue(resp.getBody().contains("ERR01"));
        assertTrue(resp.getBody().contains("Init failed"));
    }

    // -------------------------------------------------------------------
    // POST /match SUCCESS
    // -------------------------------------------------------------------
    @Test
    public void testMatchSuccess() {
        RequestDto req = new RequestDto();
        req.setVersion("1.0");

        when(mockFactory.getBioSdkServiceProvider("1.0")).thenReturn(mockProvider);
        when(mockProvider.match(req)).thenReturn("MATCH_OK");

        ResponseEntity<String> resp = controller.match(req, mockErrors);

        assertEquals(200, resp.getStatusCodeValue());
        assertTrue(resp.getBody().contains("MATCH_OK"));
    }

    // POST /match FAILURE
    @Test
    public void testMatchFailure() {
        RequestDto req = new RequestDto();
        req.setVersion("1.0");

        when(mockFactory.getBioSdkServiceProvider("1.0")).thenReturn(mockProvider);
        when(mockProvider.match(req)).thenThrow(new BioSDKException("ERRM", "Match failed"));

        ResponseEntity<String> resp = controller.match(req, mockErrors);

        assertTrue(resp.getBody().contains("ERRM"));
        assertTrue(resp.getBody().contains("Match failed"));
    }

    // -------------------------------------------------------------------
    // POST /check-quality SUCCESS
    // -------------------------------------------------------------------
    @Test
    public void testCheckQualitySuccess() {
        RequestDto req = new RequestDto();
        req.setVersion("1.0");

        when(mockFactory.getBioSdkServiceProvider("1.0")).thenReturn(mockProvider);
        when(mockProvider.checkQuality(req)).thenReturn("QUALITY_OK");

        ResponseEntity<String> resp = controller.checkQuality(req, mockErrors);
        assertTrue(resp.getBody().contains("QUALITY_OK"));
    }

    // FAILURE
    @Test
    public void testCheckQualityFailure() {
        RequestDto req = new RequestDto();
        req.setVersion("1.0");

        when(mockFactory.getBioSdkServiceProvider("1.0")).thenReturn(mockProvider);
        when(mockProvider.checkQuality(req))
                .thenThrow(new BioSDKException("ERRQ", "Quality failed"));

        ResponseEntity<String> resp = controller.checkQuality(req, mockErrors);
        assertTrue(resp.getBody().contains("ERRQ"));
    }

    // -------------------------------------------------------------------
    // POST /extract-template
    // -------------------------------------------------------------------
    @Test
    public void testExtractTemplateSuccess() {
        RequestDto req = new RequestDto();
        req.setVersion("1.0");

        when(mockFactory.getBioSdkServiceProvider("1.0")).thenReturn(mockProvider);
        when(mockProvider.extractTemplate(req)).thenReturn("TMP_OK");

        ResponseEntity<String> resp = controller.extractTemplate(req, mockErrors);
        assertTrue(resp.getBody().contains("TMP_OK"));
    }

    @Test
    public void testExtractTemplateFailure() {
        RequestDto req = new RequestDto();
        req.setVersion("1.0");

        when(mockFactory.getBioSdkServiceProvider("1.0")).thenReturn(mockProvider);
        when(mockProvider.extractTemplate(req))
                .thenThrow(new BioSDKException("ERRE", "Extract failed"));

        ResponseEntity<String> resp = controller.extractTemplate(req, mockErrors);
        assertTrue(resp.getBody().contains("ERRE"));
    }

    // -------------------------------------------------------------------
    // POST /convert-format
    // -------------------------------------------------------------------
    @Test
    public void testConvertFormatSuccess() {
        RequestDto req = new RequestDto();
        req.setVersion("1.0");

        when(mockFactory.getBioSdkServiceProvider("1.0")).thenReturn(mockProvider);
        when(mockProvider.convertFormat(req)).thenReturn("FORMAT_OK");

        ResponseEntity<String> resp = controller.convertFormat(req, mockErrors);
        assertTrue(resp.getBody().contains("FORMAT_OK"));
    }

    @Test
    public void testConvertFormatFailure() {
        RequestDto req = new RequestDto();
        req.setVersion("1.0");

        when(mockFactory.getBioSdkServiceProvider("1.0")).thenReturn(mockProvider);
        when(mockProvider.convertFormat(req))
                .thenThrow(new BioSDKException("ERRF", "Format failed"));

        ResponseEntity<String> resp = controller.convertFormat(req, mockErrors);
        assertTrue(resp.getBody().contains("ERRF"));
    }

    // -------------------------------------------------------------------
    // POST /segment
    // -------------------------------------------------------------------
    @Test
    public void testSegmentSuccess() {
        RequestDto req = new RequestDto();
        req.setVersion("1.0");

        when(mockFactory.getBioSdkServiceProvider("1.0")).thenReturn(mockProvider);
        when(mockProvider.segment(req)).thenReturn("SEG_OK");

        ResponseEntity<String> resp = controller.segment(req, mockErrors);
        assertTrue(resp.getBody().contains("SEG_OK"));
    }

    @Test
    public void testSegmentFailure() {
        RequestDto req = new RequestDto();
        req.setVersion("1.0");

        when(mockFactory.getBioSdkServiceProvider("1.0")).thenReturn(mockProvider);
        when(mockProvider.segment(req))
                .thenThrow(new BioSDKException("ERRS", "Segment failed"));

        ResponseEntity<String> resp = controller.segment(req, mockErrors);
        assertTrue(resp.getBody().contains("ERRS"));
    }

    // -------------------------------------------------------------------
    // PRIVATE getVersion(String) using reflection
    // -------------------------------------------------------------------
    @Test
    public void testGetVersionSuccess() throws Exception {
        Method method = MainController.class.getDeclaredMethod("getVersion", String.class);
        method.setAccessible(true);

        String version = (String) method.invoke(controller, "{\"version\":\"1.1\"}");
        assertEquals("1.1", version);
    }

    @Test
    public void testGetVersionFailure() throws Exception {
        Method method = MainController.class.getDeclaredMethod("getVersion", String.class);
        method.setAccessible(true);

        try {
            method.invoke(controller, "{bad_json");
            fail("Expected exception");
        } catch (Exception e) {
            assertTrue(e.getCause() instanceof BioSDKException);
        }
    }
}
