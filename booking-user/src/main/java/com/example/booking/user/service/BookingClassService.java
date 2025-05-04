package com.example.booking.user.service;

import java.security.Principal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.booking.entity.BookingUser;
import com.example.booking.entity.BookingUserPackage;
import com.example.booking.entity.ClassForBook;
import com.example.booking.entity.ClassOfBookUser;
import com.example.booking.entity.ClassOfBookUserPk;
import com.example.booking.entity.WaitingBookClassUser;
import com.example.booking.entity.WaitingBookClassUserPk;
import com.example.booking.exception.BookSystemNotFoundException;
import com.example.booking.exception.BookingSystemException;
import com.example.booking.exception.ConcurrentTryException;
import com.example.booking.repo.BookingUserPackageRepo;
import com.example.booking.repo.BookingUserRepo;
import com.example.booking.repo.ClassForBookRepo;
import com.example.booking.repo.ClassOfBookUserRepo;
import com.example.booking.repo.WaitingBookClassUserRepo;
import com.example.booking.user.model.input.ClassSearchRequst;
import com.example.booking.user.model.output.BookClassResponse;
import com.example.booking.user.model.output.ClassResponse;
import com.example.booking.utils.ErrorMessageConstant;
import com.example.booking.utils.PageResponse;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.transaction.SystemException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class BookingClassService {

	@Autowired
	private ClassForBookRepo classForBookRepo;
	@Autowired
	private BookingUserRepo userRepo;
	@Autowired
	private BookingUserPackageRepo bookingUserPackageRepo;
	@Autowired
	private ClassOfBookUserRepo classOfBookUserRepo;
	@Autowired
	private WaitingBookClassUserRepo waitingBookClassUserRepo;
	@Autowired
	private RedissonClient redissionClient;

	@Transactional
	public BookClassResponse bookClass(Principal principal, Long classId, Long bookPackageId) {

		String lockKey = String.format("%s_%s_%s", "book", principal.getName(), classId);
		RLock lock = redissionClient.getLock(lockKey);

		try {
			boolean acquired = lock.tryLock(2, 5, TimeUnit.SECONDS);
			if (!acquired) {
				throw new ConcurrentTryException(ErrorMessageConstant.CONCURRENT_ERR_003);
			}
			var user = userRepo.findByEmail(principal.getName())
					.orElseThrow(() -> new BookSystemNotFoundException(ErrorMessageConstant.NOTFOUND_001));

			var classForBook = classForBookRepo.findById(classId)
					.orElseThrow(() -> new BookSystemNotFoundException(ErrorMessageConstant.NOTFOUND_001));

			validateClassForBook(classForBook);

			validateValidUserForBook(user, classForBook, classId);

			if (classForBook.getLimitedMembers() <= classOfBookUserRepo.getCountByClassOfBookUserPkClassId(classId)) {
				throw new BookingSystemException("Member full!");
			}

			var userPackage = bookingUserPackageRepo.findByIdAndExpiredFalse(bookPackageId)
					.orElseThrow(() -> new BookSystemNotFoundException(ErrorMessageConstant.NOTFOUND_001));
			
			validatePackage(user, classForBook, userPackage);
			log.info("Remaining Credit :: {}, Class Credit :: {}", userPackage.getRemainingCredit(), classForBook.getCredit());

			if (userPackage.getRemainingCredit() < classForBook.getCredit()) {
				throw new BookingSystemException("Credit Not Enough!");
			}
			userPackage.setRemainingCredit(userPackage.getRemainingCredit() - classForBook.getCredit());

			bookingUserPackageRepo.save(userPackage);

			var classOfBookUser = createClassOfBookUser(classId, user, userPackage, false);

			return BookClassResponse.fromEntity(classOfBookUserRepo.save(classOfBookUser), classForBook);
		} catch (ConcurrentTryException c) {
			throw c;
		} catch (Exception e) {
			throw new BookingSystemException(e.getMessage());
		} finally {
			if (lock.isHeldByCurrentThread()) {
				lock.unlock();
			}
		}

	}

	private void validatePackage(BookingUser user, ClassForBook classForBook, BookingUserPackage userPackage) {
		if (userPackage.getBookUer().getId() !=  user.getId()) {
			throw new BookingSystemException("You Can't Use Other User Package!");
		}
		
		if (userPackage.getPack().getCountry().getId() != classForBook.getCountry().getId()) {
			throw new BookingSystemException("You Can't Use Different Country Package!");
		}
	}

		private void validateClassForBook(ClassForBook classForBook){
		    LocalDateTime now = LocalDateTime.now();
		    if (classForBook.getStartTime().isBefore(now) || classForBook.getEndTime().isBefore(now)) {
		        throw new BookingSystemException(ErrorMessageConstant.BOOK_FOR_ERR_CLASS_001);
		    }
	
		}


	public PageResponse<ClassResponse> getClasses(ClassSearchRequst request, int page, int size) {

		Function<CriteriaBuilder, CriteriaQuery<ClassResponse>> queryFunction = cb -> {
			var query = cb.createQuery(ClassResponse.class);
			var root = query.from(ClassForBook.class);
			ClassResponse.select(query, root);
			request.where(cb, root);
			return query;
		};

		var res = classForBookRepo.findAll(queryFunction, page, size);

		return PageResponse.from(res);

	}

	@Transactional
	public BookClassResponse waitClass(String username, Long classId, Long bookPackageId) {

		String lockKey = String.format("%s_%s_%s", "wait", username, classId);
		RLock lock = redissionClient.getLock(lockKey);

		try {

			boolean acquired = lock.tryLock(2, 5, TimeUnit.SECONDS);
			if (!acquired) {
				throw new ConcurrentTryException(ErrorMessageConstant.CONCURRENT_ERR_003);
			}

			var user = userRepo.findByEmail(username)
					.orElseThrow(() -> new BookSystemNotFoundException(ErrorMessageConstant.NOTFOUND_001));

			var classForBook = classForBookRepo.findById(classId)
					.orElseThrow(() -> new BookSystemNotFoundException(ErrorMessageConstant.NOTFOUND_001));

			validateValidUserForBook(user, classForBook, classId);

			if (classForBook.getLimitedMembers() > classOfBookUserRepo.getCountByClassOfBookUserPkClassId(classId)) {
				throw new BookingSystemException("You Can Directly Booked For UnFull Class Not In Wait List!");
			}
			
			var waitUser = new WaitingBookClassUser();
			waitUser.setCreateTime(LocalDateTime.now());
			waitUser.setId(new WaitingBookClassUserPk(user.getId(), classId));

			waitUser = waitingBookClassUserRepo.save(waitUser);

			var userPackage = bookingUserPackageRepo.findByIdAndExpiredFalse(bookPackageId)
					.orElseThrow(() -> new BookSystemNotFoundException(ErrorMessageConstant.NOTFOUND_001));
			
			validatePackage(user, classForBook, userPackage);

			userPackage.setRemainingCredit(userPackage.getRemainingCredit() - classForBook.getCredit());

			bookingUserPackageRepo.save(userPackage);

			var classOfBookUser = createClassOfBookUser(classId, user, userPackage, true);
			
			classOfBookUserRepo.save(classOfBookUser);

			return BookClassResponse.fromEntity(classOfBookUser, classForBook);

		} catch (ConcurrentTryException c) {
			throw c;
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new BookingSystemException(ErrorMessageConstant.SYSTEM_001);

		} finally {
			if (lock.isHeldByCurrentThread()) {
				lock.unlock();
			}
		}

	}

	private ClassOfBookUser createClassOfBookUser(Long classId, BookingUser user, BookingUserPackage userPackage,
			boolean isWait) {
		var classOfBookUser = new ClassOfBookUser();
		classOfBookUser.setBookedAt(LocalDateTime.now());
		classOfBookUser.setId(new ClassOfBookUserPk(user.getId(), classId));
		classOfBookUser.setWait(isWait);
		classOfBookUser.setUsedPackageId(userPackage.getId());
		return classOfBookUser;
	}

	private void validateValidUserForBook(BookingUser user, ClassForBook classForBook, Long classId) {

		var classOfBookUser = classOfBookUserRepo.findById(new ClassOfBookUserPk(user.getId(), classId)).orElse(null);
		var waitingUser = waitingBookClassUserRepo.findById(new WaitingBookClassUserPk(user.getId(), classId))
				.orElse(null);

		if (classOfBookUser != null || waitingUser != null) {
			throw new RuntimeException("User Already Booked For This Class!");
		}

	}

	@Transactional
	public String cancleClass(String username, Long classId) {

		String lockKey = String.format("%s_%s_%s", "cancle", username, classId);
		RLock lock = redissionClient.getLock(lockKey);

		try {
			boolean acquired = lock.tryLock(2, 5, TimeUnit.SECONDS);
			if (!acquired) {
				throw new ConcurrentTryException(ErrorMessageConstant.CONCURRENT_ERR_003);
			}

			var user = userRepo.findByEmail(username)
					.orElseThrow(() -> new BookSystemNotFoundException(ErrorMessageConstant.NOTFOUND_001));

			var userClass = classOfBookUserRepo.findById(new ClassOfBookUserPk(user.getId(), classId))
					.orElseThrow(() -> new BookSystemNotFoundException(ErrorMessageConstant.NOTFOUND_001));

			var classForBook = classForBookRepo.findById(classId)
					.orElseThrow(() -> new BookSystemNotFoundException(ErrorMessageConstant.NOTFOUND_001));

			var userPackage = bookingUserPackageRepo.findById(userClass.getUsedPackageId())
					.orElseThrow(() -> new BookSystemNotFoundException(ErrorMessageConstant.NOTFOUND_001));

			validateCancle(classForBook, userPackage);

			if (userClass.isWait()) {
				var pk = new WaitingBookClassUserPk(user.getId(), classId);
				waitingBookClassUserRepo.deleteById(pk);
			} else {
				insertWaitUserToBookUser(classId);
				classOfBookUserRepo.deleteById(new ClassOfBookUserPk(user.getId(), classId));
			}
			classForBookRepo.save(classForBook);
			userPackage.setRemainingCredit(userPackage.getRemainingCredit() + classForBook.getCredit());
			bookingUserPackageRepo.save(userPackage);

			return "Successfully Cancled!";
		} catch (ConcurrentTryException c) {
			throw c;
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new BookingSystemException(e.getMessage());

		} finally {
			if (lock.isHeldByCurrentThread()) {
				lock.unlock();
			}
		}

	}

	private void insertWaitUserToBookUser(Long classId) {
		Function<CriteriaBuilder, CriteriaQuery<WaitingBookClassUser>> queryFun = cb -> {
		    var query = cb.createQuery(WaitingBookClassUser.class);
		    var root = query.from(WaitingBookClassUser.class);
		    query.select(root);
		    query.orderBy(cb.asc(root.get("createTime")));
		    return query;
		};

		var firstWaitUserOfClass = waitingBookClassUserRepo.findOne(queryFun);
		if (firstWaitUserOfClass.isPresent()) {
			var waitUser = userRepo.findById(firstWaitUserOfClass.get().getId().userId())
			.orElseThrow(() -> new BookSystemNotFoundException(ErrorMessageConstant.NOTFOUND_001));
			var bookOfClassForWaitUser = classOfBookUserRepo
					.findById(new ClassOfBookUserPk(waitUser.getId(), classId)).get();
			bookOfClassForWaitUser.setWait(false);
			classOfBookUserRepo.save(bookOfClassForWaitUser);
			waitingBookClassUserRepo.deleteById(firstWaitUserOfClass.get().getId());
		}
	}

	private void validateCancle(ClassForBook classForBook, BookingUserPackage userPackage) throws SystemException {
		if (userPackage.isExpired()) {
			throw new SystemException(ErrorMessageConstant.PACKAGE_ERROR_001);
		}

		if (shouldRefundCredit(classForBook.getStartTime(), LocalDateTime.now())) {
			throw new SystemException(ErrorMessageConstant.CLASS_CANCLE_ERROR);
		}
	}

	public boolean shouldRefundCredit(LocalDateTime classStartTime, LocalDateTime cancelTime) {
		Duration duration = Duration.between(cancelTime, classStartTime);
		return duration.toHours() <= 4;
	}

	public List<BookClassResponse> getOwnClass(
			String username) {
		
		var user = userRepo.findByEmail(username)
				.orElseThrow(() -> new BookSystemNotFoundException(ErrorMessageConstant.NOTFOUND_001));
		
		Function<CriteriaBuilder, CriteriaQuery<ClassOfBookUser>> queryFunction = cb -> {
			var query = cb.createQuery(ClassOfBookUser.class);
			var root = query.from(ClassOfBookUser.class);
			query.select(root)
			.where(
					cb.equal(root.get("id").get("bookUserId"), user.getId()));
			return query;
		};

		var res = classForBookRepo.findAll(queryFunction);
		
		if (res == null) {
			return null;
		}
		
		var classForBook = classForBookRepo.findById(res.getFirst().getId().classId()).get();
		return res.stream().map(bc -> BookClassResponse.fromEntity(bc, classForBook)).toList();
	}
}
