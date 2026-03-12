package com.test.btg.controller;

import com.test.btg.dto.JwtResponseDTO;
import com.test.btg.dto.LoginRequestDTO;
import com.test.btg.model.User;
import com.test.btg.repository.UserRepository;
import com.test.btg.security.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final UserRepository userRepository;

    @PostMapping("/login")
    public ResponseEntity<JwtResponseDTO> authenticateUser(@RequestBody LoginRequestDTO request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado después de autenticación"));

        String jwt = jwtUtils.generateTokenFromEmail(user.getEmail());

        return ResponseEntity.ok(JwtResponseDTO.builder()
                .token(jwt)
                .id(user.getId())
                .email(user.getEmail())
                .build());
    }
}


