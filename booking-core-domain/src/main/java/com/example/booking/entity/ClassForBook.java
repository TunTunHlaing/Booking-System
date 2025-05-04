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
@Table(name = "class_for_book")
@Data
public class ClassForBook {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String name;
	@ManyToOne(fetch = FetchType.LAZY)
	private Country country;
	private int credit;
	private long limitedMembers;
	private LocalDateTime startTime;
	private LocalDateTime endTime;
	
}
