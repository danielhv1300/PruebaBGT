package com.test.btg.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.boot.context.event.ApplicationReadyEvent;

@Slf4j
@Component
public class MongoHealthCheck {

    @EventListener(ApplicationReadyEvent.class)
    public void checkMongoConnection() {
        log.info("La aplicación está lista.");
    }
}