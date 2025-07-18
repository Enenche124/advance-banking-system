package com.apostle.data.repositories;

import com.apostle.data.model.BankAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BankAccountRepository extends JpaRepository<BankAccount, Long>{
    boolean existsByAccountNumber(String accountNumber);
    Optional<BankAccount> findByAccountNumber(String accountNumber);
}
