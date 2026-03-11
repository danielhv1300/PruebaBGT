package com.test.btg.model;

import com.test.btg.enums.BackgroundCategory;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "backgrounds")
public class Background {
    @Id
    private String id;
    private String name;
    private Double minimumAmount;
    private BackgroundCategory category;
}
