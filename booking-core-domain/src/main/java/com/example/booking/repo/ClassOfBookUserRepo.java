package com.example.booking.repo;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.booking.entity.ClassOfBookUser;
import com.example.booking.entity.ClassOfBookUserPk;

public interface ClassOfBookUserRepo extends BaseRepository<ClassOfBookUser, ClassOfBookUserPk>{

	@Query("""
			SELECT COUNT(c.id) From ClassOfBookUser c where c.id.classId=:classId
			""")
	Long getCountByClassOfBookUserPkClassId(@Param("classId") Long classId);
	
}
