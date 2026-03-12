package com.test.btg.builder;

import com.test.btg.model.Background;
import com.test.btg.enums.BackgroundCategory;
import net.datafaker.Faker;

public class BackgroundTestDataBuilder {
    private static final Faker faker = new Faker();

    private String id = faker.random().hex(24).toLowerCase();
    private String name = "Fondo " + faker.commerce().productName();
    private Double minimumAmount = 75000.0;
    private BackgroundCategory category = BackgroundCategory.FIC;

    public static BackgroundTestDataBuilder aBackground() {
        return new BackgroundTestDataBuilder();
    }

    public BackgroundTestDataBuilder withMinimumAmount(Double amount) {
        this.minimumAmount = amount;
        return this;
    }

    public Background build() {
        Background background = new Background();
        background.setId(this.id);
        background.setName(this.name);
        background.setMinimumAmount(this.minimumAmount);
        background.setCategory(this.category);
        return background;
    }
}