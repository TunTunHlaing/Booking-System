package com.example.booking.user.model.output;

import java.time.LocalDateTime;

import com.example.booking.entity.ClassForBook;
import com.example.booking.entity.ClassOfBookUser;
import com.example.booking.entity.ClassOfBookUserPk;

public record BookClassResponse(
		ClassOfBookUserPk bookedId,
		Long classId,
		String className,
		LocalDateTime startTime,
		LocalDateTime endTime,
		boolean isWait) {

	public static BookClassResponse fromEntity(ClassOfBookUser classOfBookUser,
			ClassForBook classForBook) {
		return new BookClassResponse(
				classOfBookUser.getId(), 
				classOfBookUser.getId().classId(),
				classForBook.getName(),
				classForBook.getStartTime(),
				classForBook.getEndTime(),
				classOfBookUser.isWait());
	}
}
