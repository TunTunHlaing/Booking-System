package com.example.booking.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.booking.entity.BookingUserPackage;

public interface BookingUserPackageRepo extends BaseRepository<BookingUserPackage, Long>{

	@Query("""
			SELECT b FROM BookingUserPackage b where b.id=:id AND b.isExpired=false
			""")
	public Optional<BookingUserPackage> findByIdAndExpiredFalse(@Param("id") Long id);
}
