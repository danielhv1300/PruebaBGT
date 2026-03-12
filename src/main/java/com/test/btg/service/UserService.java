package com.test.btg.service;

import com.test.btg.dto.UserRegistrationDTO;
import com.test.btg.dto.UserResponseDTO;

public interface UserService {
    UserResponseDTO registerUser(UserRegistrationDTO request);
}

