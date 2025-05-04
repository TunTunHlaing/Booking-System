package com.example.booking.exception;

public record ErrorResponse<T>(
		ErrorType type,
		T data) {

}
