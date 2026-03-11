package com.test.btg.controller;

import com.test.btg.dto.SubscriptionRequestDTO;
import com.test.btg.dto.TransactionResponseDTO;
import com.test.btg.dto.UserResponseDTO;
import com.test.btg.service.IFundService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/funds")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class FundController {
    
    private final IFundService IFundService;

    @PostMapping("/subscribe")
    public ResponseEntity<UserResponseDTO> subscribeFund(
            @RequestBody SubscriptionRequestDTO request) {
        
        log.info("POST /api/funds/subscribe - Usuario: {}, Fondo: {}",
                 request.getUserId(), request.getFundId());
        
        UserResponseDTO response = IFundService.subscribeFund(request);
        
        log.info("Respuesta enviada - Usuario: {}", response.getId());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/cancel/{userId}/{transactionId}")
    public ResponseEntity<UserResponseDTO> cancelSubscription(
            @PathVariable String userId,
            @PathVariable String transactionId) {
        
        log.info("POST /api/funds/cancel/{}/{} - Cancelando transacción",
                 userId, transactionId);
        
        UserResponseDTO response = IFundService.cancelSubscription(userId, transactionId);
        
        log.info("Respuesta enviada - Usuario: {}", response.getId());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/history/{userId}")
    public ResponseEntity<List<TransactionResponseDTO>> getTransactionHistory(
            @PathVariable String userId) {
        
        log.info("GET /api/funds/history/{} - Obteniendo historial", userId);
        
        List<TransactionResponseDTO> response = IFundService.getTransactionHistory(userId);
        
        log.info("Respuesta enviada - {} transacciones", response.size());
        return ResponseEntity.ok(response);
    }
}

