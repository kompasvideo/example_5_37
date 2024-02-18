package ru.andreybaryshnikov.warehouseservice.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.andreybaryshnikov.warehouseservice.model.dto.ProductDto;
import ru.andreybaryshnikov.warehouseservice.model.Product;
import ru.andreybaryshnikov.warehouseservice.model.Warehouse;
import ru.andreybaryshnikov.warehouseservice.service.WarehouseService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/warehouse")
public class WarehouseController {
    private final WarehouseService warehouseService;

    public WarehouseController(WarehouseService warehouseService) {
        this.warehouseService = warehouseService;
        warehouseService.init();
    }

    /**
     * добавление товара
     * @param xRequestId
     * @param productDto
     */
    @PostMapping("/add")
    public void add(@RequestHeader("X-Request-Id") String xRequestId,
                           @RequestBody ProductDto productDto) {
        log.info("--- 1 add ---");
        warehouseService.add(xRequestId, productDto);
        log.info("--- 2 add ---");
    }

    /**
     * резервирование товара
     * @param xRequestId
     * @param xUserId
     * @param productDto
     * @return
     */
    @PostMapping("/reserve")
    public boolean reserve(@RequestHeader("X-Request-Id") String xRequestId,
                       @RequestHeader("X-UserId") String xUserId,
                       @RequestBody ProductDto productDto) {
        log.info("--- 1 reverse ---");
        boolean result  = warehouseService.reserve(xRequestId, xUserId, productDto);
        log.info("--- 2 reverse ---");
        return result;
    }

    /**
     * произошла оплата товара
     * @param xRequestId
     * @param xUserId
     * @return
     */
    @PostMapping("/pay")
    public boolean pay(@RequestHeader("X-Request-Id") String xRequestId,
                           @RequestHeader("X-UserId") String xUserId) {
        log.info("--- 1 pay ---");
        boolean result  = warehouseService.pay(xRequestId, xUserId);
        log.info("--- 2 pay ---");
        return result;
    }

    /**
     * отмена резервирования
     * @param xRequestId
     * @param xUserId
     * @return
     */
    @PostMapping("/cancel")
    public boolean cancel(@RequestHeader("X-Request-Id") String xRequestId,
                           @RequestHeader("X-UserId") String xUserId) {
        log.info("--- 1 cancel ---");
        boolean result  = warehouseService.cancel(xRequestId, xUserId);
        log.info("--- 2 cancel ---");
        return result;
    }

    /**
     * показать все товары на складе
     * @param xRequestId
     * @param xUserId
     * @return
     */
    @PostMapping("/getProducts")
    public List<Product> getProducts(@RequestHeader("X-Request-Id") String xRequestId,
                                     @RequestHeader("X-UserId") String xUserId) {
        log.info("--- 1 get ---");
        List<Product> result  = warehouseService.getProducts(xRequestId, xUserId);
        log.info("--- 2 get ---");
        return result;
    }

    /**
     * покзать статистику склада
     * @param xRequestId
     * @param xUserId
     * @return
     */
    @PostMapping("/getWarehouse")
    public List<Warehouse> getWarehouse(@RequestHeader("X-Request-Id") String xRequestId,
                                        @RequestHeader("X-UserId") String xUserId) {
        log.info("--- 1 get ---");
        List<Warehouse> result  = warehouseService.getWarehouse(xRequestId, xUserId);
        log.info("--- 2 get ---");
        return result;
    }
}
