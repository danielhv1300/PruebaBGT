package com.test.btg.config;

import com.test.btg.enums.BackgroundCategory;
import com.test.btg.model.Background;
import com.test.btg.model.User;
import com.test.btg.repository.BackgroundRepository;
import com.test.btg.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import java.util.List;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final BackgroundRepository backgroundRepository;
    private final UserRepository userRepository;

    @Override
    public void run(String... args) {
        try {
            log.info("--- INICIANDO CARGA DE DATOS ---");
            initializeBackgrounds();
            initializeDefaultUser();
            log.info("--- CARGA DE DATOS EXITOSA ---");
        } catch (Exception e) {
            log.error("⚠Error inicializando datos: {}", e.getMessage());
        }
    }

    private void initializeBackgrounds() {
        if (backgroundRepository.count() == 0) {
            List<Background> backgrounds = List.of(
                    createBackground("1", "FPV_BTG_PACTUAL_RECAUDADORA", 75000.0, BackgroundCategory.FPV),
                    createBackground("2", "FPV_BTG_PACTUAL_ECOPETROL", 125000.0, BackgroundCategory.FPV),
                    createBackground("3", "DEUDAPRIVADA", 50000.0, BackgroundCategory.FIC),
                    createBackground("4", "FDO-ACCIONES", 250000.0, BackgroundCategory.FIC),
                    createBackground("5", "FPV_BTG_PACTUAL_DINAMICA", 100000.0, BackgroundCategory.FPV)
            );
            backgroundRepository.saveAll(backgrounds);
            log.info(">> 5 Fondos insertados.");
        }
    }

    private void initializeDefaultUser() {
        if (userRepository.count() == 0) {
            User user = User.builder()
                    .id("6761ab2f21")
                    .name("Cliente Prueba BTG")
                    .email("test@btg.com")
                    .phoneNumber("31232132312")
                    .password("$2a$10$7Qy8n1s5X9Z6e5u1v8jO3u5X9Z6e5u1v8jO3u5X9Z6e5u1v8jO3") // "password" encriptado
                    .balance(500000.0) // Saldo inicial requerido
                    .build();
            userRepository.save(user);
            log.info(">> Usuario con $500.000 creado.");
        }
    }

    private Background createBackground(String id, String name, Double min, BackgroundCategory cat) {
        Background b = new Background();
        b.setId(id); b.setName(name); b.setMinimumAmount(min); b.setCategory(cat);
        return b;
    }
}