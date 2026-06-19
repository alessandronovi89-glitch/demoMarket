package com.demo.configuration;

import io.micronaut.context.annotation.ConfigurationProperties;
import io.micronaut.context.event.ApplicationEventListener;
import io.micronaut.runtime.server.event.ServerStartupEvent;
import lombok.Getter;

@ConfigurationProperties("demo")
@Getter
public class DemoConfig implements ApplicationEventListener<ServerStartupEvent> {

    private String testValue;

    @Override
    public void onApplicationEvent(ServerStartupEvent event) {
        System.out.println("value: "  + testValue);

    }
}