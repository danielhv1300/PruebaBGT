package com.test.btg.services;

import com.test.btg.builder.UserTestDataBuilder;
import com.test.btg.dto.UserRegistrationDTO;
import com.test.btg.dto.UserResponseDTO;
import com.test.btg.exception.DuplicateUserException;
import com.test.btg.model.User;
import com.test.btg.repository.UserRepository;
import com.test.btg.service.UserServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    @DisplayName("Debería registrar un usuario exitosamente con saldo inicial")
    void shouldRegisterUserSuccessfully() {
        // GIVEN
        UserRegistrationDTO request = UserTestDataBuilder.aUser().buildRegistrationDTO();
        User savedUser = UserTestDataBuilder.aUser()
                .withEmail(request.getEmail())
                .buildEntity();

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("hashedPassword");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        // WHEN
        UserResponseDTO response = userService.registerUser(request);

        // THEN
        assertThat(response).isNotNull();
        assertThat(response.getEmail()).isEqualTo(request.getEmail());
        assertThat(response.getBalance()).isEqualTo(500000.0);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Debería lanzar excepción si el email ya existe")
    void shouldThrowExceptionWhenEmailAlreadyExists() {
        // GIVEN
        UserRegistrationDTO request = UserTestDataBuilder.aUser().withEmail("existente@test.com").buildRegistrationDTO();
        User existingUser = UserTestDataBuilder.aUser().withEmail("existente@test.com").buildEntity();

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(existingUser));

        // WHEN & THEN
        assertThrows(DuplicateUserException.class, () -> userService.registerUser(request));
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("loadUserByUsername debería retornar AuthenticatedUser cuando el email existe")
    void loadUserByUsernameSuccess() {
        // GIVEN
        User user = UserTestDataBuilder.aUser().buildEntity();
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        // WHEN
        UserDetails userDetails = userService.loadUserByUsername(user.getEmail());

        // THEN
        assertThat(userDetails).isNotNull();
        assertThat(userDetails.getUsername()).isEqualTo(user.getEmail());
        assertThat(userDetails.getPassword()).isEqualTo(user.getPassword());

        assertThat(userDetails).isInstanceOf(com.test.btg.security.AuthenticatedUser.class);

        com.test.btg.security.AuthenticatedUser authUser = (com.test.btg.security.AuthenticatedUser) userDetails;
        assertThat(authUser.getId()).isEqualTo(user.getId());
    }

    @Test
    @DisplayName("loadUserByUsername debería lanzar UsernameNotFoundException cuando el email no existe")
    void loadUserByUsernameNotFound() {
        // GIVEN
        String email = "noexiste@test.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // WHEN & THEN
        assertThrows(UsernameNotFoundException.class, () -> userService.loadUserByUsername(email));
    }
}