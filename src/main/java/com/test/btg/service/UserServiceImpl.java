package com.test.btg.service;

import com.test.btg.dto.UserRegistrationDTO;
import com.test.btg.dto.UserResponseDTO;
import com.test.btg.exception.DuplicateUserException;
import com.test.btg.model.User;
import com.test.btg.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService, UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

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
                .password(passwordEncoder.encode(request.getPassword()))
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

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));

        List<GrantedAuthority> authorities = List.of();

        return new com.test.btg.security.AuthenticatedUser(
                user.getId(), user.getEmail(), user.getPassword(), authorities
        );
    }
}


