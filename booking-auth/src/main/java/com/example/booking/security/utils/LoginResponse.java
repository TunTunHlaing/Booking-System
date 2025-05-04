package com.example.booking.security.utils;

public record LoginResponse(
		String accessToken,
		String refreshToken) {

}
