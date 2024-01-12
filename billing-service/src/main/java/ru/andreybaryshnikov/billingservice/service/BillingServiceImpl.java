package ru.andreybaryshnikov.billingservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.andreybaryshnikov.billingservice.exception.BadRequestException;
import ru.andreybaryshnikov.billingservice.model.Account;
import ru.andreybaryshnikov.billingservice.model.MinusMoney;
import ru.andreybaryshnikov.billingservice.model.PlusMoney;
import ru.andreybaryshnikov.billingservice.repository.BillingRepository;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class BillingServiceImpl implements BillingService {
    private final BillingRepository billingRepository;
    private final String plusOperation = "+";
    private final String minusOperation = "-";

    @Override
    public Account addMoney(String xRequestId, long userId, PlusMoney money) {
        var accountOptional = billingRepository.findById(userId);
        if (accountOptional.isPresent()) {
            return updateAccount(xRequestId, userId, money.getPlus(), money.getTotal(),
                accountOptional.get(), plusOperation);
        } else {
            return createAccount(xRequestId, userId, money);
        }
    }

    private Account createAccount(String xRequestId, long userId, PlusMoney money) {
        Account account = new Account();
        account.setBalance(money.getTotal());
        account.setId(userId);
        account.setXRequestId(UUID.fromString(xRequestId));
        return billingRepository.save(account);
    }

    private Account updateAccount(String xRequestId, long userId, double count, double total,
                                  Account account, String operation) {
        account.setBalance(total);
        account.setXRequestId(UUID.fromString(xRequestId));
        return billingRepository.save(account);
    }

    @Override
    public Account subMoney(String xRequestId, long userId, MinusMoney money) {
        var accountOptional = billingRepository.findById(userId);
        if (accountOptional.isPresent()) {
            return updateAccount(xRequestId, userId, money.getMinus(), money.getTotal(), accountOptional.get(),
                minusOperation);
        } else {
            throw new BadRequestException();
        }
    }

    @Override
    public double getBalance(String xRequestId, long userId) {
        log.info("-- 1 getBalance --");
        log.info("-- 1 getBalance xRequestId -- " + xRequestId);
        log.info("-- 1 getBalance userId -- " + userId);
        var accountOptional = billingRepository.findById(userId);
        log.info("-- 2 getBalance --");
        if (accountOptional.isPresent()) {
            log.info(String.valueOf(accountOptional.get().getBalance()));
            log.info("----");
            return accountOptional.get().getBalance();
        }
        log.info("-- 3 getBalance --");
        throw new BadRequestException();
    }
}
