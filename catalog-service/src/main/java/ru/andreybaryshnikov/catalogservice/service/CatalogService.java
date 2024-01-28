package ru.andreybaryshnikov.catalogservice.service;


import ru.andreybaryshnikov.catalogservice.model.dto.ProductDto;

import java.util.List;

public interface CatalogService {
    List<ProductDto> productSearch(String xRequestId,
                                   String xUserId,
                                   String productName,
                                   int limit);

    void init();

    ProductDto getProduct(String xRequestId, String xUserId, String productId);
}
