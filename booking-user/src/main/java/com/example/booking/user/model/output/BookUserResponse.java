package com.example.booking.user.model.output;

import com.example.booking.entity.BookingUser;

public record BookUserResponse(
		Long id,
		String name,
		String email,
		String address) {

	public static BookUserResponse fromEntity(BookingUser entity) {
		return new BookUserResponse(
				entity.getId(),
				entity.getName(),
				entity.getEmail(),
				entity.getAddress());
	}
}
