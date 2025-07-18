package com.apostle.services;

import com.apostle.data.model.BankAccount;
import com.apostle.data.model.Transaction;
import com.apostle.data.model.TransactionStatus;
import com.apostle.data.model.TransactionType;
import com.apostle.data.repositories.TransactionRepo;
import com.apostle.dtos.requests.DepositRequest;
import com.apostle.dtos.requests.SendMoneyRequest;
import com.apostle.dtos.responses.TransactionResponse;
import com.apostle.exceptions.TransactionNotFoundException;
import com.apostle.utils.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {
    private final TransactionRepo transactionRepo;
    private final BankAccountService bankService;
    private static final Long SYSTEM_ACCOUNT_ID = 0L;


    @Override
    public TransactionResponse deposit(DepositRequest  request) {
        bankService.credit(request.receiverId(),request.amount());
        BankAccount system = bankService.getSystemAccount();
        BankAccount receiverAccount = bankService.getAccountById(request.receiverId());

        Transaction transaction = new Transaction();
        transaction.setSender(system);
        transaction.setReceiver(receiverAccount);
        transaction.setAmount(request.amount());
        transaction.setType(TransactionType.CREDIT);
        transaction.setStatus(TransactionStatus.SUCCESS);
        transaction.setNote(request.note());
        transaction.setTimestamp(LocalDateTime.now());
        transactionRepo.save(transaction);

        return new TransactionResponse(
            transaction.getTransactionId(),
            transaction.getSender() != null ? transaction.getSender().getId() : null,
            transaction.getReceiver() != null ? transaction.getReceiver().getId() : null,
            transaction.getAmount(),
            transaction.getType(),
            transaction.getStatus(),
            transaction.getNote(),
            transaction.getTimestamp()
        );
    }

    @Override
    @Transactional
    public TransactionResponse transfer(SendMoneyRequest request) {
        if(request.senderId().equals(request.receiverId())) throw new IllegalArgumentException("Cannot transfer to self");
        if(request.amount() == null || request.amount().compareTo(BigDecimal.ZERO) <= 0) throw new IllegalArgumentException("Amount must be greater than zero");

        bankService.credit(request.receiverId(), request.amount());
        bankService.debit(request.receiverId(), request.amount());

        LocalDateTime now = LocalDateTime.now();


        BankAccount senderAccount = bankService.getAccountById(request.senderId());
        BankAccount receiverAccount = bankService.getAccountById(request.receiverId());

        Transaction senderTransaction = new Transaction();
        senderTransaction.setSender(senderAccount);
        senderTransaction.setReceiver(receiverAccount);
        senderTransaction.setAmount(request.amount());
        senderTransaction.setType(TransactionType.DEBIT);
        senderTransaction.setStatus(TransactionStatus.SUCCESS);
        senderTransaction.setNote("Transfer to user " + request.receiverId() + ": " + request.note());
        senderTransaction.setTimestamp(now);
        transactionRepo.save(senderTransaction);

        Transaction receiverTransaction = new Transaction();
        receiverTransaction.setSender(senderAccount);
        receiverTransaction.setReceiver(receiverAccount);
        receiverTransaction.setAmount(request.amount());
        receiverTransaction.setType(TransactionType.CREDIT);
        receiverTransaction.setStatus(TransactionStatus.SUCCESS);
        receiverTransaction.setNote("Received from user " + request.senderId() + ": " + request.note());
        receiverTransaction.setTimestamp(now);
        transactionRepo.save(receiverTransaction);

        return mapToResponse(senderTransaction);
    }


    @Override
    public List<TransactionResponse> getTransactionsForAccount(Long accountId) {
       List<Transaction> transactions = transactionRepo.findAllBySenderIdOrReceiverId(accountId,accountId);

        return transactions.stream()
                .map(this::mapToResponse)
                .sorted(Comparator.comparing(TransactionResponse::timeStamp).reversed())
                .toList();
    }


    @Override
    public TransactionResponse getTransactionById(Long transactionId) {
        Transaction transaction = transactionRepo.findById(transactionId).orElseThrow(() -> new TransactionNotFoundException("Transaction not found"));
        return mapToResponse(transaction);
    }


    private  TransactionResponse mapToResponse(Transaction transaction) {
        return new TransactionResponse(
                transaction.getTransactionId(),
                transaction.getSender() != null ? transaction.getSender().getId() : null,
                transaction.getReceiver() != null ? transaction.getReceiver().getId() : null,
                transaction.getAmount(),
                transaction.getType(),
                transaction.getStatus(),
                transaction.getNote(),
                transaction.getTimestamp()
        );
    }
}


