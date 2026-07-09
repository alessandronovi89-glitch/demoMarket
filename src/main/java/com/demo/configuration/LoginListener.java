package com.demo.configuration;

import io.micronaut.runtime.event.annotation.EventListener;
import io.micronaut.security.event.LoginSuccessfulEvent;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Singleton
public class LoginListener {

    @EventListener
    public void onLoginSuccess(LoginSuccessfulEvent event) {
        log.info("AUTHENTICATION!!!!!!!!");
    }
}