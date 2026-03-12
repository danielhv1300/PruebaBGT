package com.test.btg.controller;

import com.test.btg.dto.SubscriptionRequestDTO;
import com.test.btg.dto.TransactionResponseDTO;
import com.test.btg.dto.UserResponseDTO;
import com.test.btg.security.AuthenticatedUser;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import com.test.btg.service.FundService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/funds")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class FundController {
    
    private final FundService IFundService;

    @PostMapping("/subscribe")
    public ResponseEntity<UserResponseDTO> subscribeFund(
            @RequestBody SubscriptionRequestDTO request,
            @AuthenticationPrincipal AuthenticatedUser principal) {

        log.info("POST /api/funds/subscribe - Usuario: {}, Fondo: {}",
                 request.getUserId(), request.getFundId());

        if (principal == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "No autenticado");
        }

        if (!principal.getId().equals(request.getUserId())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "No autorizado para suscribir en nombre de otro usuario");
        }

        UserResponseDTO response = IFundService.subscribeFund(request);
        
        log.info("Respuesta enviada - Usuario: {}", response.getId());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/cancel/{userId}/{transactionId}")
    public ResponseEntity<UserResponseDTO> cancelSubscription(
            @PathVariable String userId,
            @PathVariable String transactionId,
            @AuthenticationPrincipal AuthenticatedUser principal) {

        log.info("POST /api/funds/cancel/{}/{} - Cancelando transacción",
                 userId, transactionId);

        if (principal == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "No autenticado");
        }

        if (!principal.getId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "No autorizado para cancelar suscripción de otro usuario");
        }

        UserResponseDTO response = IFundService.cancelSubscription(userId, transactionId);
        
        log.info("Respuesta enviada - Usuario: {}", response.getId());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/history/{userId}")
    public ResponseEntity<List<TransactionResponseDTO>> getTransactionHistory(
            @PathVariable String userId,
            @AuthenticationPrincipal AuthenticatedUser principal) {

        if (principal == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Debes iniciar sesión");
        }

        if (!principal.getId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No tienes permiso para ver este historial");
        }

        return ResponseEntity.ok(IFundService.getTransactionHistory(userId));
    }
}

