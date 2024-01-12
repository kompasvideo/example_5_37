package ru.andreybaryshnikov.orderservice.orders.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import ru.andreybaryshnikov.orderservice.orders.exception.BadRequestException;
import ru.andreybaryshnikov.orderservice.orders.exception.ConflictException;
import ru.andreybaryshnikov.orderservice.orders.model.MinusMoney;
import ru.andreybaryshnikov.orderservice.orders.model.Money;
import ru.andreybaryshnikov.orderservice.orders.model.dto.OrderDto;
import ru.andreybaryshnikov.orderservice.orders.repository.OrderRepository;
import ru.andreybaryshnikov.orderservice.orders.model.Order;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class OrderServiceImpl implements OrderService{
    @Value("${billing.uri}")
    private String url;

    @Value("${notification.uri}")
    private String urlNotific;
    private final ModelMapper mapper;
    private final OrderRepository orderRepository;

    @Override
    public Order create(OrderDto orderDto, String xRequestId, String xUserId) {
        log.info("-- 1 create --");
        double balance = getBalanceFromAccount(xRequestId,xUserId);
        log.info("-- 2 create -- balance" + balance);
        if (withdrawMoneyFromBillingService(orderDto, xRequestId, xUserId, balance)) {
            log.info("-- 3 create --");
            Order order = mapper.map(orderDto, Order.class);
            log.info("-- 4 create --");
            order.setId(UUID.fromString(xRequestId));
            log.info("-- 5 create --");
            Optional<Order> orderOptional = orderRepository.findById(order.getId());
            log.info("-- 6 create --");
            if (orderOptional.isPresent()) {
                log.info("-- 7 create --");
                throw new ConflictException();
            }
            log.info("-- 8 create --");
            Order orderResult = orderRepository.save(order);
            double total = balance - orderDto.getPrice();
            log.info("-- 9 create money call notufication minus --");
            sendMessageToNotificafion(xRequestId, Long.parseLong(xUserId), "-",
                orderDto.getPrice(), total);
            log.info("-- 10 create --");
            return orderResult;
        } else {
            log.info("-- 11 create money call notufication cancel no money  --");
            sendMessageToNotificafion(xRequestId, Long.parseLong(xUserId), "Cancel Order - no money",
                0, 0);
            log.info("--- 12 create ---");
            return null;
        }
    }

    private double getBalanceFromAccount(String xRequestId, String xUserId) {
        log.info("-- 1 getBalanceFromAccount --");
        RestTemplate rt = new RestTemplate();
        log.info("-- 1 -- " + url + "get");
        URI uri = null;
        try {
            log.info("-- 2 --");
            uri = new URI(url + "get");
            log.info("-- 2 --" + uri.toURL());
        } catch (URISyntaxException e) {
            log.info("URISyntaxException - " + e);
        } catch (MalformedURLException e) {
            log.info("MalformedURLException - " + e);
        }
        log.info("-- 3 --");
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("Content-Type", "application/json");
        log.info("-- 3 -- X-Request-Id - " + xRequestId);
        headers.add("X-Request-Id", xRequestId);
        log.info("-- 3 -- X-UserId - " + xUserId);
        headers.add("X-UserId", xUserId);

        RequestEntity<String> requestEntity = new RequestEntity<>(headers, HttpMethod.GET, uri);
        log.info("-- 4 --");
        log.info("-- 4 --" + requestEntity.getBody());
        ResponseEntity<Double> balance = rt.exchange(requestEntity, Double.class);
        log.info("-- 5 --");
        log.info("-- 5 -- " + balance.getBody());
        return balance.getBody();
    }

    private boolean withdrawMoneyFromBillingService(OrderDto orderDto, String xRequestId, String xUserId,
                                                    double balance) {
        if (balance < orderDto.getPrice()) {
            log.info("-- 1 --");
            log.info(balance + "  " + orderDto.getPrice());
            log.info("-- 2 --");
            return false;
        }
        double total = balance - orderDto.getPrice();
        log.info("-- 1  withdrawMoneyFromBillingService --");
        RestTemplate rt = new RestTemplate();
        log.info("-- 1 -- " + url + "sub");
        URI uri = null;
        try {
            log.info("-- 2 --");
            uri = new URI(url + "sub");
            log.info("-- 2 --" + uri.toURL());
        } catch (URISyntaxException e) {
            log.info("URISyntaxException - " + e);
        } catch (MalformedURLException e) {
            log.info("MalformedURLException - " + e);
        }
        log.info("-- 3 --");
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("Content-Type", "application/json");
        log.info("-- 3 -- X-Request-Id - " + xRequestId);
        headers.add("X-Request-Id", xRequestId);
        log.info("-- 3 -- X-UserId - " + xUserId);
        headers.add("X-UserId", xUserId);
        MinusMoney minusMoney = new MinusMoney(orderDto.getPrice(), total);
        log.info("-- 3 -- MinusMoney - " + minusMoney);
        RequestEntity<MinusMoney> requestEntity = new RequestEntity<>(minusMoney, headers, HttpMethod.POST, uri);
        log.info("-- 4 --");
        log.info("-- 4 --" + requestEntity.getBody());
        try {
            log.info("-- 5 --");
            rt.exchange(requestEntity, String.class);
        } catch (RestClientException e) {
            log.info("-- 6 --");
            log.info(e.toString());
            return false;
        }
        log.info("-- 7 --");
        return true;
    }

    @Override
    public Order getToId(UUID uuid) {
        log.info("-- 1 getToId --");
        log.info("-- 1 getToId -- uuid " + uuid);
        Optional<Order> orderOptional = orderRepository.findById(uuid);
        log.info("-- 2 getToId --");
        if (orderOptional.isPresent()) {
            log.info("-- 3 getToId --");
            return orderOptional.get();
        } else {
            log.info("-- 4 getToId --");
            throw new BadRequestException();
        }
    }

    private void sendMessageToNotificafion(String xRequestId, long userId, String operation,
                                           double count, double total) {
        log.info("-- 1 --");
        RestTemplate rt = new RestTemplate();
        log.info("-- 1 -- " + urlNotific);
        URI uri = null;
        try {
            log.info("-- 2 --");
            uri = new URI(urlNotific);
            log.info("-- 2 --" + uri.toURL());
        } catch (URISyntaxException e) {
            log.info("URISyntaxException - " + e);
        } catch (MalformedURLException e) {
            log.info("MalformedURLException - " + e);
        }
        log.info("-- 3 --");
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("Content-Type", "application/json");
        log.info("-- 3 -- X-Request-Id - " + xRequestId);
        headers.add("X-Request-Id", xRequestId);
        log.info("-- 3 -- X-UserId - " + userId);
        headers.add("X-UserId", String.valueOf(userId));
        Money money = new Money(LocalDateTime.now(), operation, count, total);
        RequestEntity<Money> requestEntity = new RequestEntity<>(money, headers, HttpMethod.POST, uri);
        log.info("-- 4 --");
        log.info("-- 4 -- " + money);
        rt.exchange(requestEntity, String.class);
        log.info("-- 5 --");
    }
}
