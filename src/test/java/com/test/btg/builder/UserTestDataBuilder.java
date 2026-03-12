package com.test.btg.builder;

import com.test.btg.dto.UserRegistrationDTO;
import com.test.btg.model.User;
import net.datafaker.Faker;

public class UserTestDataBuilder {
    private static final Faker faker = new Faker();

    private String id = faker.random().hex(24).toLowerCase();
    private String name = faker.name().fullName();
    private String email = faker.internet().emailAddress();
    private String password = "Password123!";
    private String phoneNumber = faker.phoneNumber().cellPhone();

    public static UserTestDataBuilder aUser() {
        return new UserTestDataBuilder();
    }

    public UserTestDataBuilder withEmail(String email) {
        this.email = email;
        return this;
    }

    public UserRegistrationDTO buildRegistrationDTO() {
        return new UserRegistrationDTO(name, email, password, phoneNumber);
    }

    public User buildEntity() {
        return User.builder()
                .id(id)
                .name(name)
                .email(email)
                .password("$2a$10$encryptedPassword")
                .phoneNumber(phoneNumber)
                .balance(500000.0)
                .build();
    }
}