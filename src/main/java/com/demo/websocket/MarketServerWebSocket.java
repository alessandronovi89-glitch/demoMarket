package com.demo.websocket;

import com.demo.service.GeneratorData;
import io.micronaut.websocket.WebSocketBroadcaster;
import io.micronaut.websocket.WebSocketSession;
import io.micronaut.websocket.annotation.OnClose;
import io.micronaut.websocket.annotation.OnMessage;
import io.micronaut.websocket.annotation.OnOpen;
import io.micronaut.websocket.annotation.ServerWebSocket;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.disposables.Disposable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;


//per il websocket server e cliente vedere le guide e il progetto e IA ecc
//così anche per reactive ecc
//protobuffer..

/*
quotes → streaming veloce (tick / update)
positions → stato utente
instrument → metadata relativamente statico
*/


@Slf4j
@ServerWebSocket("/ws/market/{topic}")
@RequiredArgsConstructor
public class MarketServerWebSocket {
    private final WebSocketBroadcaster broadcaster;
    private final GeneratorData generatorData;
    private Map<String, Disposable> disposableMap = new HashMap<>();
    private Map<String, List<WebSocketSession>> webSocketSessionMap = new HashMap();

    //concorrenza? ConcurrentHashMap
    @OnOpen
    public void onOpen(WebSocketSession session, String topic) {
        log.info("WebSocket opened for topic: " + topic);
        session.sendSync("Welcome, " + session.getId()); //send a welcome message to the client
        webSocketSessionMap.computeIfAbsent(topic, k -> new ArrayList<>())
                .add(session);
        disposableMap.put(session.getId(), sendByTopic(topic));

    }

    private Disposable sendByTopic(String topic) {
        Predicate<WebSocketSession> filterSession = (session) ->
                webSocketSessionMap.get(topic).contains(session);
        //broadcast non va piu bene..
        return switch (topic) {
            case "quote" -> generatorData.getQuoteEmitter()
                    .subscribe(q -> broadcaster.broadcastAsync("quote: " + q, filterSession));
            case "position" -> generatorData.getPositionEmitter()
                    .subscribe(p -> broadcaster.broadcastAsync("position: " + p, filterSession));
            default -> Flowable.merge(generatorData.getPositionEmitter(),
                            generatorData.getQuoteEmitter())
                    .onBackpressureBuffer()
                    .subscribe(el -> broadcaster.broadcastAsync("element: " + el, filterSession));
        };

    }

    @OnMessage
    public void onMessage(WebSocketSession session, String message) {
        log.info("Received message: " + message);
    }

    @OnClose
    public void onClose(WebSocketSession session, String topic) {
        log.info("WebSocket closed for topic: " + topic);
        if(disposableMap.get(session.getId())!=null){
            disposableMap.get(session.getId()).dispose();
            disposableMap.remove(session.getId()); //clean the map
            if(webSocketSessionMap.containsKey(topic)){
                webSocketSessionMap.get(topic).remove(session);
            }
        }
    }


}
