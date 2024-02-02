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
    private String urlWarehouseReserve;
    @Value("${warehouse.uriPay}")
    private String urlWarehousePay;
    @Value("${warehouse.uriCancel}")
    private String urlWarehouseCancel;
    @Value("${billing.uri}")
    private String urlBilling;
    @Value("${delivery.uri}")
    private String urlDelivery;
    @Value("${delivery.uriCancel}")
    private String urlDeliveryCancel;
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
        log.info("-- 3 create --");
        return saveOrder(orderDto, xRequestId, xUserId);
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
        log.info("--- chooseADeliveryLocation  2 --- deliveryLocationDto - " + deliveryLocationDto);
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
        log.info("--- chooseAPaymentMethod  2 --- paymentMethod - " + paymentMethod);
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
        log.info("--- chooseTheDeliveryTime  2 --- dateTime - " + dateTime);
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
     * Зарезервировать курьера
     * @param xRequestId
     * @param xUserId
     * @return
     */
    @Override
    public Order courierReserve(String xRequestId, String xUserId) {
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
            throw new BadRequestException();
        } catch (MalformedURLException e) {
            log.info("MalformedURLException - " + e);
            throw new BadRequestException();
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
        ResponseEntity<Boolean> result = rt.exchange(requestEntity, Boolean.class);
        log.info("-- 5 --");
        log.info("-- 5 -- " + result.getBody());
        if (!Boolean.TRUE.equals(result.getBody())) {
            log.info("-- 6 --");
            throw new BadRequestException();
        }
        log.info("-- 7 --");
        order.setReserveCourier(true);
        Order resultOrder =  orderRepository.save(order);
        log.info("-- 8 --");
        return resultOrder;
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
            throw new BadRequestException();
        }
        log.info("--- payAndSendMessage 4 ---");
        if (!pay(xRequestId, xUserId, order)) {
            log.info("--- payAndSendMessage 5 ---");
            cancel(xRequestId, xUserId, order);
            log.info("--- payAndSendMessage 6 ---");
            throw new BadRequestException();
        }
        log.info("--- payAndSendMessage 7 ---");
        sendMessageToNotificafion(xRequestId, Long.parseLong(xUserId), OperationPay, order.calculatePayMoney(), 0);
        log.info("--- payAndSendMessage 8 ---");
        clear(UUID.fromString(xRequestId));
        wareHousePay(xRequestId, xUserId);
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
        log.info("-- wareHouseReserve 1 -- " + urlWarehouseReserve);
        URI uri;
        try {
            log.info("-- wareHouseReserve 2 --");
            uri = new URI(urlWarehouseReserve);
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
        ResponseEntity<Boolean> result = rt.exchange(requestEntity, Boolean.class);
        log.info("-- wareHouseReserve 5 --");
        return Boolean.TRUE.equals(result.getBody());
    }

    private boolean wareHousePay(String xRequestId, String xUserId) {
        log.info("-- wareHousePay 1 --");
        RestTemplate rt = new RestTemplate();
        log.info("-- wareHousePay 1 -- " + urlWarehousePay);
        URI uri;
        try {
            log.info("-- wareHousePay 2 --");
            uri = new URI(urlWarehousePay);
            log.info("-- wareHousePay 2 --" + uri.toURL());
        } catch (URISyntaxException e) {
            log.info("URISyntaxException - " + e);
            return false;
        } catch (MalformedURLException e) {
            log.info("MalformedURLException - " + e);
            return false;
        }
        log.info("-- wareHousePay 3 --");
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("Content-Type", "application/json");
        log.info("-- wareHousePay 3 -- X-Request-Id - " + xRequestId);
        headers.add("X-Request-Id", xRequestId);
        log.info("-- wareHousePay 3 -- X-UserId - " + xUserId);
        headers.add("X-UserId", String.valueOf(xUserId));
        RequestEntity<Object> requestEntity = new RequestEntity<>(headers, HttpMethod.POST, uri);
        log.info("-- wareHousePay 4 --");
        ResponseEntity<Boolean> result = rt.exchange(requestEntity, Boolean.class);
        log.info("-- wareHousePay 5 --");
        return Boolean.TRUE.equals(result.getBody());
    }

    /**
     * отмена оплаты
     * @param xRequestId
     * @param xUserId
     * @param order
     */
    private void cancel(String xRequestId, String xUserId, Order order) {
        log.info("--- cancel 1 --- xRequestId - " + xRequestId);
        log.info("--- cancel 2 --- xUserId - " + xUserId);
        log.info("--- cancel 3 --- order - " + order);
        sendMessageToNotificafion(xRequestId, Long.parseLong(xUserId), OperationNoPay, 0, 0);
        log.info("--- cancel 4 ---");
        order.setReserveCourier(false);
        log.info("--- cancel 5 ---");
        log.info("--- cancel 6 ---");
        cancelDeliveryCourier(xRequestId, xUserId);
        log.info("--- cancel 7 ---");
        order.setReserveProduct(false);
        log.info("--- cancel 8 ---");
        orderRepository.save(order);
        log.info("--- cancel 9 ---");
        cancelReserveProduct(xRequestId, xUserId);
        log.info("--- cancel 10 ---");
    }

    private boolean cancelDeliveryCourier(String xRequestId, String xUserId) {
        // отменить курьера
        log.info("-- cancelDeliveryCourier 1 --");
        RestTemplate rt = new RestTemplate();
        log.info("-- cancelDeliveryCourier 1 -- " + urlDeliveryCancel);
        URI uri;
        try {
            log.info("-- cancelDeliveryCourier 2 --");
            uri = new URI(urlDeliveryCancel);
            log.info("-- cancelDeliveryCourier 2 --" + uri.toURL());
        } catch (URISyntaxException e) {
            log.info("URISyntaxException - " + e);
            return false;
        } catch (MalformedURLException e) {
            log.info("MalformedURLException - " + e);
            return false;
        }
        log.info("-- cancelDeliveryCourier 3 --");
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("Content-Type", "application/json");
        log.info("-- cancelDeliveryCourier 3 -- X-Request-Id - " + xRequestId);
        headers.add("X-Request-Id", xRequestId);
        log.info("-- cancelDeliveryCourier 3 -- X-UserId - " + xUserId);
        headers.add("X-UserId", String.valueOf(xUserId));
        RequestEntity<Object> requestEntity = new RequestEntity<>(headers, HttpMethod.POST, uri);
        log.info("-- cancelDeliveryCourier 4 --");
        ResponseEntity<Boolean> result = rt.exchange(requestEntity, Boolean.class);
        log.info("-- cancelDeliveryCourier 5 --");
        return Boolean.TRUE.equals(result.getBody());
    }

    private boolean cancelReserveProduct(String xRequestId, String xUserId) {
        // отменить резервирование товара
        log.info("-- wareHouseCancel 1 --");
        RestTemplate rt = new RestTemplate();
        log.info("-- wareHouseCancel 1 -- " + urlWarehouseCancel);
        URI uri;
        try {
            log.info("-- wareHouseCancel 2 --");
            uri = new URI(urlWarehouseCancel);
            log.info("-- wareHouseCancel 2 --" + uri.toURL());
        } catch (URISyntaxException e) {
            log.info("URISyntaxException - " + e);
            return false;
        } catch (MalformedURLException e) {
            log.info("MalformedURLException - " + e);
            return false;
        }
        log.info("-- wareHouseCancel 3 --");
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("Content-Type", "application/json");
        log.info("-- wareHouseCancel 3 -- X-Request-Id - " + xRequestId);
        headers.add("X-Request-Id", xRequestId);
        log.info("-- wareHouseCancel 3 -- X-UserId - " + xUserId);
        headers.add("X-UserId", String.valueOf(xUserId));
        RequestEntity<Object> requestEntity = new RequestEntity<>(headers, HttpMethod.POST, uri);
        log.info("-- wareHouseCancel 4 --");
        ResponseEntity<Boolean> result = rt.exchange(requestEntity, Boolean.class);
        log.info("-- wareHouseCancel 5 --");
        return Boolean.TRUE.equals(result.getBody());
    }

    private Order getOrder(String xUserId) {
        log.info("--- getOrder  1 --- xUserId - " + xUserId);
        Optional<Order> orderOp = orderRepository.findByUserId(Long.parseLong(xUserId));
        log.info("--- getOrder  2 ---");
        if (orderOp.isEmpty()) {
            log.info("--- getOrder  3 ---");
            throw new BadRequestException();
        }
        log.info("--- getOrder 4 ---");
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
        log.info("--- pay  2 --- xRequestId - " + xRequestId);
        log.info("--- pay  3 --- xUserId - " + xUserId);
        boolean pay;
        log.info("--- pay 4 ---");
        double money = order.calculatePayMoney();
        log.info("--- pay 5 ---");
        switch (order.getPaymentMethod()){
            case PAYMENT:
                log.info("--- pay 6 ---");
                pay = payPaymentService(xRequestId, xUserId,money);
                log.info("--- pay 7 ---");
                return pay;
            case BILLING:
                log.info("--- pay 8 ---");
                pay = callBilling(xRequestId, xUserId, money);
                log.info("--- pay 9 ---");
                return pay;
        }
        log.info("--- pay 10 ---");
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
        order.setUserId(Long.parseLong(xUserId));
        order.setReserveProduct(true);
        Order orderResult = orderRepository.save(order);
        log.info("-- 2 saveOrder money call notufication minus --");
        log.info("-- 3 saveOrder --");
        return orderResult;
    }

    private boolean payPaymentService(String xRequestId, String xUserId, double money) {
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
        log.info("-- 4 --" + requestEntity.getBody());
        ResponseEntity<String> result = rt.exchange(requestEntity, String.class);
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
        log.info("-- callBilling 4 --" + requestEntity.getBody());
        rt.exchange(requestEntity, String.class);
        log.info("-- callBilling 5 --");
        return true;
    }
}
