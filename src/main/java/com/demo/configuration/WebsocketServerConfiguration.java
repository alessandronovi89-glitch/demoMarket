package com.demo.configuration;

import io.micronaut.context.annotation.Property;
import jakarta.inject.Singleton;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;

@Singleton
@Log4j2
@Getter
@ToString
public class WebsocketServerConfiguration{
    //da vedere..
    /*@Property(name = "application.websocket.alive-message.initial-delay")
    private Integer aliveInitialDelay;
    @Property(name = "application.websocket.alive-message.delay")
    private Integer aliveDelay;
    @Property(name = "application.websocket.entity-status.buffer.initial-delay")
    private Integer entityStatusInitialDelay;
    @Property(name = "application.websocket.entity-status.buffer.delay")
    private Integer entityStatusDelay;
    */
}
