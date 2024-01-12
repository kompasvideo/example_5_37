package ru.andreybaryshnikov.notificationservice.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@JsonIgnoreProperties({"hibernateLazyInitializer"})
@Data
@Entity
@Table(name = "notification")
public class Notification {
    @Id
    @GeneratedValue
    //@GeneratedValue(strategy = GenerationType.IDENTITY)
    private long  id;
    private long userId;
    private LocalDateTime dateTime;
    private String operation;
    private BigDecimal count;
    private BigDecimal total;
    private String xRequestId;
}
