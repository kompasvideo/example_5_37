package ru.andreybaryshnikov.paymentservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.andreybaryshnikov.paymentservice.model.Payment;
import ru.andreybaryshnikov.paymentservice.model.dto.OrderDto;
import ru.andreybaryshnikov.paymentservice.repository.PaymentRepository;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class PaymentServiceImpl implements PaymentService {
    private final PaymentRepository paymentRepository;
//    @Value("${billing.uri}")
//    private String url;

    @Override
    public boolean pay(String xRequestId, String xUserId, OrderDto orderDto) {
        log.info("--- 1 payService ---");
        boolean pay = false;
//        switch (orderDto.getPaymentMethod()) {
//            case PAYMENT:
                log.info("--- 2 payService ---");
                pay = callPay(xRequestId, xUserId, orderDto);
                log.info("--- 3 payService ---");
                logPay(xRequestId, xUserId, orderDto);
                log.info("--- 4 payService ---");
//                break;
//            case BILLING:
//                log.info("--- 5 payService ---");
//                pay = callBilling(xRequestId, xUserId, orderDto);
//                log.info("--- 6 payService ---");
//                logPay(xRequestId, xUserId, orderDto);
//                log.info("--- 7 payService ---");
//                break;
//        }
        log.info("--- 8 payService ---");
        return pay;
    }

    private boolean callPay(String xRequestId, String xUserId, OrderDto orderDto) {
        try {
            log.info("--- 1 callPay ---");
            Thread.sleep(1_000);
            log.info("--- 2 callPay ---");
            // call pay
        } catch (InterruptedException e) {
            log.info("--- 3 callPay ---");
            throw new RuntimeException(e);
        }
        log.info("--- 4 callPay ---");
        return true;
    }

    private void logPay(String xRequestId, String xUserId, OrderDto orderDto) {
        log.info("--- 1 logPay ---");
        Optional<Payment> paymentOptional = paymentRepository.findById(UUID.fromString(xRequestId));
        log.info("--- 2 logPay ---");
        if (paymentOptional.isPresent()) {
            log.info("--- 3 logPay ---");
            return;
        }
        log.info("--- 4 logPay ---");
        Payment payment = new Payment();
        payment.setId(UUID.fromString(xRequestId));
        payment.setUserId(xUserId);
        payment.setMoney(orderDto.getPrice());
        log.info("--- 5 logPay ---");
        paymentRepository.save(payment);
        log.info("--- 6 logPay ---");
    }

//    private boolean callBilling(String xRequestId, String xUserId, OrderDto orderDto) {
//        log.info("-- callBilling 1 --");
//        RestTemplate rt = new RestTemplate();
//        log.info("-- callBilling 1 -- " + url);
//        URI uri = null;
//        try {
//            log.info("-- callBilling 2 --");
//            uri = new URI(url);
//            log.info("-- callBilling 2 --" + uri.toURL());
//        } catch (URISyntaxException e) {
//            log.info("URISyntaxException - " + e);
//        } catch (MalformedURLException e) {
//            log.info("MalformedURLException - " + e);
//        }
//        log.info("-- callBilling 3 --");
//        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
//        headers.add("Content-Type", "application/json");
//        log.info("-- callBilling 3 -- X-Request-Id - " + xRequestId);
//        headers.add("X-Request-Id", xRequestId);
//        log.info("-- callBilling 3 -- X-UserId - " + xUserId);
//        headers.add("X-UserId", xUserId);
//        MinusMoney minusMoney = new MinusMoney( 1000, 1000);
//        RequestEntity<MinusMoney> requestEntity = new RequestEntity<>(minusMoney, headers, HttpMethod.POST, uri);
//        log.info("-- callBilling 4 --");
//        log.info("-- callBilling 4 --" + requestEntity.getBody());
//        rt.exchange(requestEntity, String.class);
//        log.info("-- callBilling 5 --");
//        return true;
//    }
}
