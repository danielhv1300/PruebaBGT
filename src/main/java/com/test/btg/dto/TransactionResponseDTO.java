package com.test.btg.dto;

import com.test.btg.enums.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionResponseDTO {
    
    private String id;
    private String userId;
    private String fundId;
    private String fundName;
    private Double amount;
    private TransactionType transactionType;
    private LocalDateTime createdDate;
}

