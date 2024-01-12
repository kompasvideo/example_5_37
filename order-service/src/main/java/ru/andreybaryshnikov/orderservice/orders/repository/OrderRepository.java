package ru.andreybaryshnikov.orderservice.orders.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.andreybaryshnikov.orderservice.orders.model.Order;

import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, UUID> {
}
