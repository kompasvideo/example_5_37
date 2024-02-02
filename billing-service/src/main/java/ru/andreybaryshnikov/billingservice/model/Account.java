package ru.andreybaryshnikov.billingservice.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import jakarta.persistence.*;
import java.util.UUID;
import java.math.BigDecimal;

@JsonIgnoreProperties({"hibernateLazyInitializer"})
@Data
@Entity
@Table(name = "account")
public class Account {
    @Id
    private long  id;
    private double balance;
    private UUID xRequestId;
}