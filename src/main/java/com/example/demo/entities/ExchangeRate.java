package com.example.demo.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "exchange_rates")
@Getter
@Setter
@NoArgsConstructor
public class ExchangeRate {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    public String currencyCode;

    @Column(nullable = false)
    public double rate;

    @Column(nullable = false)
    public LocalDate dateOfRate;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public ExchangeRate(String currencyCode, double rate, LocalDate dateOfRate) {
        this.currencyCode = currencyCode;
        this.rate = rate;
        this.dateOfRate = dateOfRate;
    }

}
