package com.apostle.services;

import com.apostle.data.model.BankAccount;
import com.apostle.data.model.Transaction;
import com.apostle.data.model.TransactionStatus;
import com.apostle.data.model.TransactionType;
import com.apostle.data.repositories.TransactionRepository;
import com.apostle.dtos.requests.DepositRequest;
import com.apostle.dtos.requests.SendMoneyRequest;
import com.apostle.dtos.responses.TransactionResponse;
import com.apostle.exceptions.TransactionNotFoundException;
import  org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {
    private final TransactionRepository transactionRepo;
    private final BankAccountService bankService;
    private static final String SYSTEM_ACCOUNT_ID = "SYSTEM";


    @Override
    public TransactionResponse deposit(DepositRequest request) {
        bankService.credit(request.receiverAccountNumber(), request.amount());

        BankAccount systemAccount = bankService.getSystemAccount();
        BankAccount receiverAccount = bankService.getAccountByAccountNumber(request.receiverAccountNumber());

        Transaction transaction = new Transaction();
        transaction.setSenderAccountId(systemAccount.getId());
        transaction.setSenderAccountNumber(systemAccount.getAccountNumber());
        transaction.setReceiverAccountId(receiverAccount.getId());
        transaction.setReceiverAccountNumber(receiverAccount.getAccountNumber());
        transaction.setAmount(request.amount());
        transaction.setType(TransactionType.CREDIT);
        transaction.setStatus(TransactionStatus.SUCCESS);
        transaction.setNote(request.note());
        transaction.setTimestamp(LocalDateTime.now());

        transactionRepo.save(transaction);

        return mapToResponse(transaction);
    }


    @Override
    @Transactional
    public TransactionResponse transfer(SendMoneyRequest request) {
        validateTransferRequest(request);

        BankAccount senderAccount = bankService.getAccountByAccountNumber(request.senderAccountNumber());
        BankAccount receiverAccount = bankService.getAccountByAccountNumber(request.receiverAccountNumber());

        bankService.debit(senderAccount.getAccountNumber(), request.amount());
        bankService.credit(receiverAccount.getAccountNumber(), request.amount());

        LocalDateTime now = LocalDateTime.now();
        String note = Optional.ofNullable(request.note()).orElse("");

        Transaction senderTx = createTransaction(
                senderAccount,
                receiverAccount,
                request.amount(),
                TransactionType.DEBIT,
                "Transfer to " + receiverAccount.getAccountNumber() + ": " + note,
                now
        );

        Transaction receiverTx = createTransaction(
                senderAccount,
                receiverAccount,
                request.amount(),
                TransactionType.CREDIT,
                "Received from " + senderAccount.getAccountNumber() + ": " + note,
                now
        );

        transactionRepo.save(senderTx);
        transactionRepo.save(receiverTx);

        return mapToResponse(senderTx);
    }




    @Override
    public List<TransactionResponse> getTransactionsForAccount(String  accountId) {
       List<Transaction> transactions = transactionRepo.findAllBySenderAccountIdOrReceiverAccountId(accountId,accountId);

        return transactions.stream()
                .map(this::mapToResponse)
                .sorted(Comparator.comparing(TransactionResponse::timeStamp).reversed())
                .toList();
    }


    @Override
    public TransactionResponse getTransactionById(String  transactionId) {
        Transaction transaction = transactionRepo.findById(transactionId).orElseThrow(() -> new TransactionNotFoundException("Transaction not found"));
        return mapToResponse(transaction);
    }


    private TransactionResponse mapToResponse(Transaction transaction) {
        return new TransactionResponse(
                transaction.getTransactionId(),
                transaction.getSenderAccountId(),
                transaction.getReceiverAccountId(),
                transaction.getAmount(),
                transaction.getType(),
                transaction.getStatus(),
                transaction.getNote(),
                transaction.getTimestamp(),
                transaction.getReceiverAccountNumber()
        );
    }



    private void validateTransferRequest(SendMoneyRequest request) {
        if (request.senderAccountNumber().equals(request.receiverAccountNumber())) {
            throw new IllegalArgumentException("Cannot transfer to self");
        }
        if (request.amount() == null || request.amount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero");
        }
    }

    private Transaction createTransaction(
            BankAccount sender,
            BankAccount receiver,
            BigDecimal amount,
            TransactionType type,
            String note,
            LocalDateTime timestamp
    ) {
        Transaction transaction = new Transaction();
        transaction.setSenderAccountId(sender.getId());
        transaction.setSenderAccountNumber(sender.getAccountNumber());
        transaction.setReceiverAccountId(receiver.getId());
        transaction.setReceiverAccountNumber(receiver.getAccountNumber());
        transaction.setAmount(amount);
        transaction.setType(type);
        transaction.setStatus(TransactionStatus.SUCCESS);
        transaction.setNote(note);
        transaction.setTimestamp(timestamp);
        return transaction;
    }

}


