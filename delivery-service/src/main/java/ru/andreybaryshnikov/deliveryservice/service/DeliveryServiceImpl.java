package ru.andreybaryshnikov.deliveryservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.andreybaryshnikov.deliveryservice.model.Delivery;
import ru.andreybaryshnikov.deliveryservice.repository.DeliveryRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeliveryServiceImpl implements DeliveryService{
    private final DeliveryRepository deliveryRepository;

    @Override
    public boolean reserveCourier(String xRequestId, long xUserId, String localDateTime) {
        log.info("--- reserve 1 ---");
        log.info("--- reserve 1 --- xRequestId - " + xRequestId);
        log.info("--- reserve 1 --- xUserId - " + xUserId);
        log.info("--- reserve 1 --- localDateTime - " + localDateTime);
        int courierId = isPossibleReserveAndGetCourierId(localDateTime);
        if (courierId <= 0) {
            log.info("--- reserve 2 ---");
            return false;
        }
        log.info("--- reserve 3 ---");
        save(xRequestId, xUserId, localDateTime, courierId);
        log.info("--- reserve 4 ---");
        return true;
    }

    @Override
    public boolean cancelCourier(String xRequestId, long xUserId) {
        log.info("--- cancel 1 ---");
        log.info("--- cancel 2 --- xRequestId - " + xRequestId);
        log.info("--- cancel 3 --- xUserId - " + xUserId);
        List<Delivery> deliveryList =  deliveryRepository.findAllByRequestId(xRequestId);
        log.info("--- cancel 4 ---");
        if (deliveryList == null || deliveryList.isEmpty()) {
            log.info("--- cancel 5 ---");
            return false;
        }
        log.info("--- cancel 6 ---");
        List<UUID> uuids = new ArrayList<>();
        for (var item : deliveryList) {
            log.info("--- cancel 7 ---");
            uuids.add(item.getId());
        }
        log.info("--- cancel 8 ---");
        deliveryRepository.deleteAllById(uuids);
        log.info("--- cancel 9 ---");
        return true;
    }

    private void save(String xRequestId, long xUserId, String localDateTime, int courierId) {
        log.info("--- save 1 ---");
        Delivery delivery = new Delivery();
        delivery.setId(UUID.fromString(xRequestId));
        delivery.setUserId(xUserId);
        delivery.setRequestId(xRequestId);
        delivery.setLocalDateTime(localDateTime);
        delivery.setCourierId(courierId);
        log.info("--- save 2 --- delivery " + delivery);
        deliveryRepository.save(delivery);
        log.info("--- save 3 ---");
    }

    private int isPossibleReserveAndGetCourierId(String localDateTime) {
        // возможность зарезервировать курьера на это время
        log.info("--- isPossibleReserve ---");
        return 1;
    }
}
