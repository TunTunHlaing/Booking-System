package com.example.booking.repo;

import java.util.Optional;

import com.example.booking.entity.BookingUser;


public interface BookingUserRepo extends BaseRepository<BookingUser, Long>{

	Optional<BookingUser> findByEmail(String email);
}
