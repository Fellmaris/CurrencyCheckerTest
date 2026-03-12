package com.example.demo.services;

import com.example.demo.entities.ExchangeRate;
import com.example.demo.repositories.ExchangeRateRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class LtBankAPIService {

    private static final String BASE_URL = "https://www.lb.lt/webservices/FxRates/FxRates.asmx";
    private final RestTemplate restTemplate = new RestTemplate();
    private final ExchangeRateRepository repository;

    public LtBankAPIService(ExchangeRateRepository repository) {
        this.repository = repository;
    }

    @PostConstruct
    public void onStartup() {
        new Thread(() -> {
            System.out.println("[INFO] Background job fetchDaily() started. Fetching exchange rates...");
            fetchDaily();
            System.out.println("[INFO] Background job fetchDaily() completed");
        }).start();
    }

    @Scheduled(cron = "0 0 17 * * *")
    public void fetchDaily() {
        try {
            fetchAndSaveCurrentRates();
            fillMissingHistoricalRates();
            System.out.println("[INFO] Scheduled job fetchDaily() succeeded");
        } catch (Exception e) {
            System.out.println("[ERROR] Scheduled job fetchDaily() failed: " +  e.getMessage());
        }
    }

    private void fetchAndSaveCurrentRates() {
        String url = BASE_URL + "/getCurrentFxRates?tp=EU";
        String xml = restTemplate.getForObject(url, String.class);
        List<ExchangeRate> rates = parseXml(xml);
        for (ExchangeRate rate : rates) {
            if (!repository.existsByCurrencyCodeAndDateOfRate(
                    rate.getCurrencyCode(), rate.getDateOfRate())) {
                repository.save(rate);
            }
        }
    }

    private void fillMissingHistoricalRates() throws InterruptedException {
        LocalDate from = LocalDate.of(2014, 9, 30);
        LocalDate to = LocalDate.now();
        String currentXml = restTemplate.getForObject(
                BASE_URL + "/getCurrentFxRates?tp=EU", String.class);
        List<ExchangeRate> currentRates = parseXml(currentXml);
        for (ExchangeRate current : currentRates) {
            Thread.sleep(1000);
            String ccy = current.getCurrencyCode();
            List<LocalDate> existingDates = repository
                    .findDatesByCurrencyCode(ccy);
            Set<LocalDate> existingDateSet = new HashSet<>(existingDates);
            String historyUrl = String.format(
                    "%s/getFxRatesForCurrency?tp=EU&ccy=%s&dtFrom=%s&dtTo=%s",
                    BASE_URL, ccy, from, to
            );
            String xml = restTemplate.getForObject(historyUrl, String.class);
            List<ExchangeRate> historicalRates = parseXml(xml);
            List<ExchangeRate> newRates = historicalRates.stream()
                    .filter(rate -> !existingDateSet.contains(rate.getDateOfRate()))
                    .toList();
            if (!newRates.isEmpty()) {
                repository.saveAll(newRates);
            }
        }
    }

    private List<ExchangeRate> parseXml(String xml) {
        List<ExchangeRate> rates = new ArrayList<>();
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(false);
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new ByteArrayInputStream(xml.getBytes("UTF-8")));
            NodeList fxRateNodes = doc.getElementsByTagName("FxRate");
            for (int i = 0; i < fxRateNodes.getLength(); i++) {
                Element fxRate = (Element) fxRateNodes.item(i);
                String dateStr = fxRate.getElementsByTagName("Dt")
                        .item(0).getTextContent();
                LocalDate date = LocalDate.parse(dateStr);
                NodeList ccyAmtNodes = fxRate.getElementsByTagName("CcyAmt");
                if (ccyAmtNodes.getLength() >= 2) {
                    Element foreignCcy = (Element) ccyAmtNodes.item(1);
                    String code = foreignCcy.getElementsByTagName("Ccy")
                            .item(0).getTextContent();
                    double rate = Double.parseDouble(
                            foreignCcy.getElementsByTagName("Amt")
                                    .item(0).getTextContent()
                    );
                    rates.add(new ExchangeRate(code, rate, date));
                }
            }
        } catch (Exception e) {
            System.out.println("[ERROR] Failed to parse LB API response: " + e.getMessage());
        }
        return rates;
    }
}