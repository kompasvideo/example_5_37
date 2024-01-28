package ru.andreybaryshnikov.catalogservice.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@JsonIgnoreProperties({"hibernateLazyInitializer"})
@Data
@Entity
@Table(name = "catalog")
@AllArgsConstructor
@NoArgsConstructor
public class Product {
    @Id
    private UUID id;
    private String name;
    private double price;
    @Column(name = "image_url")
    private String imageUrl;
    @Column(name = "old_price")
    private double oldPrice;
    private String rating;
}
