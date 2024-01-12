package ru.andreybaryshnikov.billingservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.andreybaryshnikov.billingservice.model.Account;

public interface BillingRepository extends JpaRepository<Account, Long> {
}
