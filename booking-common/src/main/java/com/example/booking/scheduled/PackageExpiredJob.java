package com.example.booking.scheduled;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.booking.repo.BookingUserPackageRepo;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class PackageExpiredJob implements Job{
	
	@Autowired
	private BookingUserPackageRepo packageRepo;
	@Autowired
	private PackageExpiredSchedulerService schedulerService;

	@Transactional
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
        Long bookPackageId = context.getMergedJobDataMap().getLong("bookPackageId");
		var bookPackage = packageRepo.findById(bookPackageId)
				.orElse(null);
		log.info("Booked Package expired for id {}", bookPackageId);

		if (bookPackage != null) {
			bookPackage.setExpired(true);
			packageRepo.save(bookPackage);
		}
		
		try {
			schedulerService.cancelJob(bookPackageId);
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
	}

}
