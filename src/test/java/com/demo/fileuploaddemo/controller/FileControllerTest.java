package com.demo.fileuploaddemo.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.ResourceAccessException;

import com.demo.fileuploaddemo.dto.FileProgressResDto;
import com.demo.fileuploaddemo.entity.ExcelFileMetadata;
import com.demo.fileuploaddemo.service.FileUploadService;



@SpringBootTest(webEnvironment=SpringBootTest.WebEnvironment.DEFINED_PORT)
@ExtendWith(SpringExtension.class)
public class FileControllerTest {


	@MockBean
	private FileUploadService fileUploadService;
	
    private TestRestTemplate restTemplate;
    
    private final RestTemplateBuilder restTemplateBuilder;
    private String baseUrl = "http://localhost:8090";

    private String adminToken = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJwYXJ0aEBnbWFpbC5jb20iLCJpYXQiOjE2OTYyMzI0MTcsImV4cCI6MTY5NjgzNzIxN30.4onk8vW6yHN4Z4Ib3_IKttT1C58CZDpD29Qmb9ZdWd4";
    
    private String userToken = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJwYXJ0aDJAZ21haWwuY29tIiwiaWF0IjoxNjk2MjQ3MTE0LCJleHAiOjE2OTY4NTE5MTR9.Jplz0tw_5COJeQQBww-eprPFCqfNu-BZLaLrgLh2KYk";
    
    @Autowired
    public FileControllerTest(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplateBuilder = restTemplateBuilder;
        this.restTemplate = new TestRestTemplate(restTemplateBuilder);
    }
   

    @Test
    public void testSaveFileTestSuccess() throws Exception {
        
        HttpHeaders headers = createAuthBaarerTokenHeadersForAdmin();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        
        Resource resource = new ClassPathResource("test.xlsx");
        MockMultipartFile file = new MockMultipartFile("test.xlsx", "test.xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", resource.getInputStream().readAllBytes());

        
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", file.getResource());
        
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
        ExcelFileMetadata excelFileMetadata = new ExcelFileMetadata();
        when(fileUploadService.saveFile(any(), anyString())).thenReturn(excelFileMetadata);
        ResponseEntity<ExcelFileMetadata> response = restTemplate.exchange(
        		baseUrl + "/api/file/upload",
                HttpMethod.POST,
                requestEntity,
                ExcelFileMetadata.class
        );

        assertEquals(200, response.getStatusCodeValue());
    }
    
    @Test
    public void testFileUploadUnauthorized() throws Exception {
        
        HttpHeaders headers = createAuthBaarerTokenHeadersForUser();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        
        Resource resource = new ClassPathResource("test.xlsx");

        
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", resource);
        
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
        
        		
        assertThrowsExactly(ResourceAccessException.class, () -> restTemplate.exchange(
        		baseUrl + "/api/file/upload",
                HttpMethod.POST,
                requestEntity,
                ExcelFileMetadata.class
        ));
    }
    
    @Test
    public void testGetFileProgressByAdminTestSuccess() throws Exception {
        
        HttpHeaders headers = createAuthBaarerTokenHeadersForAdmin();
    
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(headers);

        FileProgressResDto fileProgress = new FileProgressResDto();
        when(fileUploadService.getFileProgressStatus(anyLong())).thenReturn(fileProgress);
        
        ResponseEntity<FileProgressResDto> response = restTemplate.exchange(
        		baseUrl + "/api/file/status/1",
                HttpMethod.GET,
                requestEntity,
                FileProgressResDto.class
        );

        assertEquals(200, response.getStatusCodeValue());
    }
    
    @Test
    public void testGetFileProgressByUSerUnauthorizedTestSuccess() throws Exception {
        
        HttpHeaders headers = createAuthBaarerTokenHeadersForUser();
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(headers);
        
        ResponseEntity<FileProgressResDto> response = restTemplate.exchange(
        		baseUrl + "/api/file/status/1",
                HttpMethod.GET,
                requestEntity,
                FileProgressResDto.class
        );
        
        assertEquals(401, response.getStatusCodeValue());
    }
    
    @Test
    public void testDeleteFileTestSuccess() throws Exception {
        
        HttpHeaders headers = createAuthBaarerTokenHeadersForAdmin();
    
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(headers);

        doNothing().when(fileUploadService).deleteFile(anyLong());
        
        ResponseEntity<Void> response = restTemplate.exchange(
        		baseUrl + "/api/file/1",
                HttpMethod.DELETE,
                requestEntity,
                Void.class
        );

        assertEquals(200, response.getStatusCodeValue());
    }
    
    @Test
    public void testDeleteFileUnauthorized() throws Exception {
        
        HttpHeaders headers = createAuthBaarerTokenHeadersForUser();
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(headers);
        
        ResponseEntity<Void> response = restTemplate.exchange(
			        		baseUrl + "/api/file/1",
			              HttpMethod.DELETE,
			              requestEntity,
			              Void.class
			      );
        
        assertEquals(401, response.getStatusCodeValue());
    }
    
    @Test
    public void testGetFileByAdminTestSuccess() throws Exception {
        
        HttpHeaders headers = createAuthBaarerTokenHeadersForAdmin();
    
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(headers);

        ExcelFileMetadata excelFileMetadata = new ExcelFileMetadata();
        List<ExcelFileMetadata> fileList = new ArrayList<ExcelFileMetadata>();
        when(fileUploadService.getUploadedFiles()).thenReturn(fileList);
        
        ResponseEntity<List<ExcelFileMetadata>> response = restTemplate.exchange(
        		baseUrl + "/api/file/1",
                HttpMethod.GET,
                requestEntity,
                new ParameterizedTypeReference<List<ExcelFileMetadata>>() {
                }
        );

        assertEquals(200, response.getStatusCodeValue());
    }
    
    @Test
    public void testGetUploadedFileByUserTestSuccess() throws Exception {
        
        HttpHeaders headers = createAuthBaarerTokenHeadersForUser();
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(headers);
        
        ExcelFileMetadata excelFileMetadata = new ExcelFileMetadata();
        List<ExcelFileMetadata> fileList = new ArrayList<ExcelFileMetadata>();
        when(fileUploadService.getUploadedFiles()).thenReturn(fileList);
        
        ResponseEntity<List<ExcelFileMetadata>> response = restTemplate.exchange(
        		baseUrl + "/api/file/1",
                HttpMethod.GET,
                requestEntity,
                new ParameterizedTypeReference<List<ExcelFileMetadata>>() {
                }
        );
        
        assertEquals(200, response.getStatusCodeValue());
    }
    
    @Test
    public void testGetFileRecordByAdminTestSuccess() throws Exception {
        
        HttpHeaders headers = createAuthBaarerTokenHeadersForAdmin();
    
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(headers);

        ExcelFileMetadata excelFileMetadata = new ExcelFileMetadata();
        when(fileUploadService.saveFile(any(), anyString())).thenReturn(excelFileMetadata);
        
        ResponseEntity<ExcelFileMetadata> response = restTemplate.exchange(
        		baseUrl + "/api/file",
                HttpMethod.GET,
                requestEntity,
                ExcelFileMetadata.class
        );

        assertEquals(200, response.getStatusCodeValue());
    }
    
    @Test
    public void testGetFileRecordByUserTestSuccess() throws Exception {
        
        HttpHeaders headers = createAuthBaarerTokenHeadersForUser();
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(headers);
        
        ExcelFileMetadata excelFileMetadata = new ExcelFileMetadata();
        when(fileUploadService.saveFile(any(), anyString())).thenReturn(excelFileMetadata);
        
        ResponseEntity<ExcelFileMetadata> response = restTemplate.exchange(
        		baseUrl + "/api/file",
                HttpMethod.GET,
                requestEntity,
                ExcelFileMetadata.class
        );
        
        assertEquals(200, response.getStatusCodeValue());
    }
    
    public HttpHeaders createAuthBaarerTokenHeadersForAdmin() {
    	HttpHeaders headers = new HttpHeaders();
    	headers.add("Authorization", "Bearer " +  adminToken);
    	return headers;
    }
    
    public HttpHeaders createAuthBaarerTokenHeadersForUser() {
    	HttpHeaders headers = new HttpHeaders();
    	headers.add("Authorization", "Bearer " + userToken);
    	return headers;
    }
}
