package ru.andreybaryshnikov.orderservice.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class DeliveryLocationDto {
    private String locality; // город
    private String street;   // улица
    private String houseNumber; // номер дома
    private String structure; // корпус/строение
    private short flat;     // квартира
    private byte entrance; // подъезд
    private byte floor;    // этаж
    private String comment;  // комментарий
}
