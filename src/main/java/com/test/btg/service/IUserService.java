package com.test.btg.service;

import com.test.btg.dto.UserRegistrationDTO;
import com.test.btg.dto.UserResponseDTO;

public interface IUserService {
    UserResponseDTO registerUser(UserRegistrationDTO request);
}

