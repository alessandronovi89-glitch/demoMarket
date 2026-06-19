package com.demo.client;

import com.demo.proto.market.MarketMessage;
import lombok.SneakyThrows;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.nio.ByteBuffer;
import java.util.concurrent.CompletionStage;
/*
Un client di prova per testare il websocket e la lettura dei dati
* */
public class WsClient {

    public static void main(String[] args) throws Exception {

        HttpClient client = HttpClient.newHttpClient();

        WebSocket webSocket = client.newWebSocketBuilder()
                .buildAsync(URI.create("ws://localhost:8080/ws/market"), new WebSocket.Listener() {

                    @Override
                    public void onOpen(WebSocket webSocket) {
                        System.out.println("Connected");
                        webSocket.request(1);
                    }

                    @Override
                    public CompletionStage<?> onText(WebSocket webSocket, CharSequence data, boolean last) {
                        System.out.println("Received text: " + data);
                        webSocket.request(1);
                        return null;
                    }

                    @Override
                    @SneakyThrows
                    public CompletionStage<?> onBinary(WebSocket webSocket, ByteBuffer data, boolean last) {
                        System.out.println("Received binary message: " + data.remaining() + " bytes");
                        MarketMessage marketMessage = MarketMessage.parseFrom(data);
                        if(marketMessage.hasQuote()){
                            System.out.println(marketMessage.getQuote());
                        }
                        else{
                            System.out.println(marketMessage.getPosition());
                        }
                        webSocket.request(1);
                        return null;
                    }

                    @Override
                    public void onError(WebSocket webSocket, Throwable error) {
                        System.out.println("Error: " + error.getMessage());
                    }
                }).join();

        // 🔥 esempio invio testo
        webSocket.sendText("quote TSLA", true);
        System.out.println("Message sent");
        Thread.sleep(60000);
        webSocket.sendText("position", true);
        Thread.sleep(1000000); // tieni vivo il client
    }
}