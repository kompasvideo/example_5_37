package ru.andreybaryshnikov.warehouseservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import ru.andreybaryshnikov.warehouseservice.model.Product;
import ru.andreybaryshnikov.warehouseservice.model.Warehouse;
import ru.andreybaryshnikov.warehouseservice.model.dto.ProductDto;
import ru.andreybaryshnikov.warehouseservice.repository.ProductRepository;
import ru.andreybaryshnikov.warehouseservice.repository.WarehouseRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class WarehouseServiceImpl implements WarehouseService {
    private final ModelMapper modelMapper;
    private final WarehouseRepository warehouseRepository;
    private final ProductRepository productRepository;
    private final String OPERATION_MINUS = "-";
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
            saveModifyProductCount(xRequestId, productDto, productOp);
            log.info("--- 5 reverse ---");
            return true;
        }
        return false;
    }

    @Override
    public boolean cancel(String xRequestId, String xUserId, ProductDto productDto) {
        log.info("--- 1 cancel ---");
        log.info("--- 2 cancel --- xRequestId - " + xRequestId);
        log.info("--- 2 cancel --- xUserId - " + xUserId);
        log.info("--- 2 cancel --- productDto - " + productDto);
        Optional<Warehouse> warehouseOp = warehouseRepository.findById(xRequestId);
        if (warehouseOp.isPresent() && productDto.getProductId().equals(warehouseOp.get().getProductId())
                && warehouseOp.get().getOperation().equals(OPERATION_MINUS)) {
            log.info("--- 3 cancel ---");
            Warehouse warehouse = warehouseOp.get();
            log.info("--- 4 cancel --- warehouse - " + warehouse);
            int count = warehouse.getCount();
            log.info("--- 5 cancel --- count - " + count);
            List<Product> productList = productRepository.findAllByRequestId(xRequestId);
            log.info("--- 6 cancel --- productOp - " + productList);
            if(!productList.isEmpty()) {
                log.info("--- 7 cancel ---");
                Product product = productList.get(0);
                log.info("--- 8 cancel --- product - " + product);
                product.setCount(product.getCount() + count);
                log.info("--- 9 cancel --- product - " + product);
                productRepository.save(product);
                log.info("--- 10 cancel ---");
                saveNewWarehouseLogs(xRequestId, xUserId, productDto, product.getCount() + count);
                log.info("--- 11 cancel ---");
                return true;
            }
            log.info("--- 12 cancel ---");
            return false;
        }
        log.info("--- 13 cancel ---");
        return false;
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

    private void saveNewWarehouseLogs(String xRequestId, String xUserId, ProductDto productDto, int count) {
        log.info("--- 1 saveNewWarehouseLogs ---");
        Warehouse warehouseNew = new Warehouse();
        warehouseNew.setProductId(productDto.getProductId());
        warehouseNew.setXRequestId(xRequestId);
        warehouseNew.setXUserId(Long.parseLong(xUserId));
        warehouseNew.setCount(count);
        warehouseNew.setOperation(OPERATION_CANCEL);
        warehouseNew.setDateTime(LocalDateTime.now());
        log.info("--- 2 saveNewWarehouseLogs --- warehouseNew - " + warehouseNew);
        warehouseRepository.save(warehouseNew);
        log.info("--- 3 saveNewWarehouseLogs ---");
    }

    private void saveModifyProductCount(String xRequestId, ProductDto productDto, Optional<Product> productOp) {
        log.info("--- 1 saveModifyProductCount ---");
        Product product = productOp.get();
        log.info("--- 2 saveModifyProductCount --- product - " + product);
        product.setCount(product.getCount() - productDto.getCount());
        product.setRequestId(xRequestId);
        log.info("--- 3 saveModifyProductCount --- product - " + product);
        productRepository.save(product);
        log.info("--- 4 saveModifyProductCount ---");
    }

    private void saveWarehouseLogs(String xRequestId, String xUserId, ProductDto productDto) {
        log.info("--- 1 saveWarehouseLogs ---");
        Warehouse warehouse = new Warehouse();
        log.info("--- 2 saveWarehouseLogs ---");
        warehouse.setXRequestId(xRequestId);
        warehouse.setXUserId(Long.parseLong(xUserId));
        warehouse.setProductId(productDto.getProductId());
        warehouse.setCount(productDto.getCount());
        warehouse.setOperation(OPERATION_MINUS);
        warehouse.setDateTime(LocalDateTime.now());
        log.info("--- 3 saveWarehouseLogs ---");
        log.info("--- 4 saveWarehouseLogs --- warehouse - " + warehouse);
        warehouseRepository.save(warehouse);
        log.info("--- 5 saveWarehouseLogs ---");
    }
}
