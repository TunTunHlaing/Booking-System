package com.example.booking.repo;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;


public class BaseRepositoryImpl<T, ID> extends SimpleJpaRepository<T, ID> implements BaseRepository<T, ID>{
	
private EntityManager entityManager;
	
	public BaseRepositoryImpl(JpaEntityInformation<T, ?> entityInformation, EntityManager entityManager) {
		super(entityInformation, entityManager);
		this.entityManager = entityManager;
	}

	@Override
	public Long count(Function<CriteriaBuilder, CriteriaQuery<Long>> queryFun) {
		var criteriaQuery = queryFun.apply(entityManager.getCriteriaBuilder());
		var query = entityManager.createQuery(criteriaQuery);
		return query.getSingleResult();
	}

	@Override
	public <R> Page<R> findAll(Function<CriteriaBuilder, CriteriaQuery<R>> queryFun, Function<CriteriaBuilder, CriteriaQuery<Long>> countFun, int page, int size) {
		var count = count(countFun);
		var criteriaQuery = queryFun.apply(entityManager.getCriteriaBuilder());
		
		var query = entityManager.createQuery(criteriaQuery);
		query.setFirstResult(size * page);
		query.setMaxResults(size);
		var list = query.getResultList();
		
		return new PageImpl<R>(list, PageRequest.of(page, size), count);
	}

	@Override
	public <R> List<R> findAll(Function<CriteriaBuilder, CriteriaQuery<R>> queryFun) {
		var criteriaQuery = queryFun.apply(entityManager.getCriteriaBuilder());
		var query = entityManager.createQuery(criteriaQuery);
		return query.getResultList();
	}

	@Override
	public <R> Optional<R> findOne(Function<CriteriaBuilder, CriteriaQuery<R>> queryFun) {
		var criteriaQuery = queryFun.apply(entityManager.getCriteriaBuilder());
		var query = entityManager.createQuery(criteriaQuery);
		return Optional.ofNullable(query.getSingleResult());
	}

	@Override
	public <R> List<R> findAll(Function<CriteriaBuilder, CriteriaQuery<R>> queryFun, int limit) {
		var criteriaQuery = queryFun.apply(entityManager.getCriteriaBuilder());
		var query = entityManager.createQuery(criteriaQuery);
		query.setMaxResults(limit);
		return query.getResultList();
	}

	@Override
	public <R> Page<R> findAll(Function<CriteriaBuilder, CriteriaQuery<R>> queryFun, int page, int size) {
		var criteriaQuery = queryFun.apply(entityManager.getCriteriaBuilder());
		var query = entityManager.createQuery(criteriaQuery);
		var count = query.getResultList().size();
		query.setFirstResult(size * page);
		query.setMaxResults(size);
		var list = query.getResultList();
		return new PageImpl<R>(list, PageRequest.of(page, size), count);
	}
}
