package ru.andreybaryshnikov.deliveryservice.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.util.UUID;

@JsonIgnoreProperties({"hibernateLazyInitializer"})
@Data
@Entity
@Table(name = "delivery")
public class Delivery {
    @Id
    @Column(name = "id")
    private UUID id;
    private long userId;
    private String localDateTime;
}
