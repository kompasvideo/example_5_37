package ru.andreybaryshnikov.cartservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.andreybaryshnikov.cartservice.model.dto.CartDto;
import ru.andreybaryshnikov.cartservice.service.CartService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;

    /**
     * Добавить товар в корзину
     * @param xRequestId
     * @param xUserId
     * @param cartDto
     */
    @PostMapping("/api/v1/cart/products/")
    public void addToCart(@RequestHeader("X-Request-Id") String xRequestId,
                          @RequestHeader("X-UserId") long xUserId,
                          @RequestBody CartDto cartDto){
        log.info("--- addToCart 1 ---");
        cartService.addToCart(xRequestId, xUserId, cartDto);
        log.info("--- addToCart 2 ---");
    }

    /**
     * Просмотр корзины
     * @param xRequestId
     * @param xUserId
     * @return
     */
    @GetMapping("/api/v1/cart/products/")
    List<CartDto> getCart(@RequestHeader("X-Request-Id") String xRequestId,
                          @RequestHeader("X-UserId") long xUserId){
        log.info("--- getToCart 1 ---");
        List<CartDto> cartDtos = cartService.getCart(xRequestId,xUserId);
        log.info("--- getToCart 1 ---");
        return  cartDtos;
    }

    /**
     * Очистка корзины
     * @param xRequestId
     * @param xUserId
     */
    @GetMapping("/api/v1/cart/clear/")
    void clearCart(@RequestHeader("X-Request-Id") String xRequestId,
                   @RequestHeader("X-UserId") long xUserId){
        log.info("--- clearToCart 1 ---");
        cartService.clearCart(xRequestId, xUserId);
        log.info("--- clearToCart 1 ---");
    }
}
