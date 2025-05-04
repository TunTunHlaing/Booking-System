package com.example.booking.pack.model.response;

import java.time.LocalDateTime;

import com.example.booking.entity.Package;

import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

public record PackageResponse(
		Long id, String name, 
		String country, int totalCredit, LocalDateTime startTime,
		LocalDateTime endTime) {

	public static void select(CriteriaQuery<PackageResponse> query, Root<Package> root) {

		query.multiselect(root.get("id"), 
				root.get("name"),
				root.get("country").get("name"), 
				root.get("totalCredit"),
				root.get("startTime"),
				root.get("endTime"));
	}
	

}
