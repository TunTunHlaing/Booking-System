package com.example.booking.entity;

import java.time.LocalDateTime;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "waiting_book_class_user")
@Data
public class WaitingBookClassUser {

	@EmbeddedId
	private WaitingBookClassUserPk id;
	private LocalDateTime createTime;
	private Long usedPackageId;
}
