package ru.andreybaryshnikov.deliveryservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.andreybaryshnikov.deliveryservice.model.Delivery;
import ru.andreybaryshnikov.deliveryservice.repository.DeliveryRepository;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeliveryServiceImpl implements DeliveryService{
    private final DeliveryRepository deliveryRepository;

    @Override
    public boolean reserve(String xRequestId, long xUserId, String localDateTime) {
        log.info("--- reserve 1 ---");
        log.info("--- reserve 1 --- xRequestId - " + xRequestId);
        log.info("--- reserve 1 --- xUserId - " + xUserId);
        log.info("--- reserve 1 --- localDateTime - " + localDateTime);
        if (! isPossibleReserve(localDateTime)) {
            log.info("--- reserve 2 ---");
            return false;
        }
        log.info("--- reserve 3 ---");
        save(xRequestId, xUserId, localDateTime);
        log.info("--- reserve 4 ---");
        return true;
    }

    private void save(String xRequestId, long xUserId, String localDateTime) {
        log.info("--- save 1 ---");
        Delivery delivery = new Delivery();
        delivery.setId(UUID.fromString(xRequestId));
        delivery.setUserId(xUserId);
        delivery.setLocalDateTime(localDateTime);
        log.info("--- save 2 --- delivery " + delivery);
        deliveryRepository.save(delivery);
        log.info("--- save 3 ---");
    }

    private boolean isPossibleReserve(String localDateTime) {
        // возможность зарезервировать курьера на это время
        log.info("--- isPossibleReserve ---");
        return true;
    }
}
