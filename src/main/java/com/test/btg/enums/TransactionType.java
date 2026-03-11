package com.test.btg.enums;

import lombok.Getter;

@Getter
public enum TransactionType {
    OPENING("Suscripcion a fondo"),
    CANCELLATION("Retiro de fondo");

    private final String description;
    TransactionType(String description) {
        this.description = description;
    }
}
