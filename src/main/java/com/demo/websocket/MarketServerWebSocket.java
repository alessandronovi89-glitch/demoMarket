package com.demo.websocket;

import com.demo.configuration.DemoConfig;
import com.demo.models.Position;
import com.demo.models.Quote;
import com.demo.service.GeneratorData;
import io.micronaut.websocket.WebSocketSession;
import io.micronaut.websocket.annotation.OnClose;
import io.micronaut.websocket.annotation.OnMessage;
import io.micronaut.websocket.annotation.OnOpen;
import io.micronaut.websocket.annotation.ServerWebSocket;
import io.reactivex.rxjava3.disposables.Disposable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;


//per il websocket server e cliente vedere le guide e il progetto e IA ecc
//così anche per reactive ecc
//protobuffer..

/*
quotes → streaming veloce (tick / update)
positions → stato utente
instrument → metadata relativamente statico
*/


@Slf4j
@ServerWebSocket("/ws/market")
@RequiredArgsConstructor
public class MarketServerWebSocket {
    private final GeneratorData generatorData;
    private final DemoConfig demoConfig;
    private Disposable positionDisposable;
    private Disposable quoteDisposable;
    private ConcurrentHashMap<WebSocketSession, Set<String>> webSocketFilters = new ConcurrentHashMap<>();
    private final Object lock = new Object();

    //perchè i websocket con postman durano solo 5 minuti? -> idle timeout da parte del clinet di default
    //ma l'application yml lo sta leggendo?, no perchè non sembra.. bo
    @OnOpen
    public void onOpen(WebSocketSession session) {
        log.info("WebSocket opened");
        session.sendSync("Welcome, " + session.getId()); //send a welcome message to the client
        synchronized (lock) {
            if (webSocketFilters.isEmpty()) {
                quoteDisposable = generatorData.getQuoteEmitter().subscribe(this::dispatch);
                positionDisposable = generatorData.getPositionEmitter().subscribe(this::dispatch);
            }
            webSocketFilters.put(session, ConcurrentHashMap.newKeySet());
        }
    }

    @OnMessage
    public void onMessage(WebSocketSession session, String message) {
        log.info("Received message: " + message);
        if(message.equals("ping")){
            return; //message from client for keeping alive the connection
        }
        try {
            if (message.startsWith("add")) { //se vuoi concatenare interessi
                webSocketFilters.get(session).add(message.substring(4).trim()); //è una bozza!
            } else {
                webSocketFilters.replace(session, ConcurrentHashMap.newKeySet());
                webSocketFilters.get(session).add(message);
            }
        }catch (Exception e){
            log.error("error in subscription with message: {}", message);
        }
    }

    private void dispatch(Quote quote) {
        webSocketFilters.keySet().forEach(session-> {
            sendGenericQuote(quote, session);
            sendSpecificQuote(quote, session);
        });
    }

    private void sendGenericQuote(Quote quote, WebSocketSession session) {
        try {
            if (webSocketFilters.get(session).contains("quote")) { //ha solo quote -> inviamo tutte le quote
                session.sendAsync(quote.toString());
            }
        } catch (Exception e) {
            log.error("Error in sending generic quote: {}", quote);
        }
    }

    private void sendSpecificQuote(Quote quote, WebSocketSession session) {
        try {
            webSocketFilters.get(session).stream().filter(s->s.startsWith("quote ")).forEach(subscription -> {
               String symbol = subscription.split(" ")[1];
               if(symbol.equals(quote.getSymbol())) {
                   session.sendAsync(quote.toString());
               }
            });
        } catch (Exception e) {
            log.error("Error in sending a specific quote: {}", quote);
        }
    }



    private void dispatch(Position position) {
        webSocketFilters.keySet().forEach(session->{
            try {
                if(webSocketFilters.get(session).contains("position")){
                    session.sendAsync(position.toString());
                }
            } catch (Exception e) {
                log.error("Error in sending position: ", position.toString());
            }
        });
    }

    @OnClose
    public void onClose(WebSocketSession session) {
        log.info("WebSocket closed");
        synchronized (lock) {
            webSocketFilters.remove(session);
            if (webSocketFilters.isEmpty()) { //problema concorrenza
                closeDisposable(positionDisposable);
                closeDisposable(quoteDisposable);
            }
        }
    }

    private void closeDisposable(Disposable disposable) {
        if (disposable != null) {
            disposable.dispose();
        }
    }


}
