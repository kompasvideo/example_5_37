package ru.andreybaryshnikov.warehouseservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import ru.andreybaryshnikov.warehouseservice.model.Product;
import ru.andreybaryshnikov.warehouseservice.model.ReserveProduct;
import ru.andreybaryshnikov.warehouseservice.model.Warehouse;
import ru.andreybaryshnikov.warehouseservice.model.dto.ProductDto;
import ru.andreybaryshnikov.warehouseservice.repository.ProductRepository;
import ru.andreybaryshnikov.warehouseservice.repository.ReserveRepository;
import ru.andreybaryshnikov.warehouseservice.repository.WarehouseRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class WarehouseServiceImpl implements WarehouseService {
    private final ModelMapper modelMapper;
    private final WarehouseRepository warehouseRepository;
    private final ProductRepository productRepository;
    private final ReserveRepository reserveRepository;
    private final String OPERATION_MINUS = "-";
    private final String OPERATION_RESERVE = "RESERVE";
    private final String OPERATION_CANCEL = "CANCEL";

    @Override
    public void add(String xRequestId, ProductDto productDto) {
        log.info("--- 1 add ---");
        log.info("--- 2 add --- productDto - " + productDto);
        Product product = modelMapper.map(productDto, Product.class);
        product.setRequestId(xRequestId);
        log.info("--- 3 add --- warehouse - " + product);
        productRepository.save(product);
        log.info("--- 4 add ---");
    }

    @Override
    public boolean reserve(String xRequestId, String xUserId, ProductDto productDto) {
        log.info("--- 1 reverse ---");
        log.info("--- 2 reverse --- xRequestId - " + xRequestId);
        log.info("--- 2 reverse --- xUserId - " + xUserId);
        log.info("--- 2 reverse --- productDto - " + productDto);
        Optional<Product> productOp = productRepository.findById(productDto.getProductId());
        if (productOp.isPresent() && productOp.get().getCount() >= productDto.getCount()) {
            log.info("--- 3 reverse ---");
            saveWarehouseLogs(xRequestId, xUserId, productDto);
            log.info("--- 4 reverse ---");
            reserveProduct(xRequestId, xUserId, productDto);
            log.info("--- 5 reverse ---");
            return true;
        }
        return false;
    }

    @Override
    public boolean cancel(String xRequestId, String xUserId) {
        log.info("--- 1 cancel ---");
        log.info("--- 2 cancel --- xRequestId - " + xRequestId);
        log.info("--- 3 cancel --- xUserId - " + xUserId);
        List<ReserveProduct> reserveProducts = reserveRepository.findAllByRequestId(xRequestId);
        if (reserveProducts.isEmpty()) {
            log.info("--- 4 cancel ---");
            return false;
        }
        log.info("--- 5 cancel ---");
        List<String> produuctIds = new ArrayList<>();
        log.info("--- 6 cancel ---");
        for (var reserverProduct : reserveProducts) {
            produuctIds.add(reserverProduct.getProductId());
            saveWarehouseLogs(xRequestId, xUserId, reserverProduct);
        }
        log.info("--- 7 cancel --- listSize - " + produuctIds.size());
        reserveRepository.deleteAllByIdInBatch(produuctIds);
        log.info("--- 8 cancel ---");
        log.info("--- 8 cancel ---");
        return true;
    }

    @Override
    public void init() {
        Product product = new Product();
        product.setRequestId("");
        product.setProductId("1");
        product.setCount(10);
        log.info("--- 3 init --- warehouse - " + product);
        productRepository.save(product);
    }

    /**
     * показать все товары на складе
     * @param xRequestId
     * @param xUserId
     * @return
     */
    @Override
    public List<Product> getProducts(String xRequestId, String xUserId) {
        log.info("--- getProducts 1 ---");
        List<Product> productList = productRepository.findAll();
        log.info("--- getProducts 2 ---");
        return productList;
    }

    /**
     * покзать статистику склада
     * @param xRequestId
     * @param xUserId
     * @return
     */
    @Override
    public List<Warehouse> getWarehouse(String xRequestId, String xUserId) {
        log.info("--- getWarehouse 1 ---");
        List<Warehouse> warehouseList = warehouseRepository.findAll();
        log.info("--- getWarehouse 2 ---");
        return warehouseList;
    }


    @Override
    public boolean pay(String xRequestId, String xUserId) {
        log.info("--- pay 1 --- xRequestId - " + xRequestId);
        log.info("--- pay 2 --- xUserId - " + xUserId);
        ReserveProduct reserveProduct = getReserveProduct(xRequestId);
        log.info("--- pay 3 --- reserveProduct - " + reserveProduct);
        Product product = getProduct(reserveProduct.getProductId());
        log.info("--- pay 4 --- product - " + product);
        if (product.getCount() < reserveProduct.getCount()) {
            log.info("--- pay 5 ---");
            return false;
        }
        log.info("--- pay 6 ---");
        product.setCount(product.getCount() - reserveProduct.getCount());
        log.info("--- pay 7 --- product - " + product);
        productRepository.save(product);
        log.info("--- pay 8 ---");
        reserveRepository.deleteById(xRequestId);
        log.info("--- pay 9 ---");
        saveWarehouseLogs(xRequestId, xUserId, product);
        return true;
    }

    private ReserveProduct getReserveProduct(String xRequestId) {
        log.info("--- getReserveProduct 1 --- xRequestId - " + xRequestId);
        List<ReserveProduct> reserveProductOp = reserveRepository.findAllByRequestId(xRequestId);
        log.info("--- getReserveProduct 2 ---");
        if (reserveProductOp.isEmpty()) {
            log.info("--- getReserveProduct 3 ---");
            throw new RuntimeException();
        }
        log.info("--- getReserveProduct 4 --- reserveProduct - " + reserveProductOp.get(0));
        return reserveProductOp.get(0);
    }

    private Product getProduct(String productId) {
        log.info("--- getProduct 1 ---");
        Optional<Product> productOp = productRepository.findById(productId);
        log.info("--- getProduct 2 ---");
        if (productOp.isEmpty()) {
            log.info("--- getProduct 3 ---");
            throw new RuntimeException();
        }
        log.info("--- getProduct 4 --- product - " + productOp.get());
        return productOp.get();
    }

    private void reserveProduct(String xRequestId, String xUserId, ProductDto productDto) {
        log.info("--- reserveProduct 1 --- xRequestId - " + xRequestId);
        log.info("--- reserveProduct 2 --- xUserId - " + xUserId);
        log.info("--- reserveProduct 3 --- productDto - " + productDto);
        ReserveProduct reserveProduct = modelMapper.map(productDto, ReserveProduct.class);
        log.info("--- reserveProduct 4 --- reserveProduct - " + reserveProduct);
        reserveProduct.setUserId(Long.parseLong(xUserId));
        reserveProduct.setRequestId(xRequestId);
        reserveProduct.setDateTime(LocalDateTime.now());
        log.info("--- reserveProduct 5 --- reserveProduct - " + reserveProduct);
        reserveRepository.save(reserveProduct);
        log.info("--- reserveProduct 6 ---");
    }

    private void saveWarehouseLogs(String xRequestId, String xUserId, Product product) {
        log.info("--- 1 saveNewWarehouseLogs ---");
        Warehouse warehouseNew = new Warehouse();
        warehouseNew.setProductId(product.getProductId());
        warehouseNew.setXRequestId(xRequestId);
        warehouseNew.setXUserId(Long.parseLong(xUserId));
        warehouseNew.setCount(product.getCount());
        warehouseNew.setOperation(OPERATION_MINUS);
        warehouseNew.setDateTime(LocalDateTime.now());
        log.info("--- 2 saveNewWarehouseLogs --- warehouseNew - " + warehouseNew);
        warehouseRepository.save(warehouseNew);
        log.info("--- 3 saveNewWarehouseLogs ---");
    }

    private void saveWarehouseLogs(String xRequestId, String xUserId, ProductDto productDto) {
        log.info("--- 1 saveWarehouseLogs ---");
        Warehouse warehouse = new Warehouse();
        log.info("--- 2 saveWarehouseLogs ---");
        warehouse.setXRequestId(xRequestId);
        warehouse.setXUserId(Long.parseLong(xUserId));
        warehouse.setProductId(productDto.getProductId());
        warehouse.setCount(productDto.getCount());
        warehouse.setOperation(OPERATION_RESERVE);
        warehouse.setDateTime(LocalDateTime.now());
        log.info("--- 3 saveWarehouseLogs ---");
        log.info("--- 4 saveWarehouseLogs --- warehouse - " + warehouse);
        warehouseRepository.save(warehouse);
        log.info("--- 5 saveWarehouseLogs ---");
    }

    private void saveWarehouseLogs(String xRequestId, String xUserId, ReserveProduct reserverProduct) {
        log.info("--- 1 saveWarehouseLogs ---");
        Warehouse warehouse = new Warehouse();
        log.info("--- 2 saveWarehouseLogs ---");
        warehouse.setXRequestId(xRequestId);
        warehouse.setXUserId(Long.parseLong(xUserId));
        warehouse.setProductId(reserverProduct.getProductId());
        warehouse.setCount(reserverProduct.getCount());
        warehouse.setOperation(OPERATION_CANCEL);
        warehouse.setDateTime(LocalDateTime.now());
        log.info("--- 3 saveWarehouseLogs ---");
        log.info("--- 4 saveWarehouseLogs --- warehouse - " + warehouse);
        warehouseRepository.save(warehouse);
        log.info("--- 5 saveWarehouseLogs ---");
    }
}
