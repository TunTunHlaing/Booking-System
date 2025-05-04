package com.example.booking.pack.model.request;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import com.example.booking.entity.BookingUserPackage;
import com.example.booking.entity.Package;

public record PackageSearchRequest(
		Long id,
		String name,
		Integer totalCredit,
		Boolean isExpired
		) {

	public Predicate where(CriteriaBuilder cb, Root<Package> root) {
		
		List<Predicate> predicates = new ArrayList<Predicate>();
		
		if (id != null) {
			predicates.add(
					cb.equal(root.get("id"), id));
		}
		
		if (name != null) {
			predicates.add(
					cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%")
					);
		}
		
		if (totalCredit != null) {
			predicates.add(
					cb.equal(
							root.get("totalCredit"), totalCredit)
					);
		}
		
		if (isExpired != null) {
		    LocalDateTime now = LocalDateTime.now();
		    Path<LocalDateTime> endTimePath = root.get("endTime");

		    predicates.add(isExpired
		        ? cb.lessThanOrEqualTo(endTimePath, now)
		        : cb.greaterThanOrEqualTo(endTimePath, now));
		}

		
		return cb.and(predicates.toArray(size -> new Predicate[size]));
	}
	
	public Predicate whereForBookedPackages(CriteriaBuilder cb, Root<BookingUserPackage> root, String username) {
		
		List<Predicate> predicates = new ArrayList<Predicate>();
		var pack = root.join("pack");
		var user = root.join("bookUer");

		predicates.add(
				cb.equal(user.get("email"), username));
		
		if (id != null) {
			predicates.add(
					cb.equal(pack.get("id"), id));
		}
		
		if (name != null) {
			predicates.add(
					cb.like(cb.lower(pack.get("name")), "%" + name.toLowerCase() + "%")
					);
		}
		
		if (totalCredit != null) {
			predicates.add(
					cb.equal(
							pack.get("remainingCredit"), totalCredit)
					);
		}
		
		if (isExpired != null) {
		    LocalDateTime now = LocalDateTime.now();
		    Path<LocalDateTime> endTimePath = pack.get("endTime");

		    predicates.add(isExpired
		        ? cb.lessThanOrEqualTo(endTimePath, now)
		        : cb.greaterThanOrEqualTo(endTimePath, now));
		}

		
		return cb.and(predicates.toArray(size -> new Predicate[size]));
	}

}
