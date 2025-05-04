package com.example.booking.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "booking_user_package")
@Data
public class BookingUserPackage {

//	@EmbeddedId
//	private BookingUserPackagePk id;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@ManyToOne(fetch = FetchType.LAZY)
	private Package pack;
	private int remainingCredit;
	private LocalDateTime issuedAt;
	private boolean isExpired;
	@ManyToOne(fetch = FetchType.LAZY)
	private BookingUser bookUer;
}
