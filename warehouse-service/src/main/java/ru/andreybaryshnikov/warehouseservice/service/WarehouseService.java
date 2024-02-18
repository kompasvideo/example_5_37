package ru.andreybaryshnikov.warehouseservice.service;

import ru.andreybaryshnikov.warehouseservice.model.Product;
import ru.andreybaryshnikov.warehouseservice.model.Warehouse;
import ru.andreybaryshnikov.warehouseservice.model.dto.ProductDto;

import java.util.*;

public interface WarehouseService {
    void add(String xRequestId, ProductDto productDto);
    boolean reserve(String xRequestId, String xUserId, ProductDto productDto);
    boolean cancel(String xRequestId, String xUserId);
    void init();

    List<Product> getProducts(String xRequestId, String xUserId);

    List<Warehouse> getWarehouse(String xRequestId, String xUserId);

    boolean pay(String xRequestId, String xUserId);
}
