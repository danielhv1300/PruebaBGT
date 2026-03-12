package com.test.btg.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionRequestDTO {

    @NotBlank(message = "El ID de usuario (userId) no puede estar vacío")
    @JsonProperty("userId")
    private String userId;

    @NotBlank(message = "El ID del fondo (fundId) no puede estar vacío")
    @JsonProperty("fundId")
    private String fundId;

    @NotBlank(message = "El tipo de notificación es obligatorio")
    @Pattern(regexp = "^(SMS|EMAIL)$", message = "El tipo de notificación debe ser 'SMS' o 'EMAIL'")
    @JsonProperty("notificationType")
    private String notificationType;
}