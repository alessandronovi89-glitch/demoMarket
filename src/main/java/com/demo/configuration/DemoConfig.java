package com.demo.configuration;

import io.micronaut.context.annotation.ConfigurationProperties;
import io.micronaut.context.event.ApplicationEventListener;
import io.micronaut.runtime.server.event.ServerStartupEvent;

@ConfigurationProperties("demo")
public class DemoConfig implements ApplicationEventListener<ServerStartupEvent> {

    private String testValue;

    public String getTestValue() {
        return testValue;
    }

    public void setTestValue(String testValue) {
        this.testValue = testValue;
    }


    @Override
    public void onApplicationEvent(ServerStartupEvent event) {
        System.out.println("value: "  + testValue);

    }
}