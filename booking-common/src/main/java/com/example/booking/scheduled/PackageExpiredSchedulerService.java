package com.example.booking.scheduled;

import java.time.LocalDateTime;
import java.time.ZoneId;

import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PackageExpiredSchedulerService {

	@Autowired
	private Scheduler scheduler;
	
	public void schedulePackageExpireJob(Long bookPackageId, LocalDateTime endTime) throws SchedulerException {
        JobDetail jobDetail = JobBuilder.newJob(PackageExpiredJob.class)
                .withIdentity("expire_package_" + bookPackageId, "package-expire-jobs")
                .usingJobData("bookPackageId", bookPackageId)
                .build();

        Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity("expireTrigger_" + bookPackageId, "class-expire-triggers")
                .startAt(java.util.Date.from(endTime.atZone(ZoneId.systemDefault()).toInstant()))
                .build();

        scheduler.scheduleJob(jobDetail, trigger);
    }

    public void cancelJob(Long bookPackageId) throws SchedulerException {
        JobKey jobKey = new JobKey("expire_package_" + bookPackageId, "package-expire-jobs");
        scheduler.deleteJob(jobKey);
    }
}
