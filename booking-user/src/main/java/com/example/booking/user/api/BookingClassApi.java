package com.example.booking.user.api;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.booking.user.model.input.ClassSearchRequst;
import com.example.booking.user.model.output.BookClassResponse;
import com.example.booking.user.model.output.ClassResponse;
import com.example.booking.user.service.BookingClassService;
import com.example.booking.utils.ApiResponse;
import com.example.booking.utils.PageResponse;

@RestController
@RequestMapping("booking-class")
public class BookingClassApi {

	@Autowired
	private BookingClassService bookingClassService;

	@GetMapping
	public ApiResponse<PageResponse<ClassResponse>> getClassList(ClassSearchRequst searchRequest,
		@RequestParam(defaultValue = "0") int page,
		@RequestParam(defaultValue = "5") int size) {
		return ApiResponse.createSuccessResponse(bookingClassService.getClasses(searchRequest, page, size),
				"Successfully Fetch Data.");
	}
	
	@GetMapping("own")
	public ApiResponse<List<BookClassResponse>> getOwnClassList(
			Principal principal) {
		return ApiResponse.createSuccessResponse(bookingClassService.getOwnClass(principal.getName()),
				"Successfully Fetch Data.");
	}
	
	@PostMapping("book")
	public ApiResponse<BookClassResponse> bookClass(
			Principal principal,
			Long classId,
			Long bookedPackageId){
		return ApiResponse.createSuccessResponse(bookingClassService.bookClass(principal, classId, bookedPackageId),
				"Successfully Created Data.");
	}
	
	
	@PostMapping("wait")
	public ApiResponse<BookClassResponse> bookClassInWaitList(
			Principal principal,
			Long classId,
			Long bookedPackageId){
		return ApiResponse.createSuccessResponse(bookingClassService.waitClass(principal.getName(), classId, bookedPackageId),
				"Successfully Created Data.");
	}
	
	@PostMapping("cancle")
	public ApiResponse<String> cancleClass(
			Principal principal,
			Long classId){
		return ApiResponse.createSuccessResponse(bookingClassService.cancleClass(principal.getName(), classId),
				"Successfully Cancled.");
	}
	
}
