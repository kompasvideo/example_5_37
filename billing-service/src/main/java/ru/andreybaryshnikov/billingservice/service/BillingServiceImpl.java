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
        log.info("-- 1 addMoney --");
        var accountOptional = billingRepository.findById(userId);
        log.info("-- 2 addMoney --");
        if (accountOptional.isPresent()) {
            log.info("-- 3 addMoney --");
            return updateAccount(xRequestId, money.getTotal(),
                accountOptional.get());
        }
        log.info("-- 4 addMoney --");
        return createAccount(xRequestId, userId, money);
    }

    private Account createAccount(String xRequestId, long userId, PlusMoney money) {
        log.info("-- 1 createAccount --");
        Account account = new Account();
        log.info("-- 2 createAccount --");
        account.setBalance(money.getTotal());
        account.setId(userId);
        account.setXRequestId(UUID.fromString(xRequestId));
        log.info("-- 3 createAccount --");
        return billingRepository.save(account);
    }

    private Account updateAccount(String xRequestId, double total,
                                  Account account) {
        log.info("-- 1 updateAccount --");
        account.setBalance(total);
        account.setXRequestId(UUID.fromString(xRequestId));
        log.info("-- 2 updateAccount --");
        return billingRepository.save(account);
    }

    @Override
    public Account pay(String xRequestId, long userId, MinusMoney money) {
        log.info("-- 1 pay --");
        var accountOptional = billingRepository.findById(userId);
        log.info("-- 1 pay -- ");
        if (accountOptional.isEmpty()) {
            log.info("-- 1 pay -- ");
            throw new BadRequestException();
        }
        log.info("-- 1 pay -- accountOptional - " + accountOptional.get());
        return updateAccount(xRequestId, money.getTotal(), accountOptional.get());
    }

    @Override
    public double getBalance(String xRequestId, long userId) {
        log.info("-- 1 getBalance --");
        log.info("-- 1 getBalance xRequestId -- " + xRequestId);
        log.info("-- 1 getBalance userId -- " + userId);
        var accountOptional = billingRepository.findById(userId);
        log.info("-- 2 getBalance --");
        if (accountOptional.isEmpty()) {
            log.info("-- 3 getBalance --");
            throw new BadRequestException();
        }
        log.info(String.valueOf(accountOptional.get().getBalance()));
        log.info("-- 1 pay -- accountOptional - " + accountOptional.get());
        return accountOptional.get().getBalance();
    }

    @Override
    public Account withdraw(String xRequestId, long userId, MinusMoney money) {
        log.info("--- withdraw service 1 ---");
        var accountOptional = billingRepository.findById(userId);
        log.info("--- withdraw service 2 ---");
        if (accountOptional.isEmpty()) {
            log.info("--- withdraw service 3 ---");
            throw new BadRequestException();
        }
        log.info("--- withdraw service 4 --- withdraw - " + money.getMinus());
        Account account = updateAccount(xRequestId, money.getTotal(), accountOptional.get());
        log.info("--- withdraw service 4 --- account - " + account);
        return account;
    }
}
