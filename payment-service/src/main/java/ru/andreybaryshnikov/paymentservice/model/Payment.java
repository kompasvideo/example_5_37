package ru.andreybaryshnikov.paymentservice.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@JsonIgnoreProperties({"hibernateLazyInitializer"})
@Data
@Entity
@Table(name = "orders")
public class Payment {
    @Id
    @Column(name = "id")
    private UUID id;
    private String userId;
    private double money;
}
