package ru.andreybaryshnikov.orderservice.orders.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.andreybaryshnikov.orderservice.orders.exception.UnauthorizedException;
import ru.andreybaryshnikov.orderservice.orders.model.Order;
import ru.andreybaryshnikov.orderservice.orders.model.dto.OrderDto;
import ru.andreybaryshnikov.orderservice.orders.service.OrderService;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/order")
public class OrderController {
    private final OrderService orderService;

    @GetMapping
    public Order get(@RequestHeader("X-Request-Id") String xRequestId,
                     @RequestHeader("X-UserId") String xUserId) {
        if (xUserId == null)
            throw new UnauthorizedException();
        return orderService.getToId(UUID.fromString(xRequestId));
    }

    @PostMapping
    public Order create(@RequestBody OrderDto orderDto,
                        @RequestHeader("X-Request-Id") String xRequestId,
                        @RequestHeader("X-UserId") String xUserId) {
        if (xUserId == null)
            throw new UnauthorizedException();
        return orderService.create(orderDto, xRequestId, xUserId);
    }
}
