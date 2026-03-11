package com.test.btg.service;

import com.test.btg.dto.UserRegistrationDTO;
import com.test.btg.dto.UserResponseDTO;
import com.test.btg.exception.DuplicateUserException;
import com.test.btg.model.User;
import com.test.btg.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService {

    private final UserRepository userRepository;

    private static final Double INITIAL_BALANCE = 500000.0;

    @Override
    public UserResponseDTO registerUser(UserRegistrationDTO request) {
        java.util.Optional<User> existing = userRepository.findByEmail(request.getEmail());
        if (existing.isPresent()) {
            throw new DuplicateUserException("Usuario con email ya registrado: " + request.getEmail());
        }

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .balance(INITIAL_BALANCE)
                .build();

        User saved = userRepository.save(user);

        return UserResponseDTO.builder()
                .id(saved.getId())
                .name(saved.getName())
                .email(saved.getEmail())
                .phoneNumber(saved.getPhoneNumber())
                .balance(saved.getBalance())
                .build();
    }
}


