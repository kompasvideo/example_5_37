package ru.andreybaryshnikov.warehouseservice.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalDateTime;

@JsonIgnoreProperties({"hibernateLazyInitializer"})
@Data
@Entity
@Table(name = "warehouse")
public class Warehouse {
    @Id
    @Column(name = "id")
    private String xRequestId;
    private String productId;
    private long xUserId;
    private int count;
    private String operation;
    private LocalDateTime dateTime;
}
