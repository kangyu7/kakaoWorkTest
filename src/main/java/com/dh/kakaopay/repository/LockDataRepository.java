package com.dh.kakaopay.repository;

import org.springframework.data.repository.CrudRepository;

import com.dh.kakaopay.domain.LockData;

public interface LockDataRepository extends CrudRepository<LockData, String> {
}
