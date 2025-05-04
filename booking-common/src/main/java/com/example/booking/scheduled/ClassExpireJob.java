package com.example.booking.scheduled;

import java.util.function.Function;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.booking.entity.BookingUserPackage;
import com.example.booking.entity.ClassOfBookUser;
import com.example.booking.repo.BookingUserPackageRepo;
import com.example.booking.repo.ClassForBookRepo;
import com.example.booking.repo.ClassOfBookUserRepo;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.transaction.Transactional;

@Component
public class ClassExpireJob implements Job{

	@Autowired
	private ClassForBookRepo classForBookRepo;
	@Autowired
	private ClassOfBookUserRepo classOfBookUserRepo;
	@Autowired
	private BookingUserPackageRepo userPackageRepo;
	@Autowired
	private SchedulerService schedulerService;
	
	@Transactional
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
        Long classId = context.getMergedJobDataMap().getLong("classId");
        
        var classForBook = classForBookRepo.findById(classId).get();
        
        Function<CriteriaBuilder, CriteriaQuery<ClassOfBookUser>> queryFun = cb -> {
        	var query = cb.createQuery(ClassOfBookUser.class);
        	var root = query.from(ClassOfBookUser.class);
        	
        	query.select(root)
        	.where(
        			cb.and(
        					cb.equal(root.get("id").get("classId"), classId),
                			cb.equal(root.get("isWait"), true)
                			)
        			);
        	
        	return query;
        };
        
        var bookedClassedList = classOfBookUserRepo.findAll(queryFun);

        
        var packageIdList = bookedClassedList.stream().map(c -> c.getUsedPackageId()).toList();
        
        Function<CriteriaBuilder, CriteriaQuery<BookingUserPackage>> queryFunForPackage = cb -> {
        	var query = cb.createQuery(BookingUserPackage.class);
        	var root = query.from(BookingUserPackage.class);
        	
        	query.select(root)
        	.where(
        			cb.and(
        					root.get("id").in(packageIdList),
                			cb.equal(root.get("isExpired"), false)
                			)
        			);
        	
        	return query;
        };
        
        var bookedPackageList = userPackageRepo.findAll(queryFunForPackage);
        
        for (BookingUserPackage userPackage : bookedPackageList) {
        	userPackage.setRemainingCredit(userPackage.getRemainingCredit() + classForBook.getCredit());
        }
        
        userPackageRepo.saveAll(bookedPackageList);
        try {
			schedulerService.cancelJob(classId);
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
	}

}
