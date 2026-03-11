package com.test.btg.service;

import com.test.btg.dto.SubscriptionRequestDTO;
import com.test.btg.dto.TransactionResponseDTO;
import com.test.btg.dto.UserResponseDTO;

import java.util.List;

public interface IFundService {
    UserResponseDTO subscribeFund(SubscriptionRequestDTO request);
    UserResponseDTO cancelSubscription(String userId, String transactionId);
    List<TransactionResponseDTO> getTransactionHistory(String userId);
}

