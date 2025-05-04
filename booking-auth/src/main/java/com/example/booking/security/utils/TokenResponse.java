package com.example.booking.security.utils;

import java.util.List;

public record TokenResponse(
		String username,
		TokenType type,
		List<String> authorities) {

}
