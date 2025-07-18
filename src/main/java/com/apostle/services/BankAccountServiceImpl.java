package com.apostle.services;

import com.apostle.data.model.AccountType;
import com.apostle.data.model.BankAccount;
import com.apostle.data.model.User;
import com.apostle.data.repositories.BankAccountRepository;
import com.apostle.data.repositories.UserRepository;
import com.apostle.dtos.requests.AddAccountRequest;
import com.apostle.dtos.responses.AddAccountResponse;
import com.apostle.dtos.responses.BalanceResponse;
import com.apostle.exceptions.InsufficientBalanceException;
import com.apostle.exceptions.UserNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Random;

@Service
public class BankAccountServiceImpl implements BankAccountService {

    private final BankAccountRepository bankAccountRepository;
    private final UserRepository userRepository;
    public static final Long SYSTEM_ACCOUNT_ID = 0L;

    public BankAccountServiceImpl(BankAccountRepository bankAccountRepository, UserRepository userRepository) {
        this.bankAccountRepository = bankAccountRepository;
        this.userRepository = userRepository;
    }

    @Override
    public BankAccount getSystemAccount() {
        return bankAccountRepository.findByAccountNumber("SYSTEM")
                .orElseThrow(() -> new RuntimeException("System account not found"));    }

    @Override
    public BankAccount createAccountForUser(User user, AccountType accountType) {
        String accountNumber = generateUniqueAccountNumber();

        BankAccount account = BankAccount.builder()
                .accountNumber(accountNumber)
                .balance(BigDecimal.ZERO)
                .accountType(accountType)
                .user(user)
                .build();

        return bankAccountRepository.save(account);
    }

    @Override
    public AddAccountResponse createAccount(AddAccountRequest addAccountRequest) {
        String currentUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findUserByEmail(currentUserEmail).orElseThrow(() -> new UserNotFoundException("User not found"));
        String accountNumber = generateUniqueAccountNumber();

        BankAccount account = new BankAccount();
        account.setName(addAccountRequest.getName());
        account.setUser(user);
        account.setBalance(BigDecimal.ZERO);
        account.setAccountNumber(accountNumber);

        bankAccountRepository.save(account);

        return new AddAccountResponse(accountNumber, addAccountRequest.getName(), BigDecimal.ZERO);
    }

    @Override
    public BalanceResponse getBalance(Long accountId) {
        BankAccount account = getAccountById(accountId);
        return new BalanceResponse(account.getAccountNumber(), account.getBalance());    }

    @Override
    @Transactional
    public void credit(Long accountId, BigDecimal amount) {
        BankAccount account = getAccountById(accountId);
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InsufficientBalanceException("amount must be greater than 0");
        }
        account.setBalance(account.getBalance().add(amount));
        bankAccountRepository.save(account);
    }

    @Override
    @Transactional
    public void debit(Long accountId, BigDecimal amount) {
        BankAccount account = getAccountById(accountId);
        if (account.getBalance().compareTo(amount) < 0) {
            throw new InsufficientBalanceException("Insufficient Balance");
        }
        account.setBalance(account.getBalance().subtract(amount));
        bankAccountRepository.save(account);
    }

    @Override
    public BankAccount getAccountById(Long accountId) {
        return bankAccountRepository.findById(accountId)
                .orElseThrow(() -> new UserNotFoundException("Account not found"));
    }

    private String generateUniqueAccountNumber() {
        String accountNumber;
        do {
            accountNumber = String.valueOf(1000000000L + new Random().nextLong(9000000000L));
        } while (bankAccountRepository.existsByAccountNumber(accountNumber));
        return accountNumber;
    }
}
