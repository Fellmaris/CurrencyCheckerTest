package com.example.demo.services;

import com.example.demo.entities.ExchangeRate;
import com.example.demo.repositories.ExchangeRateRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class ExchangeRateService {

    private final ExchangeRateRepository repository;

    public ExchangeRateService(ExchangeRateRepository repository) {
        this.repository = repository;
    }

    public List<ExchangeRate> getCurrentRates() {
        return repository.findLatestRateForEachCurrency();
    }

    public List<ExchangeRate> getHistoricalRates(String currencyCode,
                                                 LocalDate from,
                                                 LocalDate to) {
        return repository.findByCurrencyCodeAndDateOfRateBetweenOrderByDateOfRateDesc(
                currencyCode, from, to);
    }

    public List<String> getAllCurrencyCodes (){
        return repository.findAllCurrencyCodes();
    }

    public ExchangeRate getCurrencyRate(String currencyCode){
        return repository.findTopByCurrencyCodeOrderByDateOfRateDesc(currencyCode);
    }
}