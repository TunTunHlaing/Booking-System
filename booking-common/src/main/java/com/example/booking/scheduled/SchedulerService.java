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
public class SchedulerService {
	
	@Autowired
	private Scheduler scheduler;
	
	public void scheduleClassExpireJob(Long classId, LocalDateTime endTime) throws SchedulerException {
        JobDetail jobDetail = JobBuilder.newJob(ClassExpireJob.class)
                .withIdentity("expireJob_" + classId, "class-expire-jobs")
                .usingJobData("classId", classId)
                .build();

        Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity("expireTrigger_" + classId, "class-expire-triggers")
                .startAt(java.util.Date.from(endTime.atZone(ZoneId.systemDefault()).toInstant()))
                .build();

        scheduler.scheduleJob(jobDetail, trigger);
    }

    public void cancelJob(Long classId) throws SchedulerException {
        JobKey jobKey = new JobKey("expireJob_" + classId, "class-expire-jobs");
        scheduler.deleteJob(jobKey);
    }

}
