package com.example.booking.pack.api;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.booking.pack.model.request.PackageSearchRequest;
import com.example.booking.pack.model.response.OwnPackageResponse;
import com.example.booking.pack.model.response.PackageResponse;
import com.example.booking.pack.service.PackageManagementService;
import com.example.booking.utils.ApiResponse;
import com.example.booking.utils.PageResponse;

@RestController
@RequestMapping("package")
public class PackageManagementApi {

	@Autowired
	private PackageManagementService service;
	
	@GetMapping
	public ApiResponse<PageResponse<PackageResponse>> getPackageList(
			PackageSearchRequest searchRequest,
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "5") int size) {
		return ApiResponse.createSuccessResponse(service.getPackageList(searchRequest, page, size), "Successfully Fetch Data!");
	}
	
	@GetMapping("own")
	public ApiResponse<PageResponse<OwnPackageResponse>> getOwnPackageList(
			Principal principal,
			PackageSearchRequest searchRequest,
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "5") int size) {
		return ApiResponse.createSuccessResponse(service.getOwnPackageList(principal, searchRequest, page, size), "Successfully Fetch Data!");
	}
	
	@PostMapping("buy")
	public ApiResponse<OwnPackageResponse> buyPackage(
			Principal principal,
			Long packageId) {
		
		return ApiResponse.createSuccessResponse( service.buyPackage(principal.getName(), packageId), "Successfully Bought Package.");
		
	}
}
