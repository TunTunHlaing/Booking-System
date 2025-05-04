package com.example.booking.user.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.booking.user.model.input.BookUserCreateRequest;
import com.example.booking.user.model.input.ResetPasswordRequest;
import com.example.booking.user.model.output.BookUserResponse;
import com.example.booking.user.service.BookingUserManagementService;
import com.example.booking.utils.ApiResponse;

@RestController
@RequestMapping("public")
public class BookUserRegistrationApi {
	
	@Autowired
	private BookingUserManagementService managementService;
	
	@PostMapping
	public ApiResponse<BookUserResponse> createUser (@RequestBody @Validated BookUserCreateRequest createRequest) {
		var res = managementService.createUser(createRequest);
		return ApiResponse.createSuccessResponse(res, "Successfully created.");
	}
	
	@PutMapping("reset-pwd")
	public ApiResponse<String> createUser (@RequestBody @Validated ResetPasswordRequest request) {
		var res = managementService.resetPassword(request);
		return ApiResponse.createSuccessResponse(res, "Successfully created.");
	}

}
