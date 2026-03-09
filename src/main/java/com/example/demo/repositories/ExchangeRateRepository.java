package com.example.demo.repositories;

import com.example.demo.entities.ExchangeRate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface ExchangeRateRepository extends JpaRepository<ExchangeRate, UUID> {

    List<ExchangeRate> findByDateOfRate(LocalDate date);

    List<ExchangeRate> findByCurrencyCodeOrderByDateOfRateAsc(String currencyCode);

    boolean existsByDateOfRate(LocalDate date);

    ExchangeRate findTopByOrderByDateOfRateDesc();

}
