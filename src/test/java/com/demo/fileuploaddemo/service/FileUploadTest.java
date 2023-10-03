package com.demo.fileuploaddemo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.AuthenticationManager;

import com.demo.fileuploaddemo.dto.FileProgressResDto;
import com.demo.fileuploaddemo.entity.ExcelFileData;
import com.demo.fileuploaddemo.entity.ExcelFileMetadata;
import com.demo.fileuploaddemo.entity.User;
import com.demo.fileuploaddemo.exception.RecordNotFoundException;
import com.demo.fileuploaddemo.repository.ExcelFileDataRepository;
import com.demo.fileuploaddemo.repository.FileRepository;
import com.demo.fileuploaddemo.util.FileUploadStatus;

@SpringBootTest
public class FileUploadTest {
	
	@Autowired
	ApplicationContext applicationContext;
	
	@MockBean
	AuthenticationManager authenticationManager;
	
	@MockBean
	ExcelFileDataRepository excelFileDataRepository;
	
	@MockBean
	FileRepository fileRepository;
	
	@MockBean
	private AuthService authService;
	
	@Autowired
	private FileUploadService fileService;
	
	@Test
	public void testSaveFileSuccess() throws Exception {
		Resource resource = new ClassPathResource("test.xlsx");
        MockMultipartFile file = new MockMultipartFile("test.xlsx", "test.xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", resource.getInputStream().readAllBytes());
        
        User user = new User();
        user.setEmail("part@gmail.com");
        when(authService.getUserByUserName(anyString())).thenReturn(user);
        
        ExcelFileMetadata expectedExcelFileMetadata = new ExcelFileMetadata();
        expectedExcelFileMetadata.setFileName("test.xlsx");
        
        ExcelFileData excelFileData = new ExcelFileData();
        when(fileRepository.save(any())).thenReturn(expectedExcelFileMetadata);
        when(excelFileDataRepository.save(any())).thenReturn(excelFileData);
        
        ExcelFileMetadata actulExcelFileMetadata = fileService.saveFile(file, user.getEmail());
        
        assertEquals(expectedExcelFileMetadata.getFileName(), actulExcelFileMetadata.getFileName());
        
	}
	
	@Test
	public void testDeleteFileSuccess() throws Exception {
		ExcelFileMetadata expectedExcelFileMetadata = new ExcelFileMetadata();
		expectedExcelFileMetadata.setId(1L);
		expectedExcelFileMetadata.setFileName("test.xlsx");
        
        when(fileRepository.findById(anyLong())).thenReturn(Optional.of(expectedExcelFileMetadata));
        
        fileService.deleteFile(1L);
        
        verify(fileRepository, atLeast(1)).findById(anyLong());
        
	}
	
	@Test
	public void testDeleteFileRecordNotFoundException() throws Exception {
		when(fileRepository.findById(anyLong())).thenReturn(Optional.empty());
        
		assertThrows(RecordNotFoundException.class, () -> fileService.deleteFile(1L));
        
	}
	
	@Test
	public void testGetFileWithRecordSuccess() throws Exception {
		User user = new User();
        user.setEmail("part@gmail.com");
        when(authService.getUserByUserName(anyString())).thenReturn(user);
        
        ExcelFileMetadata expectedExcelFileMetadata = new ExcelFileMetadata();
        expectedExcelFileMetadata.setId(1L);
        expectedExcelFileMetadata.setFileName("test.xlsx");
        
        ExcelFileData excelFileData = new ExcelFileData();
        Set<ExcelFileData> fileRecords = new HashSet<ExcelFileData>();
        expectedExcelFileMetadata.setFileRecords(fileRecords);
        fileRecords.add(excelFileData);
        
        when(fileRepository.findById(anyLong())).thenReturn(Optional.of(expectedExcelFileMetadata));
        when(fileRepository.save(any())).thenReturn(expectedExcelFileMetadata);
        when(excelFileDataRepository.getExcelFileDataByFileId(anyLong())).thenReturn(fileRecords);
        
        ExcelFileMetadata actulExcelFileMetadata = fileService.getExcelFileWithRecords(expectedExcelFileMetadata.getId(), user.getEmail());
        assertEquals(expectedExcelFileMetadata.getId(), actulExcelFileMetadata.getId());
        assertEquals(expectedExcelFileMetadata.getFileName(), actulExcelFileMetadata.getFileName());
        assertEquals(expectedExcelFileMetadata.getFileRecords().size(), actulExcelFileMetadata.getFileRecords().size());
	}
	
	@Test
	public void testGetFileWithRecordRecordNotFoundException() throws Exception {
		when(fileRepository.findById(anyLong())).thenReturn(Optional.empty());
        
		assertThrows(RecordNotFoundException.class, () -> fileService.deleteFile(1L));
        
	}
	
	@Test
	public void testGetFileStatusSuccess() throws Exception {
		ExcelFileMetadata expectedExcelFileMetadata = new ExcelFileMetadata();
		expectedExcelFileMetadata.setId(1L);
		expectedExcelFileMetadata.setFileName("test.xlsx");
		expectedExcelFileMetadata.setStatus(FileUploadStatus.SUCCESS);
        
        when(fileRepository.findById(anyLong())).thenReturn(Optional.of(expectedExcelFileMetadata));
        
        FileProgressResDto fileProgressDto = fileService.getFileProgressStatus(1L);
        
        assertEquals(FileUploadStatus.SUCCESS, fileProgressDto.getStatus());
        
	}
	
	@Test
	public void testGetFileStatusRecordNotFoundException() throws Exception {
		when(fileRepository.findById(anyLong())).thenReturn(Optional.empty());
        
		assertThrows(RecordNotFoundException.class, () -> fileService.deleteFile(1L));
        
	}
	
}
