package com.apostle.data.repositories;

import com.apostle.data.model.Transaction;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface TransactionRepo extends MongoRepository<Transaction, String> {


//    BigDecimal computeBalance(@Param("accountId") String  accountId);

    List<Transaction> findAllBySenderIdOrReceiverId(String senderId, String receiverId);
}
