package com.example.booking.entity;

import jakarta.persistence.Embeddable;

@Embeddable
public record WaitingBookClassUserPk(
		Long userId,
		Long classId) {

}
