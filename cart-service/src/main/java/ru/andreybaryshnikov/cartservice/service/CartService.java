package ru.andreybaryshnikov.cartservice.service;

import ru.andreybaryshnikov.cartservice.model.dto.CartDto;

import java.util.List;

public interface CartService {
    void addToCart(String xRequestId, long xUserId, CartDto cartDto);

    List<CartDto> getCart(String xRequestId, long xUserId);

    void clearCart(String xRequestId, long xUserId);
}
