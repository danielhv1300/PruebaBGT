package com.test.btg.service.notification;

public interface NotificationService {

    void sendNotification(String userId, String userName, String userContact, 
                         String fundName, Double amount, String transactionType);
}

