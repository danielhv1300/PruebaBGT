package com.test.btg.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionRequestDTO {
    
    @JsonProperty("userId")
    private String userId;
    
    @JsonProperty("fundId")
    private String fundId;
    
    @JsonProperty("notificationType")
    private String notificationType; // "SMS" o "EMAIL"
}

