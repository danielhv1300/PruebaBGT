package com.test.btg.services;

import com.test.btg.builder.BackgroundTestDataBuilder;
import com.test.btg.builder.UserTestDataBuilder;
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
import com.test.btg.service.FundServiceImpl;
import com.test.btg.service.notification.EmailNotificationService;
import com.test.btg.service.notification.SmsNotificationService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FundServiceImplTest {

    @Mock private UserRepository userRepository;
    @Mock private BackgroundRepository backgroundRepository;
    @Mock private TransactionRepository transactionRepository;
    @Mock private SmsNotificationService smsNotificationService;
    @Mock private EmailNotificationService emailNotificationService;

    @InjectMocks
    private FundServiceImpl fundService;

    @Test
    @DisplayName("Debería suscribir al fondo y descontar saldo exitosamente")
    void subscribeFundSuccess() {
        // GIVEN
        User user = UserTestDataBuilder.aUser().buildEntity();
        Background background = BackgroundTestDataBuilder.aBackground()
                .withMinimumAmount(100000.0)
                .build();

        SubscriptionRequestDTO request = new SubscriptionRequestDTO(user.getId(), background.getId(), "EMAIL");

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(backgroundRepository.findById(background.getId())).thenReturn(Optional.of(background));
        when(transactionRepository.findByUserIdAndFundIdAndTransactionType(any(), any(), any()))
                .thenReturn(Optional.empty());

        Transaction mockTransaction = Transaction.builder().amount(100000.0).transactionType(TransactionType.OPENING).build();
        when(transactionRepository.save(any(Transaction.class))).thenReturn(mockTransaction);

        // WHEN
        UserResponseDTO response = fundService.subscribeFund(request);

        // THEN
        assertThat(response.getBalance()).isEqualTo(400000.0);
        verify(userRepository).save(argThat(u -> u.getBalance() == 400000.0));
        verify(emailNotificationService).sendNotification(any(), any(), any(), any(), any(), any());
        verify(transactionRepository).save(any(Transaction.class));
    }

    @Test
    @DisplayName("Debería lanzar BalanceInsufficientException cuando no hay dinero suficiente")
    void subscribeFundInsufficientBalance() {
        // GIVEN
        User user = UserTestDataBuilder.aUser().buildEntity();
        user.setBalance(5000.0);

        Background background = BackgroundTestDataBuilder.aBackground()
                .withMinimumAmount(75000.0)
                .build();

        SubscriptionRequestDTO request = new SubscriptionRequestDTO(user.getId(), background.getId(), "SMS");

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(backgroundRepository.findById(background.getId())).thenReturn(Optional.of(background));

        // WHEN & THEN
        assertThrows(BalanceInsufficientException.class, () -> fundService.subscribeFund(request));

        verify(userRepository, never()).save(any(User.class));
        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    @DisplayName("Debería cancelar suscripción y devolver el dinero al usuario")
    void cancelSubscriptionSuccess() {
        // GIVEN
        User user = UserTestDataBuilder.aUser().buildEntity();
        user.setBalance(100000.0);

        String transactionId = "trans123";
        Transaction openingTransaction = Transaction.builder()
                .id(transactionId)
                .userId(user.getId())
                .amount(50000.0)
                .fundId("fund1")
                .fundName("Fondo Test")
                .build();

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(transactionRepository.findById(transactionId)).thenReturn(Optional.of(openingTransaction));

        // WHEN
        UserResponseDTO response = fundService.cancelSubscription(user.getId(), transactionId);

        // THEN
        assertThat(response.getBalance()).isEqualTo(150000.0);
        verify(userRepository).save(argThat(u -> u.getBalance() == 150000.0));
        verify(transactionRepository).save(argThat(t -> t.getTransactionType() == TransactionType.CANCELLATION));
    }

    @Test
    @DisplayName("Debería suscribir al fondo usando notificación por SMS")
    void subscribeFundWithSmsNotification() {
        // GIVEN
        User user = UserTestDataBuilder.aUser().buildEntity(); // Tiene un teléfono aleatorio
        Background background = BackgroundTestDataBuilder.aBackground()
                .withMinimumAmount(50000.0)
                .build();

        SubscriptionRequestDTO request = new SubscriptionRequestDTO(
                user.getId(),
                background.getId(),
                "SMS"
        );

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(backgroundRepository.findById(background.getId())).thenReturn(Optional.of(background));

        Transaction mockTransaction = Transaction.builder()
                .amount(50000.0)
                .transactionType(TransactionType.OPENING)
                .build();
        when(transactionRepository.save(any(Transaction.class))).thenReturn(mockTransaction);

        // WHEN
        fundService.subscribeFund(request);

        // THEN
        verify(smsNotificationService, times(1)).sendNotification(
                eq(user.getId()),
                eq(user.getName()),
                eq(user.getPhoneNumber()),
                eq(background.getName()),
                anyDouble(),
                anyString()
        );
        verify(emailNotificationService, never()).sendNotification(any(), any(), any(), any(), any(), any());
    }

    @Test
    @DisplayName("Debería fallar si el usuario ya tiene una suscripción activa al mismo fondo")
    void shouldFailIfAlreadySubscribed() {
        // GIVEN
        User user = UserTestDataBuilder.aUser().buildEntity();
        Background background = BackgroundTestDataBuilder.aBackground().build();
        SubscriptionRequestDTO request = new SubscriptionRequestDTO(user.getId(), background.getId(), "EMAIL");

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(backgroundRepository.findById(background.getId())).thenReturn(Optional.of(background));

        Transaction existingTransaction = Transaction.builder().id("existing_id").build();
        when(transactionRepository.findByUserIdAndFundIdAndTransactionType(
                eq(user.getId()), eq(background.getId()), eq(TransactionType.OPENING)))
                .thenReturn(Optional.of(existingTransaction));

        // WHEN & THEN
        assertThrows(FundAlreadySubscribedException.class, () -> fundService.subscribeFund(request));

        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debería retornar el historial de transacciones ordenado")
    void getTransactionHistorySuccess() {
        // GIVEN
        String userId = "user123";

        Transaction t1 = Transaction.builder()
                .id("t1").userId(userId).fundName("Fondo A")
                .amount(50000.0).transactionType(TransactionType.OPENING)
                .createdDate(LocalDateTime.now())
                .build();

        Transaction t2 = Transaction.builder()
                .id("t2").userId(userId).fundName("Fondo B")
                .amount(100000.0).transactionType(TransactionType.OPENING)
                .createdDate(LocalDateTime.now().minusDays(1))
                .build();

        when(userRepository.existsById(userId)).thenReturn(true);
        when(transactionRepository.findByUserIdOrderByCreatedDateDesc(userId))
                .thenReturn(List.of(t1, t2));

        // WHEN
        List<TransactionResponseDTO> result = fundService.getTransactionHistory(userId);

        // THEN
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getFundName()).isEqualTo("Fondo A");
        assertThat(result.get(1).getFundName()).isEqualTo("Fondo B");

        verify(userRepository, times(1)).existsById(userId);
        verify(transactionRepository, times(1)).findByUserIdOrderByCreatedDateDesc(userId);
    }

    @Test
    @DisplayName("Debería lanzar IllegalArgumentException si el usuario no existe al pedir historial")
    void getTransactionHistoryUserNotFound() {
        // GIVEN
        String userId = "no_existe";
        when(userRepository.existsById(userId)).thenReturn(false);

        // WHEN & THEN
        assertThrows(IllegalArgumentException.class, () -> fundService.getTransactionHistory(userId));

        verify(transactionRepository, never()).findByUserIdOrderByCreatedDateDesc(anyString());
    }
}