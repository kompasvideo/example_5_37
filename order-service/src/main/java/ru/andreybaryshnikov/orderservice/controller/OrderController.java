package ru.andreybaryshnikov.orderservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.andreybaryshnikov.orderservice.exception.UnauthorizedException;
import ru.andreybaryshnikov.orderservice.model.PaymentMethod;
import ru.andreybaryshnikov.orderservice.model.dto.DeliveryLocationDto;
import ru.andreybaryshnikov.orderservice.model.dto.OrderDto;
import ru.andreybaryshnikov.orderservice.model.Order;
import ru.andreybaryshnikov.orderservice.service.OrderService;

import java.util.UUID;
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/order")
public class OrderController {
    private final OrderService orderService;

    /**
     * Оформить заказ
     * @param orderDto
     * @param xRequestId
     * @param xUserId
     * @return
     */
    @PostMapping("/create")
    public Order create(@RequestBody OrderDto orderDto,
                        @RequestHeader("X-Request-Id") String xRequestId,
                        @RequestHeader("X-UserId") String xUserId) {
        log.info("--- controller create 1 ---");
        if (xUserId == null) {
            log.info("--- controller create 2 ---");
            throw new UnauthorizedException();
        }
        log.info("--- controller create 1 ---");
        return orderService.create(orderDto, xRequestId, xUserId);
    }

    /**
     * выбрать место доставки
     * @param xRequestId
     * @param xUserId
     * @param deliveryLocationDto
     */
    @PostMapping("/chooseADeliveryLocation")
    public Order chooseADeliveryLocation(@RequestHeader("X-Request-Id") String xRequestId,
                                        @RequestHeader("X-UserId") String xUserId,
                                        @RequestBody DeliveryLocationDto deliveryLocationDto) {
        log.info("--- controller chooseADeliveryLocation 1 ---");
        if (xUserId == null) {
            log.info("--- controller chooseADeliveryLocation 2 ---");
            throw new UnauthorizedException();
        }
        log.info("--- controller chooseADeliveryLocation 3 ---");
        return orderService.chooseADeliveryLocation(xRequestId,xUserId,deliveryLocationDto);
    }

    /**
     * выбрать способ оплаты
     * @param xRequestId
     * @param xUserId
     * @param paymentMethod
     */
    @PostMapping("/chooseAPaymentMethod")
    public Order chooseAPaymentMethod(@RequestHeader("X-Request-Id") String xRequestId,
                                     @RequestHeader("X-UserId") String xUserId,
                                     @RequestBody PaymentMethod paymentMethod) {
        log.info("--- controller chooseAPaymentMethod 1 ---");
        if (xUserId == null) {
            log.info("--- controller chooseAPaymentMethod 2 ---");
            throw new UnauthorizedException();
        }
        log.info("--- controller chooseAPaymentMethod 3 ---");
        return orderService.chooseAPaymentMethod(xRequestId,xUserId, paymentMethod);
    }

    /**
     * Выбрать время доставки
     * @param xRequestId
     * @param xUserId
     * @param dateTime
     */
    @PostMapping("/chooseTheDeliveryTime")
    public Order chooseTheDeliveryTime(@RequestHeader("X-Request-Id") String xRequestId,
                                      @RequestHeader("X-UserId") String xUserId,
                                      @RequestBody String dateTime){
        log.info("--- controller chooseTheDeliveryTime 1 ---");
        if (xUserId == null) {
            log.info("--- controller chooseTheDeliveryTime 2 ---");
            throw new UnauthorizedException();
        }
        log.info("--- controller chooseTheDeliveryTime 3 ---");
        return orderService.chooseTheDeliveryTime(xRequestId, xUserId, dateTime);
    }

    /**
     * Зарезервировать курьера
     * @param xRequestId
     * @param xUserId
     * @return
     */
    @PostMapping("/courierReserve")
    public Order courierReserve(@RequestHeader("X-Request-Id") String xRequestId,
                                  @RequestHeader("X-UserId") String xUserId) {
        log.info("--- controller courierReserve 1 ---");
        if (xUserId == null) {
            log.info("--- controller courierReserve 2 ---");
            throw new UnauthorizedException();
        }
        log.info("--- controller courierReserve 3 ---");
        return orderService.courierReserve(xRequestId, xUserId);
    }

    /**
     * Оплатить заказ
     * @param xRequestId
     * @param xUserId
     * @return
     */
    @PostMapping("/pay")
    public boolean pay(@RequestHeader("X-Request-Id") String xRequestId,
                    @RequestHeader("X-UserId") String xUserId) {
        log.info("--- controller pay 1 ---");
        if (xUserId == null) {
            log.info("--- controller pay 2 ---");
            throw new UnauthorizedException();
        }
        log.info("--- controller pay 3 ---");
        return orderService.payAndSendMessage(xRequestId, xUserId);
    }

    /**
     * посмотреть заказ
     * @param xRequestId
     * @param xUserId
     * @return
     */
    @GetMapping("/get")
    public Order get(@RequestHeader("X-Request-Id") String xRequestId,
                     @RequestHeader("X-UserId") String xUserId) {
        log.info("--- controller get 1 ---");
        if (xUserId == null) {
            log.info("--- controller get 2 ---");
            throw new UnauthorizedException();
        }
        log.info("--- controller get 3 ---");
        return orderService.getToId(UUID.fromString(xRequestId));
    }

    /**
     * очистка корзины
     * @param xRequestId
     * @param xUserId
     * @return
     */
    @PostMapping("/clear")
    public boolean clear(@RequestHeader("X-Request-Id") String xRequestId,
                         @RequestHeader("X-UserId") String xUserId) {
        log.info("--- controller clear 1 ---");
        if (xUserId == null) {
            log.info("--- controller clear 2 ---");
            throw new UnauthorizedException();
        }
        log.info("--- controller clear 3 ---");
        return orderService.clear(UUID.fromString(xRequestId));
    }
}
