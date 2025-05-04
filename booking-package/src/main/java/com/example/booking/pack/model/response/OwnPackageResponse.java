package com.example.booking.pack.model.response;

import java.time.LocalDateTime;

import com.example.booking.entity.BookingUserPackage;

import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

public record OwnPackageResponse(
		Long id, String name, 
		String country, int totalCredit, 
		LocalDateTime boughtAt,
		boolean isExpired) {

	public static void selectBookingPackage(CriteriaQuery<OwnPackageResponse> query, 
			Root<BookingUserPackage> root) {
		var pack = root.join("pack");
		query.multiselect(root.get("id"), 
				pack.get("name"),
				pack.get("country").get("name"), 
				root.get("remainingCredit"),
				root.get("issuedAt"),
				root.get("isExpired"));
	}
	
	public static OwnPackageResponse fromEntity (BookingUserPackage userPackage) {
		return new OwnPackageResponse(
				userPackage.getId(),
				userPackage.getPack().getName(),
				userPackage.getPack().getCountry().getName(),
				userPackage.getRemainingCredit(),
				userPackage.getIssuedAt(),
				userPackage.isExpired());
	}
	
}
