package com.test.btg.service.notification;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class SmsNotificationService implements NotificationService {

    @Override
    public void sendNotification(String userId, String userName, String userContact,
                                 String fundName, Double amount, String transactionType) {

        String formattedAmount = String.format("%,.2f", amount);

        log.info("***************************************************************");
        log.info("NOTIFICACIÓN SMS ENVIADA");
        log.info("***************************************************************");
        log.info("Usuario: {} (ID: {})", userName, userId);
        log.info("Numero: {}", userContact);
        log.info("Fondo: {}", fundName);
        log.info("Monto: ${}", formattedAmount);
        log.info("Tipo de Operacion: {}", transactionType);
        log.info("****************************************************************");
        log.info("SMS entregado exitosamente");
        log.info("****************************************************************\n");
    }
}

