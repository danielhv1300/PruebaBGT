package com.test.btg.repository;

import com.test.btg.enums.TransactionType;
import com.test.btg.model.Transaction;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends MongoRepository<Transaction, String> {

    List<Transaction> findByUserIdOrderByCreatedDateDesc(String userId);

    Optional<Transaction> findByUserIdAndFundIdAndTransactionType(
            String userId, 
            String fundId, 
            TransactionType transactionType
    );
}
