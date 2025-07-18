package com.apostle.controllers;

import com.apostle.data.repositories.TransactionRepo;
import com.apostle.dtos.requests.DepositRequest;
import com.apostle.dtos.requests.SendMoneyRequest;
import com.apostle.dtos.responses.TransactionResponse;
import com.apostle.services.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
@Validated
public class TransactionController {

    private final TransactionService transactionService;
    private final TransactionRepo transactionRepo;


    @PostMapping("/deposit")
    public ResponseEntity<?> deposit(@Valid @RequestBody DepositRequest request) {
        TransactionResponse response = transactionService.deposit(request);
        return ResponseEntity.ok(response);
    }


    @PostMapping("/transfer")
    public ResponseEntity<?> transfer(@Valid @RequestBody SendMoneyRequest request) {
        TransactionResponse response = transactionService.transfer(request);
        return ResponseEntity.ok(response);
    }


    @GetMapping("/{transactionId}")
    public ResponseEntity<?> getTransactionById(@PathVariable Long transactionId) {
        TransactionResponse response = transactionService.getTransactionById(transactionId);
        return ResponseEntity.ok(response);
    }


    @GetMapping("/account/{accountId}")
    public ResponseEntity<?> getTransactionsForAccount(@PathVariable Long accountId) {
        List<TransactionResponse> responses = transactionService.getTransactionsForAccount(accountId);
        return ResponseEntity.ok(responses);
    }



}
