package com.apostle.services;

import com.apostle.dtos.requests.DepositRequest;
import com.apostle.dtos.requests.SendMoneyRequest;
import com.apostle.dtos.responses.TransactionResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface TransactionService {
    TransactionResponse deposit(DepositRequest request);
    TransactionResponse transfer(SendMoneyRequest request);
    List<TransactionResponse> getTransactionsForAccount(Long accountId);
    TransactionResponse getTransactionById(Long transactionId);
}
