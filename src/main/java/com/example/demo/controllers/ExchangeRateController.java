package com.example.demo.controllers;

import com.example.demo.entities.ExchangeRate;
import com.example.demo.services.ExchangeRateService;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping
@CrossOrigin(origins = "http://localhost:4200")
public class ExchangeRateController {

    private final ExchangeRateService exchangeRateService;

    public ExchangeRateController(ExchangeRateService exchangeRateService) {
        this.exchangeRateService = exchangeRateService;
    }

    @GetMapping("/getCodes")
    public List<String> getAllCurrencyCodes(){
        return exchangeRateService.getAllCurrencyCodes();
    }

    @GetMapping("/current/{currencyCode}")
    public ExchangeRate getCurrencyRate(@PathVariable String currencyCode){
        return exchangeRateService.getCurrencyRate(currencyCode);
    }

    @GetMapping("/current")
    public List<ExchangeRate> getCurrentRates() {
        return exchangeRateService.getCurrentRates();
    }

    @GetMapping("/history/{currencyCode}")
    public List<ExchangeRate> getHistoricalRates(
            @PathVariable String currencyCode,
            @RequestParam LocalDate from,
            @RequestParam LocalDate to) {
        return exchangeRateService.getHistoricalRates(currencyCode, from, to);
    }

}