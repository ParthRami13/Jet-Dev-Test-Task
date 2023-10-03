package com.demo.fileuploaddemo.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.demo.fileuploaddemo.dto.FileProgressResDto;
import com.demo.fileuploaddemo.entity.ExcelFileMetadata;
import com.demo.fileuploaddemo.exception.InvalidFileFormatException;
import com.demo.fileuploaddemo.security.JwtTokenProvider;
import com.demo.fileuploaddemo.service.FileUploadService;
import com.demo.fileuploaddemo.util.BaseController;
import com.demo.fileuploaddemo.util.ExcelUtil;

import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/api/file")
public class FileController extends BaseController {

	@Autowired
	private FileUploadService fileUploadService;

	@Autowired
	private JwtTokenProvider jwtTokenProvider;

	@ApiOperation(value = "")
	@PostMapping(value = "/upload", consumes = { "multipart/form-data" })
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public ResponseEntity<ExcelFileMetadata> uploadFile(@RequestPart("file") MultipartFile file,
			@RequestHeader(name = "Authorization") String bearerToken) {
		String token = bearerToken.substring(7);
		if (ExcelUtil.hasExcelFormat(file)) {
			String userEmail = jwtTokenProvider.getUsername(token);
			ExcelFileMetadata savedFile = null;
			try {
				savedFile = fileUploadService.saveFile(file, userEmail);
			} catch (IOException e) {
				return error("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR, "/upload-file");
			}
			return success(savedFile, "File is upload");
		} else {
			throw new InvalidFileFormatException("Please upload proper excel file");
		}
	}

	@ApiOperation(value = "")
	@GetMapping("/status/{fileId}")
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public ResponseEntity<FileProgressResDto> progress(@PathVariable Long fileId) {
		return success(fileUploadService.getFileProgressStatus(fileId), "Progress of file uploading");
	}

	@ApiOperation(value = "")
	@DeleteMapping("/{fileId}")
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public ResponseEntity<Void> deleteFile(@PathVariable long fileId) {
		fileUploadService.deleteFile(fileId);
		return success();
	}

	@ApiOperation(value = "")
	@GetMapping("/{fileId}")
	@PreAuthorize("hasRole('ROLE_USER') ||hasRole('ROLE_ADMIN')")
	public ResponseEntity<ExcelFileMetadata> getFileRecords(@PathVariable long fileId,
			@RequestHeader(name = "Authorization") String bearerToken) {
		String token = bearerToken.substring(7);
		String userEmail = jwtTokenProvider.getUsername(token);
		return success(fileUploadService.getExcelFileWithRecords(fileId, userEmail), "List of file record");
	}

	@ApiOperation(value = "")
	@GetMapping("/")
	@PreAuthorize("hasRole('ROLE_USER') ||hasRole('ROLE_ADMIN')")
	public ResponseEntity<List<ExcelFileMetadata>> getUploadedFile() {
		return success(fileUploadService.getUploadedFiles(), "List of uploaded files");
	}
}
