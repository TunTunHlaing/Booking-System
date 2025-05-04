package com.example.booking.exception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.example.booking.utils.ApiResponse;

import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class BookingSystemGlobalExceptionHandler {

	@ExceptionHandler(exception = BookSystemNotFoundException.class)
	@ResponseStatus(value = HttpStatus.BAD_REQUEST)
	public ApiResponse<ErrorResponse<String>> handleNotFoundException(BookSystemNotFoundException ex) {
		ex.printStackTrace();
		return ApiResponse.createFailedResponse(new ErrorResponse<String>(ErrorType.BUSINESS, ex.getMessage()), "Failed!!");
	}
	
	@ExceptionHandler(exception = BookingSystemException.class)
	@ResponseStatus(value = HttpStatus.FORBIDDEN)
	public ApiResponse<ErrorResponse<String>> handleNotFoundException(BookingSystemException ex) {
		ex.printStackTrace();
		log.error(ex.getMessage());
		return ApiResponse.createFailedResponse(new ErrorResponse<String>(ErrorType.BUSINESS, ex.getMessage()), "Failed!!");
	}
	
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ApiResponse<ErrorResponse<Map<String, String>>> handleValidationException(MethodArgumentNotValidException ex) {
    	
    	Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        
        return ApiResponse.createFailedResponse(
        		new ErrorResponse<Map<String, String>>(
        				ErrorType.VALIDATION, errors),
        		"Failed!!!");
	}
	
	@ExceptionHandler(exception = Exception.class)
	@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
	public ApiResponse<ErrorResponse<String>> handleNotFoundException(Exception ex) {
		ex.printStackTrace();
		return ApiResponse.createFailedResponse(new ErrorResponse<String>(ErrorType.INTERNAL, ex.getMessage()), "Failed!!");
	}
}
