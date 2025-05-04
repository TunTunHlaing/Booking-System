package com.example.booking.utils;

public record ApiResponse<T>(
		boolean isSuccess,
		T payload,
		String message) {

	public static <T> ApiResponse<T> createSuccessResponse(T payload, String message){
		return new ApiResponse<T>(true, payload, message);
	}
	
	public static <T> ApiResponse<T> createFailedResponse(String message){
		return new ApiResponse<T>(false, null, message);
	}
	
	public static <T> ApiResponse<T> createFailedResponse(T payload, String message){
		return new ApiResponse<T>(false, payload, message);
	}
}
