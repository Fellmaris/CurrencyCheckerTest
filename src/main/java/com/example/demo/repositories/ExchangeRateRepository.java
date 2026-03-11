package com.example.demo.repositories;

import com.example.demo.entities.ExchangeRate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface ExchangeRateRepository extends JpaRepository<ExchangeRate, UUID> {

    List<ExchangeRate> findByCurrencyCodeAndDateOfRateBetweenOrderByDateOfRateDesc(
            String currencyCode, LocalDate from, LocalDate to);

    @Query("SELECT er.dateOfRate FROM ExchangeRate er WHERE er.currencyCode = :currencyCode")
    List<LocalDate> findDatesByCurrencyCode(String currencyCode);

    boolean existsByCurrencyCodeAndDateOfRate(String currencyCode, LocalDate dateOfRate);

    @Query("SELECT DISTINCT e.currencyCode FROM ExchangeRate e ORDER BY e.currencyCode")
    List<String> findAllCurrencyCodes();

    ExchangeRate findTopByCurrencyCodeOrderByDateOfRateDesc(String currencyCode);

    @Query("SELECT e FROM ExchangeRate e WHERE (e.currencyCode, e.dateOfRate) IN " +
            "(SELECT e2.currencyCode, MAX(e2.dateOfRate) FROM ExchangeRate e2 GROUP BY e2.currencyCode)")
    List<ExchangeRate> findLatestRateForEachCurrency();
}
