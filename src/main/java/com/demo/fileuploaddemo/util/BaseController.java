package com.demo.fileuploaddemo.util;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Date;

public class BaseController<T> {
    protected ResponseEntity<SuccessResponse> success(T data, String message) {
        SuccessResponse<T> successResponse = new SuccessResponse<T>();
        successResponse.setData(data);
        successResponse.setMessage(message);
        return new ResponseEntity<SuccessResponse>(successResponse, HttpStatus.OK);
    }
    
    protected ResponseEntity<SuccessResponse> success(String message) {
        SuccessResponse<T> successResponse = new SuccessResponse<T>();
        successResponse.setMessage(message);
        return new ResponseEntity<SuccessResponse>(HttpStatus.OK);
    }
    
    protected ResponseEntity<SuccessResponse> success() {
        return new ResponseEntity<SuccessResponse>(HttpStatus.OK);
    }

    protected ResponseEntity<ErrorResponse> error(String message, HttpStatus httpStatus, String path) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setTimeStamp(new Date());
        errorResponse.setMessage(message);
        errorResponse.setStatusCode(httpStatus.value());
        errorResponse.setPath(path);
        return new ResponseEntity<>(errorResponse, httpStatus);
    }
}
