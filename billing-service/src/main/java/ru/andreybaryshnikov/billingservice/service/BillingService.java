package ru.andreybaryshnikov.billingservice.service;

import ru.andreybaryshnikov.billingservice.model.Account;
import ru.andreybaryshnikov.billingservice.model.MinusMoney;
import ru.andreybaryshnikov.billingservice.model.PlusMoney;

public interface BillingService {
    Account addMoney(String xRequestId, long userId, PlusMoney money);

    Account pay(String xRequestId, long userId, MinusMoney money);

    double getBalance(String xRequestId, long id);

    Account withdraw(String xRequestId, long id, MinusMoney money);
}
