package ru.andreybaryshnikov.catalogservice.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.andreybaryshnikov.catalogservice.model.dto.ProductDto;
import ru.andreybaryshnikov.catalogservice.service.CatalogService;

import java.util.List;

@Slf4j
@RestController
public class CatalogController {
    private final CatalogService catalogService;

    public CatalogController(CatalogService catalogService) {
        this.catalogService = catalogService;
        catalogService.init();
    }

    /**
     * Поиск товара
     * @param xRequestId
     * @param xUserId
     * @param productName
     * @param limit
     * @return
     */
    @GetMapping("/api/v1/search/product_search/")
    public List<ProductDto> productSearch(@RequestHeader("X-Request-Id") String xRequestId,
                                           @RequestHeader("X-UserId") String xUserId,
                                           @RequestParam("q") String productName,
                                           @RequestParam int limit) {
        return catalogService.productSearch(xRequestId, xUserId, productName, limit);
    }

    /**
     * Просмотр товара
     * @param xRequestId
     * @param xUserId
     * @param productId
     * @return
     */
    @GetMapping("/api/v1/products/{id}")
    public ProductDto getProduct(@RequestHeader("X-Request-Id") String xRequestId,
                                    @RequestHeader("X-UserId") String xUserId,
                                    @PathVariable("id") String productId) {
        return catalogService.getProduct(xRequestId, xUserId, productId);
    }
}