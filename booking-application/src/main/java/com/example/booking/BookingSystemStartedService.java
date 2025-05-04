package com.example.booking;

import java.time.LocalDateTime;
import java.util.function.Function;

import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.booking.entity.ClassForBook;
import com.example.booking.repo.ClassForBookRepo;
import com.example.booking.scheduled.SchedulerService;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class BookingSystemStartedService {

	@Autowired
	private ClassForBookRepo classForBookRepo;
	@Autowired
	private SchedulerService schedulerService;
	
	@PostConstruct
	private void schedule() {
		
		Function<CriteriaBuilder, CriteriaQuery<ClassForBook>> queryFun = cb -> {
			var query = cb.createQuery(ClassForBook.class);
			var root = query.from(ClassForBook.class);
			query.select(root)
			.where(
					cb.greaterThanOrEqualTo(root.get("endTime"), LocalDateTime.now()));
			return query;
			
		};
		var classList = classForBookRepo.findAll(queryFun);
		
		log.info("Class count From Scheduler {}", classList.size());
		
		for (ClassForBook classForBook : classList) {
			try {
				schedulerService.scheduleClassExpireJob(classForBook.getId(), classForBook.getEndTime());
				log.info("Successfully scheduled for class {}",classForBook.getName() );
			} catch (SchedulerException e) {
				e.printStackTrace();
			}
		}
		
	}
	
}
