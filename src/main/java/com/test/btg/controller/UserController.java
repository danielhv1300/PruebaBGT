package com.test.btg.controller;

import com.test.btg.dto.UserRegistrationDTO;
import com.test.btg.dto.UserResponseDTO;
import com.test.btg.service.IUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class UserController {

    private final IUserService userService;

    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> registerUser(@RequestBody UserRegistrationDTO request) {
        log.info("POST /api/users/register - Registrando usuario: {}", request.getEmail());
        UserResponseDTO response = userService.registerUser(request);
        log.info("Usuario registrado - id: {}", response.getId());
        return ResponseEntity.ok(response);
    }
}

