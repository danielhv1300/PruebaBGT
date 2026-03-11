package com.test.btg.model;

import com.test.btg.enums.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "transactions")
public class Transaction {
    @Id
    private String id;
    private String userId;
    private String fundId;
    private String fundName;
    private Double amount;
    private TransactionType transactionType;

    @CreatedDate
    private LocalDateTime createdDate;
}
