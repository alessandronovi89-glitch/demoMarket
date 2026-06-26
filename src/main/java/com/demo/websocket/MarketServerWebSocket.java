package com.demo.websocket;

import com.demo.models.Position;
import com.demo.models.Quote;
import com.demo.proto.market.MarketMessage;
import com.demo.proto.market.PositionMessage;
import com.demo.proto.market.QuoteMessage;
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


/*
quotes → streaming veloce (tick / update)
positions → stato utente
instrument → metadata relativamente statico
*/
//TODO @ClientWebSocket o neovisionaries?

@Slf4j
@ServerWebSocket("/ws/market")
@RequiredArgsConstructor
public class MarketServerWebSocket {
    private final GeneratorData generatorData;
    private Disposable positionDisposable;
    private Disposable quoteDisposable;
    private ConcurrentHashMap<WebSocketSession, Set<String>> webSocketFilters = new ConcurrentHashMap<>();
    private final Object lock = new Object();

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
                session.sendAsync(toMarketMessageByte(quote));
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
                   session.sendAsync(toMarketMessageByte(quote));
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
                    session.sendAsync(toMarketMessageByte(position));
                }
            } catch (Exception e) {
                log.error("Error in sending position: ", position.toString());
            }
        });
    }

    private static byte[] toMarketMessageByte(Position position) {
        MarketMessage positionMessage = MarketMessage.newBuilder().setPosition(
                PositionMessage.newBuilder()
                .setSymbol(position.getSymbol())
                .setAccountId(position.getAccountId())
                .setSide(position.getSide())
                .setSize(position.getSize())
                .setMarkPrice(position.getMarkPrice().toPlainString())
                .setAvgEntryPrice(position.getAvgEntryPrice().toPlainString())
                .setUnrealizedPnL(position.getUnrealizedPnL().toPlainString())
                .build()
        ).build();
        return positionMessage.toByteArray();
    }

    private static byte[] toMarketMessageByte(Quote quote) {
        MarketMessage quoteMessage = MarketMessage.newBuilder().setQuote(
                QuoteMessage.newBuilder()
                .setSymbol(quote.getSymbol())
                .setAsk(quote.getAsk().toPlainString())
                .setBid(quote.getBid().toPlainString())
                .setLast(quote.getLast().toPlainString())
                .setType(quote.getType())
                .setVolume24H(quote.getVolume24h())
                .setTimestamp(quote.getTimestamp())
                .build()
        ).build();
        return quoteMessage.toByteArray();
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
