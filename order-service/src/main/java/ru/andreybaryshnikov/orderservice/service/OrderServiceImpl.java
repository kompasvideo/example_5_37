package ru.andreybaryshnikov.orderservice.service;

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
import org.springframework.web.client.RestTemplate;
import ru.andreybaryshnikov.orderservice.model.dto.OrderDto;
import ru.andreybaryshnikov.orderservice.exception.BadRequestException;
import ru.andreybaryshnikov.orderservice.exception.TheProductIsOutOfStock;
import ru.andreybaryshnikov.orderservice.model.Money;
import ru.andreybaryshnikov.orderservice.model.Order;
import ru.andreybaryshnikov.orderservice.model.PaymentMethod;
import ru.andreybaryshnikov.orderservice.model.dto.DeliveryLocationDto;
import ru.andreybaryshnikov.orderservice.model.dto.PayMoneyDto;
import ru.andreybaryshnikov.orderservice.model.dto.ProductDto;
import ru.andreybaryshnikov.orderservice.repository.OrderRepository;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class OrderServiceImpl implements OrderService {
    private final ModelMapper mapper;
    private final OrderRepository orderRepository;
    @Value("${payment.uri}")
    private String url;
    @Value("${notification.uri}")
    private String urlNotific;
    @Value("${warehouse.uri}")
    private String urlWarehouse;
    @Value("${billing.uri}")
    private String urlBilling;
    @Value("${delivery.uri}")
    private String urlDelivery;
    private final String OperationNoPay = "no pay";
    private final String OperationPay = "- pay";


    /**
     * Создать заказ
     * @param orderDto
     * @param xRequestId
     * @param xUserId
     * @return
     */
    @Override
    public Order create(OrderDto orderDto, String xRequestId, String xUserId) {
        log.info("-- 1 create --");
        if (! wareHouseReserve(xRequestId, xUserId, orderDto)) {
            log.info("-- 2 create --");
            throw new TheProductIsOutOfStock();
        }
//        String payment = paymentPay(xRequestId, xUserId, orderDto.getPrice());
//        boolean result = Boolean.parseBoolean(payment);
//        log.info("-- 2 create -- result" + result);
//        if (result) {
        log.info("-- 3 create --");
        return saveOrder(orderDto, xRequestId, xUserId);
//        }
//        return callBilling(orderDto, xRequestId, xUserId);
    }

    @Override
    public Order getToId(UUID uuid) {
        log.info("-- 1 getToId --");
        log.info("-- 1 getToId -- uuid " + uuid);
        log.info("-- 1 getToId -- findById");
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

    /**
     * выбрать место доставки
     * @param xRequestId
     * @param xUserId
     * @param deliveryLocationDto
     */
    @Override
    public Order chooseADeliveryLocation(String xRequestId, String xUserId, DeliveryLocationDto deliveryLocationDto) {
        log.info("--- chooseADeliveryLocation  1 ---");
        log.info("--- chooseADeliveryLocation  1 --- xRequestId - " + xRequestId);
        log.info("--- chooseADeliveryLocation  1 --- xUserId - " + xUserId);
        log.info("--- chooseADeliveryLocation  1 --- deliveryLocationDto - " + deliveryLocationDto);
        List<Order> orderL = orderRepository.findAllByUserId(Long.parseLong(xUserId));
        log.info("--- chooseADeliveryLocation  2 ---");
        Optional<Order> orderOp = orderRepository.findByUserId(Long.parseLong(xUserId));
        log.info("--- chooseADeliveryLocation  3 ---");
        if (orderOp.isEmpty()) {
            log.info("--- chooseADeliveryLocation  4 ---");
            throw new BadRequestException();
        }
        log.info("--- chooseADeliveryLocation  5 ---");
        Order order = orderOp.get();
        order.setLocality(deliveryLocationDto.getLocality());
        order.setStreet(deliveryLocationDto.getStreet());
        order.setHouseNumber(deliveryLocationDto.getHouseNumber());
        order.setStructure(deliveryLocationDto.getStructure());
        order.setFlat(deliveryLocationDto.getFlat());
        order.setEntrance(deliveryLocationDto.getEntrance());
        order.setFloor(deliveryLocationDto.getFloor());
        order.setComment(deliveryLocationDto.getComment());
        log.info("--- chooseADeliveryLocation  6 --- order - " + order);
        Order resultOrder = orderRepository.save(order);
        log.info("--- chooseADeliveryLocation  7 --- order " + order);
        return resultOrder;
    }

    /**
     * выбрать способ оплаты
     * @param xRequestId
     * @param xUserId
     * @param paymentMethod
     */
    @Override
    public Order chooseAPaymentMethod(String xRequestId, String xUserId, PaymentMethod paymentMethod) {
        log.info("--- chooseAPaymentMethod  1 ---");
        log.info("--- chooseAPaymentMethod  1 --- xRequestId - " + xRequestId);
        log.info("--- chooseAPaymentMethod  1 --- xUserId - " + xUserId);
        log.info("--- chooseAPaymentMethod  1 --- paymentMethod - " + paymentMethod);
        List<Order> orderL = orderRepository.findAllByUserId(Long.parseLong(xUserId));
        log.info("--- chooseAPaymentMethod  2 ---");
        Optional<Order> orderOp = orderRepository.findByUserId(Long.parseLong(xUserId));
        log.info("--- chooseAPaymentMethod  3 ---");
        if (orderOp.isEmpty()) {
            log.info("--- chooseAPaymentMethod  4 ---");
            throw new BadRequestException();
        }
        log.info("--- chooseAPaymentMethod  5 ---");
        Order order = orderOp.get();
        order.setPaymentMethod(paymentMethod);
        log.info("--- chooseAPaymentMethod  6 --- order - " + order);
        Order resultOrder = orderRepository.save(order);
        log.info("--- chooseAPaymentMethod  7 --- order - " + order);
        return resultOrder;
    }

    /**
     * Выбрать время доставки
     * @param xRequestId
     * @param xUserId
     * @param dateTime
     */
    @Override
    public Order chooseTheDeliveryTime(String xRequestId, String xUserId, String dateTime) {
        log.info("--- chooseTheDeliveryTime  1 ---");
        log.info("--- chooseTheDeliveryTime  1 --- xRequestId - " + xRequestId);
        log.info("--- chooseTheDeliveryTime  1 --- xUserId - " + xUserId);
        log.info("--- chooseTheDeliveryTime  1 --- dateTime - " + dateTime);
        List<Order> orderL = orderRepository.findAllByUserId(Long.parseLong(xUserId));
        log.info("--- chooseTheDeliveryTime  2 ---");
        Optional<Order> orderOp = orderRepository.findByUserId(Long.parseLong(xUserId));
        log.info("--- chooseTheDeliveryTime  3 ---");
        if (orderOp.isEmpty()) {
            log.info("--- chooseTheDeliveryTime  4 ---");
            throw new BadRequestException();
        }
        log.info("--- chooseTheDeliveryTime  5 ---");
        Order order = orderOp.get();
        order.setDateTime(dateTime);
        log.info("--- chooseTheDeliveryTime  6 --- order - " + order);
        Order resultOrder = orderRepository.save(order);
        log.info("--- chooseTheDeliveryTime  7 --- order - " + order);
        return resultOrder;
    }

    /**
     * Зарезервировать товар
     * @param xRequestId
     * @param xUserId
     * @param orderDto
     * @return
     */
    private boolean wareHouseReserve(String xRequestId, String xUserId, OrderDto orderDto) {
        log.info("-- wareHouseReserve 1 --");
        RestTemplate rt = new RestTemplate();
        log.info("-- wareHouseReserve 1 -- " + urlWarehouse);
        URI uri;
        try {
            log.info("-- wareHouseReserve 2 --");
            uri = new URI(urlWarehouse);
            log.info("-- wareHouseReserve 2 --" + uri.toURL());
        } catch (URISyntaxException e) {
            log.info("URISyntaxException - " + e);
            return false;
        } catch (MalformedURLException e) {
            log.info("MalformedURLException - " + e);
            return false;
        }
        log.info("-- wareHouseReserve 3 --");
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("Content-Type", "application/json");
        log.info("-- wareHouseReserve 3 -- X-Request-Id - " + xRequestId);
        headers.add("X-Request-Id", xRequestId);
        log.info("-- wareHouseReserve 3 -- X-UserId - " + xUserId);
        headers.add("X-UserId", String.valueOf(xUserId));
        ProductDto productDto = new ProductDto(orderDto.getProductId(), orderDto.getCount());
        RequestEntity<ProductDto> requestEntity = new RequestEntity<>(productDto, headers, HttpMethod.POST, uri);
        log.info("-- wareHouseReserve 4 --");
        log.info("-- wareHouseReserve 4 -- " + productDto);
        rt.exchange(requestEntity, String.class);
        log.info("-- wareHouseReserve 5 --");
        return true;
    }

    /**
     * Зарезервировать курьера
     * @param xRequestId
     * @param xUserId
     * @return
     */
    @Override
    public boolean courierReserve(String xRequestId, String xUserId) {
        log.info("-- 1 courierReserve --");
        Order order = getOrder(xUserId);
        RestTemplate rt = new RestTemplate();
        log.info("-- 1 courierReserve -- " + urlDelivery);
        URI uri ;
        try {
            log.info("-- 2 courierReserve --");
            uri = new URI(urlDelivery);
            log.info("-- 2 courierReserve --" + uri.toURL());
        } catch (URISyntaxException e) {
            log.info("URISyntaxException - " + e);
            return false;
        } catch (MalformedURLException e) {
            log.info("MalformedURLException - " + e);
            return false;
        }
        log.info("-- 3 courierReserve --");
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("Content-Type", "application/json");
        log.info("-- 3 -- X-Request-Id - " + xRequestId);
        headers.add("X-Request-Id", xRequestId);
        log.info("-- 3 -- X-UserId - " + xUserId);
        headers.add("X-UserId", xUserId);
        log.info("-- 3 courierReserve -- dateTime - " + order.getDateTime());
        RequestEntity<String> requestEntity = new RequestEntity<>(order.getDateTime(), headers, HttpMethod.POST, uri);
        log.info("-- 4 courierReserve --");
        log.info("-- 4 --" + requestEntity.getBody());
        ResponseEntity<String> result = rt.exchange(requestEntity, String.class);
        log.info("-- 5 --");
        log.info("-- 5 -- " + result.getBody());
        return result.getBody() != null;
    }

    /**
     * Оплатить заказ
     * @param xRequestId
     * @param xUserId
     * @return
     */
    @Override
    public boolean payAndSendMessage(String xRequestId, String xUserId) {
        log.info("--- payAndSendMessage 1 ---");
        Order order = getOrder(xUserId);
        log.info("--- payAndSendMessage 2 --- order - " + order);
        if (!validateOrder(order)) {
            log.info("--- payAndSendMessage 3 ---");
            return false;
        }
        log.info("--- payAndSendMessage 4 ---");
        if (!pay(xRequestId, xUserId, order)) {
            log.info("--- payAndSendMessage 5 ---");
            sendMessageToNotificafion(xRequestId, Long.parseLong(xUserId), OperationNoPay, 0, 0);
            log.info("--- payAndSendMessage 6 ---");
            return false;
        }
        log.info("--- payAndSendMessage 7 ---");
        sendMessageToNotificafion(xRequestId, Long.parseLong(xUserId), OperationPay, order.calculatePayMoney(), 0);
        log.info("--- payAndSendMessage 8 ---");
        clear(UUID.fromString(xRequestId));
        log.info("--- payAndSendMessage 9 ---");
        return true;
    }

    /**
     * очистка корзины
     * @param uuid
     * @return
     */
    @Override
    public boolean clear(UUID uuid) {
        log.info("--- clear 1 --- uuid - " + uuid);
        orderRepository.deleteById(uuid);
        log.info("--- clear 2 ---");
        return true;
    }


    private Order getOrder(String xUserId) {
        log.info("--- pay  1 --- xUserId - " + xUserId);
        List<Order> orderL = orderRepository.findAllByUserId(Long.parseLong(xUserId));
        log.info("--- pay  2 --- size - " + orderL.size());
        Optional<Order> orderOp = orderRepository.findByUserId(Long.parseLong(xUserId));
        log.info("--- pay  3 ---");
        if (orderOp.isEmpty()) {
            log.info("--- pay  4 ---");
            throw new BadRequestException();
        }
        log.info("--- pay 5 ---");
        return orderOp.get();
    }

    private boolean validateOrder(Order order) {
        log.info("--- validateOrder  1 ---");
        return checkDeliveryLocation(order)
            && checkPaymentMethod(order)
            && checkDeliveryTime(order);
    }

    private boolean checkDeliveryLocation(Order order) {
        log.info("--- checkDeliveryLocation  1 ---");
        return  (order.getLocality() != null
            && order.getStreet() != null
            && order.getHouseNumber() != null);
    }

    private boolean checkPaymentMethod(Order order) {
        log.info("--- checkPaymentMethod  1 ---");
        return order.getPaymentMethod() != PaymentMethod.NOSELECT;
    }

    private boolean checkDeliveryTime(Order order) {
        log.info("--- checkDeliveryTime  1 ---");
        return order.getDateTime() != null;
    }

    private boolean pay(String xRequestId, String xUserId, Order order) {
        log.info("--- pay  1 ---");
        log.info("--- pay  1 --- xRequestId - " + xRequestId);
        log.info("--- pay  1 --- xUserId - " + xUserId);
        boolean pay;
        log.info("--- pay 6 ---");
        double money;
        switch (order.getPaymentMethod()){
            case PAYMENT:
                log.info("--- pay 7 ---");
                money = order.calculatePayMoney();
                log.info("--- pay 8 ---");
                pay = paymentPay(xRequestId, xUserId,money);
                log.info("--- pay 9 ---");
                return pay;
            case BILLING:
                log.info("--- pay 10 ---");
                money = order.calculatePayMoney();
                pay = callBilling(xRequestId, xUserId, money);
                log.info("--- pay 11 ---");
                return pay;
        }
        return false;
    }

    private Order saveOrder(OrderDto orderDto, String xRequestId, String xUserId) {
        log.info("-- 1 saveOrder --");
        log.info("-- 1 saveOrder -- OrderDto - " + orderDto);
        Order order = mapper.map(orderDto, Order.class);
        order.setId(UUID.fromString(xRequestId));
        log.info("-- 1 saveOrder -- Order - " + order);
        log.info("-- 1 saveOrder -- xRequestId - " + xRequestId);
        log.info("-- 1 saveOrder -- xUserId - " + xUserId);
        log.info("-- 1 saveOrder -- save");
        order.setXRequestId(xRequestId);
        order.setUserId(Long.parseLong(xUserId));
        Order orderResult = orderRepository.save(order);
        log.info("-- 2 saveOrder money call notufication minus --");
        log.info("-- 3 saveOrder --");
        return orderResult;
    }

    private boolean paymentPay(String xRequestId, String xUserId, double money) {
        log.info("-- 1 paymentPay --");
        RestTemplate rt = new RestTemplate();
        log.info("-- 1 paymentPay -- " + url);
        URI uri ;
        try {
            log.info("-- 2 paymentPay --");
            uri = new URI(url);
            log.info("-- 2 paymentPay --" + uri.toURL());
        } catch (URISyntaxException e) {
            log.info("URISyntaxException - " + e);
            return false;
        } catch (MalformedURLException e) {
            log.info("MalformedURLException - " + e);
            return false;
        }
        log.info("-- 3 paymentPay --");
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("Content-Type", "application/json");
        log.info("-- 3 -- X-Request-Id - " + xRequestId);
        headers.add("X-Request-Id", xRequestId);
        log.info("-- 3 -- X-UserId - " + xUserId);
        headers.add("X-UserId", xUserId);
        PayMoneyDto payMoneyDto = new PayMoneyDto();
        payMoneyDto.setAmount(money);
        RequestEntity<PayMoneyDto> requestEntity = new RequestEntity<>(payMoneyDto, headers, HttpMethod.POST, uri);
        log.info("-- 4 --");
        log.info("-- 4 --" + requestEntity.getBody());
        ResponseEntity<String> result = rt.exchange(requestEntity, String.class);
        log.info("-- 5 --");
        log.info("-- 5 -- " + result.getBody());
        return result.getBody() != null;
    }

    private void sendMessageToNotificafion(String xRequestId, long userId, String operation,
                                           double count, double total) {
        log.info("-- 1 --");
        RestTemplate rt = new RestTemplate();
        log.info("-- 1 -- " + urlNotific);
        URI uri;
        try {
            log.info("-- 2 --");
            uri = new URI(urlNotific);
            log.info("-- 2 --" + uri.toURL());
        } catch (URISyntaxException e) {
            log.info("URISyntaxException - " + e);
            return;
        } catch (MalformedURLException e) {
            log.info("MalformedURLException - " + e);
            return;
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

    private boolean callBilling(String xRequestId, String xUserId, double money) {
        log.info("-- callBilling 1 --");
        RestTemplate rt = new RestTemplate();
        log.info("-- callBilling 1 -- " + urlBilling);
        URI uri;
        try {
            log.info("-- callBilling 2 --");
            uri = new URI(urlBilling);
            log.info("-- callBilling 2 --" + uri.toURL());
        } catch (URISyntaxException e) {
            log.info("URISyntaxException - " + e);
            return false;
        } catch (MalformedURLException e) {
            log.info("MalformedURLException - " + e);
            return false;
        }
        log.info("-- callBilling 3 --");
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("Content-Type", "application/json");
        log.info("-- callBilling 3 -- X-Request-Id - " + xRequestId);
        headers.add("X-Request-Id", xRequestId);
        log.info("-- callBilling 3 -- X-UserId - " + xUserId);
        headers.add("X-UserId", xUserId);
        RequestEntity<Double> requestEntity = new RequestEntity<>(money, headers, HttpMethod.POST, uri);
        log.info("-- callBilling 4 --");
        log.info("-- callBilling 4 --" + requestEntity.getBody());
        rt.exchange(requestEntity, String.class);
        log.info("-- callBilling 5 --");
        return true;
    }

    //    private Order callBilling(OrderDto orderDto, String xRequestId, String xUserId) {
//        log.info("-- 1 callBilling --");
//        double balance = getBalanceFromAccount(xRequestId, xUserId);
//        log.info("-- 2 callBilling -- balance" + balance);
//        if (withdrawMoneyFromBillingService(orderDto, xRequestId, xUserId, balance)) {
//            log.info("-- 5 create --");
//            Order order = mapper.map(orderDto, Order.class);
//            log.info("-- 5 create --");
//            order.setId(UUID.fromString(xRequestId));
//            log.info("-- 6 create --");
//            log.info("-- 6 create -- findById");
//            Optional<Order> orderOptional = orderRepository.findById(order.getId());
//            log.info("-- 7 create --");
//            if (orderOptional.isPresent()) {
//                log.info("-- 8 create --");
//                throw new ConflictException();
//            }
//            double total = balance - orderDto.getPrice();
//            return saveOrder(orderDto, xRequestId, xUserId, total, "-");
//        } else {
//            log.info("-- 9 create money call notufication cancel no money  --");
//            sendMessageToNotificafion(xRequestId, Long.parseLong(xUserId), "Cancel Order - no money",
//                0, 0);
//            log.info("--- 10 create ---");
//            return null;
//        }
//    }

//    private double getBalanceFromAccount(String xRequestId, String xUserId) {
//        log.info("-- 1 getBalanceFromAccount --");
//        RestTemplate rt = new RestTemplate();
//        log.info("-- 1 -- " + url + "get");
//        URI uri = null;
//        try {
//            log.info("-- 2 --");
//            uri = new URI(url + "get");
//            log.info("-- 2 --" + uri.toURL());
//        } catch (URISyntaxException e) {
//            log.info("URISyntaxException - " + e);
//        } catch (MalformedURLException e) {
//            log.info("MalformedURLException - " + e);
//        }
//        log.info("-- 3 --");
//        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
//        headers.add("Content-Type", "application/json");
//        log.info("-- 3 -- X-Request-Id - " + xRequestId);
//        headers.add("X-Request-Id", xRequestId);
//        log.info("-- 3 -- X-UserId - " + xUserId);
//        headers.add("X-UserId", xUserId);
//
//        RequestEntity<String> requestEntity = new RequestEntity<>(headers, HttpMethod.GET, uri);
//        log.info("-- 4 --");
//        log.info("-- 4 --" + requestEntity.getBody());
//        ResponseEntity<Double> balance = rt.exchange(requestEntity, Double.class);
//        log.info("-- 5 --");
//        log.info("-- 5 -- " + balance.getBody());
//        return balance.getBody();
//    }
//
//    private boolean withdrawMoneyFromBillingService(OrderDto orderDto, String xRequestId, String xUserId,
//                                                    double balance) {
//        if (balance < orderDto.getPrice()) {
//            log.info("-- 1 --");
//            log.info(balance + "  " + orderDto.getPrice());
//            log.info("-- 2 --");
//            return false;
//        }
//        double total = balance - orderDto.getPrice();
//        log.info("-- 1  withdrawMoneyFromBillingService --");
//        RestTemplate rt = new RestTemplate();
//        log.info("-- 1 -- " + url + "pay");
//        URI uri = null;
//        try {
//            log.info("-- 2 --");
//            uri = new URI(url + "pay");
//            log.info("-- 2 --" + uri.toURL());
//        } catch (URISyntaxException e) {
//            log.info("URISyntaxException - " + e);
//        } catch (MalformedURLException e) {
//            log.info("MalformedURLException - " + e);
//        }
//        log.info("-- 3 --");
//        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
//        headers.add("Content-Type", "application/json");
//        log.info("-- 3 -- X-Request-Id - " + xRequestId);
//        headers.add("X-Request-Id", xRequestId);
//        log.info("-- 3 -- X-UserId - " + xUserId);
//        headers.add("X-UserId", xUserId);
//        MinusMoney minusMoney = new MinusMoney(orderDto.getPrice(), total);
//        log.info("-- 3 -- MinusMoney - " + minusMoney);
//        RequestEntity<MinusMoney> requestEntity = new RequestEntity<>(minusMoney, headers, HttpMethod.POST, uri);
//        log.info("-- 4 --");
//        log.info("-- 4 --" + requestEntity.getBody());
//        try {
//            log.info("-- 5 --");
//            rt.exchange(requestEntity, String.class);
//        } catch (RestClientException e) {
//            log.info("-- 6 --");
//            log.info(e.toString());
//            return false;
//        }
//        log.info("-- 7 --");
//        return true;
//    }
}
