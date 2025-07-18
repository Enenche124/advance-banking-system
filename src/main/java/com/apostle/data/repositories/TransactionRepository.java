package com.apostle.data.repositories;

import com.apostle.data.model.Transaction;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends MongoRepository<Transaction, String> {


//    BigDecimal computeBalance(@Param("accountId") String  accountId);

    List<Transaction> findAllBySenderAccountIdOrReceiverAccountId(String senderId, String receiverId);
}
