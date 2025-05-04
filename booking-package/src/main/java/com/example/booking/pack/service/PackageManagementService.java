package com.example.booking.pack.service;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.function.Function;

import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.booking.entity.BookingUserPackage;
import com.example.booking.entity.Package;
import com.example.booking.exception.BookSystemNotFoundException;
import com.example.booking.pack.model.request.PackageSearchRequest;
import com.example.booking.pack.model.response.OwnPackageResponse;
import com.example.booking.pack.model.response.PackageResponse;
import com.example.booking.repo.BookingUserPackageRepo;
import com.example.booking.repo.BookingUserRepo;
import com.example.booking.repo.PackageRepo;
import com.example.booking.scheduled.PackageExpiredSchedulerService;
import com.example.booking.utils.ErrorMessageConstant;
import com.example.booking.utils.PageResponse;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class PackageManagementService {

	@Autowired
	private PackageRepo packageRepo;
	@Autowired
	private BookingUserRepo userRepo;
	@Autowired
	private BookingUserPackageRepo userPackageRepo;
	@Autowired
	private PackageExpiredSchedulerService packageSchedulerService;

	public PageResponse<PackageResponse> getPackageList(PackageSearchRequest searchRequest, int page, int size) {

		Function<CriteriaBuilder, CriteriaQuery<PackageResponse>> queryFunction = cb -> {
			var query = cb.createQuery(PackageResponse.class);
			var root = query.from(Package.class);
			PackageResponse.select(query, root);
			query.where(searchRequest.where(cb, root));
			return query;
		};

		var resPage = packageRepo.findAll(queryFunction, page, size);
		return PageResponse.from(resPage);
	}

	public PageResponse<OwnPackageResponse> getOwnPackageList(
			Principal principal,
			PackageSearchRequest searchRequest, int page, int size) {

		Function<CriteriaBuilder, CriteriaQuery<OwnPackageResponse>> queryFunction = cb -> {
			var query = cb.createQuery(OwnPackageResponse.class);
			var root = query.from(BookingUserPackage.class);
			OwnPackageResponse.selectBookingPackage(query, root);
			query.where(searchRequest.whereForBookedPackages(cb, root, principal.getName()));
			return query;
		};

		var resPage = packageRepo.findAll(queryFunction, page, size);
		return PageResponse.from(resPage);
	}

	public OwnPackageResponse buyPackage(String name, Long packageId) {
		var pack = packageRepo.findById(packageId)
				.orElseThrow(() -> new BookSystemNotFoundException(ErrorMessageConstant.NOTFOUND_001));
		var user = userRepo.findByEmail(name)
				.orElseThrow(() -> new BookSystemNotFoundException(ErrorMessageConstant.NOTFOUND_001));
		var ownPackage = new BookingUserPackage();
		ownPackage.setBookUer(user);
		ownPackage.setPack(pack);
		ownPackage.setRemainingCredit(pack.getTotalCredit());
		ownPackage.setIssuedAt(LocalDateTime.now());
		ownPackage.setExpired(false);
		var entity = userPackageRepo.saveAndFlush(ownPackage);
		try {
			packageSchedulerService.schedulePackageExpireJob(entity.getId(), pack.getEndTime());
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
		log.info("Successfully save for book user package with id {}", entity.getId());
		return OwnPackageResponse.fromEntity(entity);
	}
}
