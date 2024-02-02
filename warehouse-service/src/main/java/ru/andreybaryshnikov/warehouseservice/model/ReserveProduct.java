package ru.andreybaryshnikov.warehouseservice.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalDateTime;

@JsonIgnoreProperties({"hibernateLazyInitializer"})
@Entity
@Data
@Table(name = "reserve_product")
public class ReserveProduct {
    @Id
    @Column(name ="id")
    private String productId;
    private long userId;
    private int count;
    private String requestId;
    private LocalDateTime dateTime;
}
