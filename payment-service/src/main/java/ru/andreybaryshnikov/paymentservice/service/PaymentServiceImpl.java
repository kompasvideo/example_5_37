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

    @Override
    public boolean pay(String xRequestId, String xUserId, OrderDto orderDto) {
        log.info("--- 1 payService ---");
        log.info("--- 2 payService ---");
        boolean pay = callPay();
        log.info("--- 3 payService ---");
        logPay(xRequestId, xUserId, orderDto);
        log.info("--- 4 payService ---");
        log.info("--- 8 payService ---");
        return pay;
    }

    private boolean callPay() {
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
}
