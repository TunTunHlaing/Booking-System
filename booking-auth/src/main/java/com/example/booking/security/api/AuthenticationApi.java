package com.example.booking.security.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.booking.security.service.AuthenticationService;
import com.example.booking.security.utils.LoginRequest;
import com.example.booking.security.utils.LoginResponse;
import com.example.booking.utils.ApiResponse;

import jakarta.validation.constraints.NotBlank;

@RestController
@RequestMapping("public")
public class AuthenticationApi {

	@Autowired
	private AuthenticationService service;
	
	@PostMapping("login")
	public ApiResponse<LoginResponse> login(@RequestBody @Validated LoginRequest loginRequest) {
		return ApiResponse.createSuccessResponse(service.login(loginRequest), "Successfully Logged in.");
	}
	
	@PostMapping("refresh")
	public ApiResponse<LoginResponse> refreshToken(
			@Validated @NotBlank(message="Token Can't Be Blank.") String refreshToken) {
		return ApiResponse.createSuccessResponse(service.refreshToken(refreshToken.trim()), "Successfully refresh token.");

	}
	
}
