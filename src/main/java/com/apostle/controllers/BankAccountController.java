package com.apostle.controllers;

import com.apostle.dtos.requests.AddAccountRequest;
import com.apostle.dtos.responses.BalanceResponse;
import com.apostle.services.BankAccountService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/user")
public class BankAccountController {

    private final BankAccountService bankAccountService;

    public BankAccountController(BankAccountService bankAccountService) {
        this.bankAccountService = bankAccountService;
    }

    @PostMapping("/bank-account")
    public ResponseEntity<?> createBankAccount(@RequestBody AddAccountRequest addAccountRequest){
        try {
            return ResponseEntity.ok(bankAccountService.addSubAccountForCurrentUser(addAccountRequest));
        }catch (Exception exception){
            return ResponseEntity.badRequest().body(exception.getMessage());
        }
    }

    @GetMapping("/balance/{accountId}")
    public ResponseEntity<?> getBalance(@PathVariable  String accountId) {
        BalanceResponse response = bankAccountService.getBalance(accountId);
        return ResponseEntity.ok(response);
    }
}
