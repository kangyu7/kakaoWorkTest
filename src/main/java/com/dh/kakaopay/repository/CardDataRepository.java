package com.dh.kakaopay.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.dh.kakaopay.domain.CardData;

public interface CardDataRepository extends CrudRepository<CardData, Long> {
	public List<CardData> findByinspNo(String inspNo);
	public Optional<CardData> findTopByinspNoOrderByIdDesc(String inspNo);
}
