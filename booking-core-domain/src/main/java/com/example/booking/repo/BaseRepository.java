package com.example.booking.repo;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation;
import org.springframework.data.repository.NoRepositoryBean;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;

@NoRepositoryBean
public interface BaseRepository<T, ID> extends JpaRepositoryImplementation<T, ID> {

	Long count(Function<CriteriaBuilder, CriteriaQuery<Long>> queryFun);
	<R> Page<R> findAll(Function<CriteriaBuilder, CriteriaQuery<R>> queryFun, Function<CriteriaBuilder, CriteriaQuery<Long>> countFun, int page, int size);
	<R> List<R> findAll(Function<CriteriaBuilder, CriteriaQuery<R>> queryFun);
	<R> List<R> findAll(Function<CriteriaBuilder, CriteriaQuery<R>> queryFun, int limit);
	<R> Optional<R> findOne(Function<CriteriaBuilder, CriteriaQuery<R>> queryFun);
	<R> Page<R> findAll(Function<CriteriaBuilder, CriteriaQuery<R>> queryFun, int page, int size);
}