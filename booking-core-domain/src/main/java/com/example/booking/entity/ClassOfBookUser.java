package com.example.booking.entity;

import java.time.LocalDateTime;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "class_of_book_user")
@Data
public class ClassOfBookUser {

	@EmbeddedId
	private ClassOfBookUserPk id;
	private LocalDateTime bookedAt;
	private Long usedPackageId;
	private boolean isWait;
}
