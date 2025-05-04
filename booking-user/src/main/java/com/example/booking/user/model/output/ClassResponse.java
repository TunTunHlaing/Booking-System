package com.example.booking.user.model.output;

import java.time.LocalDateTime;

import com.example.booking.entity.ClassForBook;

import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

public record ClassResponse(
		Long id,
		String className,
		String country,
		int credit,
		long limitedMembers,
		LocalDateTime startTime,
		LocalDateTime endTime) {

	public static void select (CriteriaQuery<ClassResponse> query, Root<ClassForBook> root) {
		query.multiselect(
				root.get("id"),
				root.get("name"),
				root.get("country").get("name"),
				root.get("credit"),
				root.get("limitedMembers"),
				root.get("startTime"),
				root.get("endTime")
				);
	}
}
