package ru.andreybaryshnikov.otus_auth.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@AllArgsConstructor
@Data
public class Money {
    private LocalDateTime dateTime;
    private String operation;
    private double count;
    private double total;
}
