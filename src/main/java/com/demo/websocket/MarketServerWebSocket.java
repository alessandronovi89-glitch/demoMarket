package com.demo.websocket;

import io.micronaut.websocket.WebSocketSession;
import io.micronaut.websocket.annotation.OnClose;
import io.micronaut.websocket.annotation.OnMessage;
import io.micronaut.websocket.annotation.OnOpen;
import io.micronaut.websocket.annotation.ServerWebSocket;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;

//per il websocket server e cliente vedere le guide e il progetto e IA ecc
//così anche per reactive ecc

@Slf4j
@ServerWebSocket("/ws/market/{topic}")
@Singleton //necessario?
public class MarketServerWebSocket {

    @OnOpen
    public void onOpen(WebSocketSession session, String topic) {
        log.info("WebSocket opened for topic: " + topic);
    }

    @OnMessage
    public void onMessage(WebSocketSession session, String message) {
        log.info("Received message: " + message);
        // Echo the message back to the client
        session.sendSync("Echo: " + message);
    }

    @OnClose
    public void onClose(WebSocketSession session, String topic) {
        log.info("WebSocket closed for topic: " + topic);
    }
}
