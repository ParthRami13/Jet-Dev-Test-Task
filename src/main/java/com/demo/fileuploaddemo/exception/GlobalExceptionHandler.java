package com.demo.fileuploaddemo.exception;

import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.context.request.WebRequest;

import com.demo.fileuploaddemo.util.BaseController;
import com.demo.fileuploaddemo.util.ErrorResponse;

import io.jsonwebtoken.security.SignatureException;

@ControllerAdvice
public class GlobalExceptionHandler extends BaseController {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationErrors(MethodArgumentNotValidException exception, WebRequest webRequest) {
        String error = exception.getBindingResult().getFieldErrors()
                .stream().map(FieldError::getDefaultMessage).collect(Collectors.toList()).get(0);
        return error(error, HttpStatus.BAD_REQUEST, webRequest.getDescription(false));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException exception, WebRequest webRequest){
        return error("Unauthorized resource", HttpStatus.UNAUTHORIZED, webRequest.getDescription(false));
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthenticationException(AuthenticationException exception, WebRequest webRequest){
        return error("Unauthorized resource", HttpStatus.UNAUTHORIZED, webRequest.getDescription(false));
    }
    
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorizedException(UnauthorizedException exception, WebRequest webRequest){
        return error(exception.getMessage(), HttpStatus.UNAUTHORIZED, webRequest.getDescription(false));
    }
    
    @ExceptionHandler(ResourceAccessException.class)
    public ResponseEntity<ErrorResponse> handleResourceAccessException(ResourceAccessException exception, WebRequest webRequest){
        return error(exception.getMessage(), HttpStatus.UNAUTHORIZED, webRequest.getDescription(false));
    }
    
    @ExceptionHandler(InvalidLoginException.class)
    public ResponseEntity<ErrorResponse> handleInvalidLoginException(InvalidLoginException exception, WebRequest webRequest){
        return error(exception.getMessage(), HttpStatus.UNAUTHORIZED, webRequest.getDescription(false));
    }
    
    @ExceptionHandler(RecordAlreadyExistException.class)
    public ResponseEntity<ErrorResponse> handleRecordAlreadyExistException(RecordAlreadyExistException exception, WebRequest webRequest){
        return error(exception.getMessage(), HttpStatus.BAD_REQUEST, webRequest.getDescription(false));
    }
    
    @ExceptionHandler(MissingDataException.class)
    public ResponseEntity<ErrorResponse> handleMissingDataException(MissingDataException exception, WebRequest webRequest){
        return error(exception.getMessage(), HttpStatus.BAD_REQUEST, webRequest.getDescription(false));
    }

    @ExceptionHandler(SignatureException.class)
    public ResponseEntity<ErrorResponse> handleSignatureException(AuthenticationException exception, WebRequest webRequest){
        return error("Invalid token", HttpStatus.UNAUTHORIZED, webRequest.getDescription(false));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> runtimeException(RuntimeException exception, WebRequest webRequest){
        return error(exception.getMessage(), HttpStatus.BAD_REQUEST, webRequest.getDescription(false));
    }

}
