package com.test.btg.service;

import com.test.btg.dto.SubscriptionRequestDTO;
import com.test.btg.dto.TransactionResponseDTO;
import com.test.btg.dto.UserResponseDTO;
import com.test.btg.enums.TransactionType;
import com.test.btg.exception.BalanceInsufficientException;
import com.test.btg.exception.FundAlreadySubscribedException;
import com.test.btg.model.Background;
import com.test.btg.model.Transaction;
import com.test.btg.model.User;
import com.test.btg.repository.BackgroundRepository;
import com.test.btg.repository.TransactionRepository;
import com.test.btg.repository.UserRepository;
import com.test.btg.service.notification.EmailNotificationService;
import com.test.btg.service.notification.NotificationService;
import com.test.btg.service.notification.SmsNotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FundServiceImpl implements FundService {
    
    private final UserRepository userRepository;
    private final BackgroundRepository backgroundRepository;
    private final TransactionRepository transactionRepository;
    private final SmsNotificationService smsNotificationService;
    private final EmailNotificationService emailNotificationService;

    @Override
    public UserResponseDTO subscribeFund(SubscriptionRequestDTO request) {
        log.info("Procesando suscripción - Usuario: {}, Fondo: {}",
                 request.getUserId(), request.getFundId());
        
        User user = getUserOrThrow(request.getUserId());
        Background fund = getFundOrThrow(request.getFundId());
        
        validateSufficientBalance(user, fund);
        validateNoActiveSubscription(user.getId(), fund.getId());
        
        user.setBalance(user.getBalance() - fund.getMinimumAmount());
        userRepository.save(user);
        
        Transaction transaction = createAndSaveTransaction(
            user.getId(), 
            fund.getId(), 
            fund.getName(), 
            fund.getMinimumAmount(), 
            TransactionType.OPENING
        );
        
        sendNotification(request.getNotificationType(), user, fund, transaction);
        
        log.info("Suscripción completada - Usuario: {}, Fondo: {}",
                 user.getId(), fund.getName());
        
        return mapToUserResponseDTO(user);
    }

    @Override
    public UserResponseDTO cancelSubscription(String userId, String transactionId) {
        log.info("Procesando cancelación - Usuario: {}, Transacción: {}",
                 userId, transactionId);
        
        User user = getUserOrThrow(userId);
        Transaction openingTransaction = getTransactionOrThrow(transactionId);
        
        user.setBalance(user.getBalance() + openingTransaction.getAmount());
        userRepository.save(user);
        
        createAndSaveTransaction(
            userId,
            openingTransaction.getFundId(),
            openingTransaction.getFundName(),
            openingTransaction.getAmount(),
            TransactionType.CANCELLATION
        );
        
        log.info("Cancelación completada - Usuario: {}, Fondo: {}",
                 userId, openingTransaction.getFundName());
        
        return mapToUserResponseDTO(user);
    }

    @Override
    public List<TransactionResponseDTO> getTransactionHistory(String userId) {
        log.info("Obteniendo historial - Usuario: {}", userId);
        
        validateUserExists(userId);
        
        List<Transaction> transactions = transactionRepository
            .findByUserIdOrderByCreatedDateDesc(userId);
        
        log.info("Historial obtenido - {} transacciones encontradas",
                 transactions.size());
        
        return transactions.stream()
            .map(this::mapToTransactionResponseDTO)
            .collect(Collectors.toList());
    }

    private void validateSufficientBalance(User user, Background fund) {
        if (user.getBalance() < fund.getMinimumAmount()) {
            String errorMessage = String.format(
                "No tiene saldo disponible para vincularse al fondo %s",
                fund.getName()
            );
            log.warn("{}", errorMessage);
            throw new BalanceInsufficientException(errorMessage);
        }
    }

    private void validateNoActiveSubscription(String userId, String fundId) {
        transactionRepository
            .findByUserIdAndFundIdAndTransactionType(userId, fundId, TransactionType.OPENING)
            .ifPresent(transaction -> {
                String errorMessage = String.format(
                    "Ya tiene una suscripción activa al fondo con ID: %s",
                    fundId
                );
                log.warn("{}", errorMessage);
                throw new FundAlreadySubscribedException(errorMessage);
            });
    }

    private void validateUserExists(String userId) {
        if (!userRepository.existsById(userId)) {
            String errorMessage = String.format("Usuario con ID %s no encontrado", userId);
            log.warn("{}", errorMessage);
            throw new IllegalArgumentException(errorMessage);
        }
    }

    private User getUserOrThrow(String userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> {
                String errorMessage = String.format(
                    "Usuario con ID %s no encontrado",
                    userId
                );
                log.error("{}", errorMessage);
                return new IllegalArgumentException(errorMessage);
            });
    }

    private Background getFundOrThrow(String fundId) {
        return backgroundRepository.findById(fundId)
            .orElseThrow(() -> {
                String errorMessage = String.format(
                    "Fondo con ID %s no encontrado",
                    fundId
                );
                log.error("{}", errorMessage);
                return new IllegalArgumentException(errorMessage);
            });
    }

    private Transaction getTransactionOrThrow(String transactionId) {
        return transactionRepository.findById(transactionId)
            .orElseThrow(() -> {
                String errorMessage = String.format(
                    "Transacción con ID %s no encontrada",
                    transactionId
                );
                log.error("{}", errorMessage);
                return new IllegalArgumentException(errorMessage);
            });
    }

    private Transaction createAndSaveTransaction(String userId, String fundId, 
                                                  String fundName, Double amount,
                                                  TransactionType type) {
        Transaction transaction = Transaction.builder()
            .userId(userId)
            .fundId(fundId)
            .fundName(fundName)
            .amount(amount)
            .transactionType(type)
            .createdDate(LocalDateTime.now())
            .build();
        
        return transactionRepository.save(transaction);
    }

    private void sendNotification(String notificationType, User user, 
                                  Background fund, Transaction transaction) {
        try {
            NotificationService notificationService = selectNotificationService(notificationType);
            String userContact = getContactForNotificationType(notificationType, user);
            
            notificationService.sendNotification(
                user.getId(),
                user.getName(),
                userContact,
                fund.getName(),
                transaction.getAmount(),
                transaction.getTransactionType().getDescription()
            );
            
            log.info("Notificación enviada - Tipo: {}", notificationType);
        } catch (Exception e) {
            log.error("Error al enviar notificación: {}", e.getMessage());
        }
    }

    private NotificationService selectNotificationService(String notificationType) {
        return "SMS".equalsIgnoreCase(notificationType) 
            ? smsNotificationService 
            : emailNotificationService;
    }

    private String getContactForNotificationType(String notificationType, User user) {
        return "SMS".equalsIgnoreCase(notificationType)
            ? user.getPhoneNumber()
            : user.getEmail();
    }

    private UserResponseDTO mapToUserResponseDTO(User user) {
        return UserResponseDTO.builder()
            .id(user.getId())
            .name(user.getName())
            .email(user.getEmail())
            .phoneNumber(user.getPhoneNumber())
            .balance(user.getBalance())
            .build();
    }

    private TransactionResponseDTO mapToTransactionResponseDTO(Transaction transaction) {
        return TransactionResponseDTO.builder()
            .id(transaction.getId())
            .userId(transaction.getUserId())
            .fundId(transaction.getFundId())
            .fundName(transaction.getFundName())
            .amount(transaction.getAmount())
            .transactionType(transaction.getTransactionType())
            .createdDate(transaction.getCreatedDate())
            .build();
    }
}

