package com.test.btg.enums;

import lombok.Getter;

@Getter
public enum BackgroundCategory {
    FPV("Fondo de Pensiones Voluntarias"),
    FIC("Fondo de Inversion Colectiva");

    private final String desciption;

    BackgroundCategory(String desciption) {
        this.desciption = desciption;
    }
}
