package com.demo.fileuploaddemo.service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.demo.fileuploaddemo.dto.FileProgressResDto;
import com.demo.fileuploaddemo.entity.ExcelFileData;
import com.demo.fileuploaddemo.entity.ExcelFileMetadata;
import com.demo.fileuploaddemo.entity.User;
import com.demo.fileuploaddemo.exception.RecordNotFoundException;
import com.demo.fileuploaddemo.repository.ExcelFileDataRepository;
import com.demo.fileuploaddemo.repository.FileRepository;
import com.demo.fileuploaddemo.util.FileUploadStatus;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class FileUploadService {
    @Autowired
    private AuthService authService;

    @Autowired
    private FileRepository fileRepository;
    
    @Autowired
    private ExcelFileDataRepository excelFileDataRepository;

    public ExcelFileMetadata saveFile(MultipartFile file, String userEmail) throws IOException {
        User user = authService.getUserByUserName(userEmail);

        ExcelFileMetadata excelFileMetadata = new ExcelFileMetadata();
        excelFileMetadata.setFileName(file.getName());
        excelFileMetadata.setLastAccessOn(LocalDateTime.now());
        excelFileMetadata.setLastReviewedBy(user);
        excelFileMetadata.setStatus(FileUploadStatus.IN_PROGRESS);
        excelFileMetadata = fileRepository.save(excelFileMetadata);
        
        processAnsStoreExcelSheet(file, excelFileMetadata);
        
        return excelFileMetadata;
    }

    public List<ExcelFileMetadata> getUploadedFiles() {
        return fileRepository.findAll();
    }
    
    @Async
    public void processAnsStoreExcelSheet(MultipartFile file, ExcelFileMetadata excelFileMetadata) throws IOException {
    	String headerAsJson = "";
    	String rowsAsJson = "";
    	try(Workbook workbook = WorkbookFactory.create(file.getInputStream());) {
	        Sheet sheet = workbook.getSheetAt(0); 
	        headerAsJson = extractHeaderAsJson(sheet.getRow(0));
	        rowsAsJson = extractRowsAsJson(getExcelRowsWithoutHeader(sheet));
    	} catch (Exception e) {
    		throw new RuntimeException("Error while processing file");
    		
    	} finally {
    		excelFileMetadata.setLastAccessOn(LocalDateTime.now());
    		excelFileMetadata.setStatus(FileUploadStatus.ERROR);
        	fileRepository.save(excelFileMetadata);
		}
    	
    	ExcelFileData excelFileData = new ExcelFileData();
        excelFileData.setFile(excelFileMetadata);
        excelFileData.setHeader(headerAsJson);
        excelFileData.setData(rowsAsJson);
        excelFileDataRepository.save(excelFileData);
        excelFileMetadata.setLastAccessOn(LocalDateTime.now());
        excelFileMetadata.setStatus(FileUploadStatus.SUCCESS);
        fileRepository.save(excelFileMetadata);
    }
    
    private List<List<String>> getExcelRowsWithoutHeader(Sheet sheet) {
    	List<List<String>> excelRows = new ArrayList<>();
        for (Row row : sheet) {
            if (row.getRowNum() == 0) {
                continue; // Skip the header row
            }

            List<String> rowData = new ArrayList<>();
            for (Cell cell : row) {
                rowData.add(cell.toString());
            }
            excelRows.add(rowData);
        }
        return excelRows;
    }
    
    private String extractHeaderAsJson(Row headerRow) throws JsonProcessingException {
        List<String> header = new ArrayList<>();
        for (Cell cell : headerRow) {
            header.add(cell.toString());
        }
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(header);
    }

    private String extractRowsAsJson(List<List<String>> excelRows) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(excelRows);
    }

    public void deleteFile(long id) {
    	ExcelFileMetadata file = fileRepository.findById(id).orElseThrow(()->new RecordNotFoundException("No record found for file id : " + id));
        fileRepository.delete(file);
    }

    
    public ExcelFileMetadata getExcelFileWithRecords(long fileId, String userEmail) {
        User user = authService.getUserByUserName(userEmail);
        ExcelFileMetadata fileDetail = fileRepository.findById(fileId)
        											.orElseThrow(()->new RecordNotFoundException("Invalid file id"));
		
		fileDetail.setLastAccessOn(LocalDateTime.now()); 
		fileDetail.setLastReviewedBy(user);
		fileRepository.save(fileDetail); 
		
		fileDetail.setFileRecords(excelFileDataRepository.getExcelFileDataByFileId(fileId));		
        return fileDetail;
    }

    public FileProgressResDto getFileProgressStatus(Long fileId) {
    	ExcelFileMetadata fileDetails = fileRepository.findById(fileId).orElseThrow(()->new RecordNotFoundException("Invalid file id"));
        FileProgressResDto fileProgressResDto = new FileProgressResDto(fileDetails.getStatus());
        return fileProgressResDto;
    }

}
