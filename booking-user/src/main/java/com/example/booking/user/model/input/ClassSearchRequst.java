package com.example.booking.user.model.input;

import java.util.ArrayList;
import java.util.List;

import com.example.booking.entity.ClassForBook;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

public record ClassSearchRequst(
		Long id,
		String name,
		String country,
		Integer credit) {
	
	public Predicate where(CriteriaBuilder cb , Root<ClassForBook> root) {
		List<Predicate> predicates = new ArrayList<Predicate>();

		if (id != null) {
			predicates.add(
					cb.equal(root.get("id"), id)
					);
		}
		
		if (name != null) {
			predicates.add(
					cb.like(cb.lower(root.get("name")), name.toLowerCase())
					);
		}
		
		if (country != null) {
			predicates.add(
					cb.like(cb.lower(root.get("country").get("name")), name.toLowerCase())
					);
		}
		
		if (credit != null) {
			predicates.add(
					cb.lessThanOrEqualTo(root.get("credit"), credit)
			);
		}
		
		return cb.and(predicates.toArray(size -> new Predicate[size]));

	}
	

}
