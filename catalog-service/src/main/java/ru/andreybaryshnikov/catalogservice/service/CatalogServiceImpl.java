package ru.andreybaryshnikov.catalogservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.stereotype.Service;
import ru.andreybaryshnikov.catalogservice.exception.BadRequestException;
import ru.andreybaryshnikov.catalogservice.model.Product;
import ru.andreybaryshnikov.catalogservice.model.dto.ProductDto;
import ru.andreybaryshnikov.catalogservice.repository.CatalogRepository;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class CatalogServiceImpl implements CatalogService {
    private final CatalogRepository catalogRepository;
    private final ModelMapper modelMapper;

    @Override
    public List<ProductDto> productSearch(String xRequestId, String xUserId, String productName, int limit) {
        log.info("--- search 1 ---");
        Optional<List<Product>> productsOp = catalogRepository.findByNameIsLike("%" + productName + "%");
        log.info("--- search 2 ---");
        if (productsOp.isEmpty()) {
            log.info("--- search 3 ---");
             throw new BadRequestException();
        }
        log.info("--- search 4 ---");
        List<Product> products = productsOp.get();
        Type listProduct = new TypeToken<List<ProductDto>>() {}.getType();
        log.info("--- search 5 ---");
        List<ProductDto> productDtos = modelMapper.map(products, listProduct);
        log.info("--- search 6 ---");
        return productDtos;
    }

    @Override
    public void init() {
        log.info("--- init 1 ---");
        Product product = new Product(UUID.fromString("3d3abd76-2afb-4bf5-8956-dd5018225106"), "Трюковый самокат", 6200, "http://image.ru/img1.svg",
            8800,"0");
        log.info("--- init 2 --- product - " + product);
        catalogRepository.save(product);
        product = new Product(UUID.fromString("321b6be2-ab92-4787-9bce-7cb57af83b9d"), "Трюковый самокат красный", 6300, "http://image.ru/img2.svg",
            8900,"0");
        log.info("--- init 3 --- product - " + product);
        catalogRepository.save(product);
        product = new Product(UUID.fromString("a025a29b-4c44-4619-9327-a854f5a9f517"), "Трюковый самокат синий", 6500, "http://image.ru/img3.svg",
            9000,"3");
        log.info("--- init 4 --- product - " + product);
        catalogRepository.save(product);
        log.info("--- init 5 ---");
    }

    @Override
    public ProductDto getProduct(String xRequestId, String xUserId, String productId) {
        log.info("--- getProduct 1 ---");
        Optional<Product> productOp = catalogRepository.findById(UUID.fromString(productId));
        log.info("--- getProduct 2 --- productOp - " + productOp);
        if (productOp.isEmpty()) {
            log.info("--- getProduct 3 ---");
            throw new BadRequestException();
        }
        log.info("--- getProduct 4 ---");
        ProductDto productDto = modelMapper.map(productOp, ProductDto.class);
        log.info("--- getProduct 5 --- productDto - " + productDto);
        return productDto;
    }
}
