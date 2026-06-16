package com.demo.service;

import com.demo.models.Position;
import com.demo.models.Quote;
import io.micronaut.scheduling.annotation.Scheduled;
import io.reactivex.rxjava3.core.BackpressureStrategy;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.subjects.BehaviorSubject;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Slf4j
@Singleton
public class GeneratorData {

    private static final List<String> SYMBOLS = List.of("BTC-USD", "ETH-USD", "AAPL", "TSLA", "AMZN");
    private static final List<String> ACCOUNT_IDS = List.of("U123", "U456", "U789");
    private static final List<String> SIDES = List.of("LONG", "SHORT");

    private final Random random = new Random();

    private BehaviorSubject<Quote> quoteEmitter = BehaviorSubject.create();
    private BehaviorSubject<Position> positionEmitter = BehaviorSubject.create();
    public GeneratorData() {
        Disposable quoteDisposable = quoteEmitter.subscribe((t)->log.info("Quote: {}", t));
        Disposable positionDisposable = positionEmitter.subscribe((p)->log.info("Position: {}", p));

        CompletableFuture.delayedExecutor(10, TimeUnit.SECONDS)
                .execute(() -> {
                    log.info("Disposing subscriptions...");
                    quoteDisposable.dispose();
                    positionDisposable.dispose();
                });
    }

    @Scheduled(fixedRate = "200ms")
    void generateMarketData() {
        Quote quote = generateQuote();
        Position position = generatePosition();
        quoteEmitter.onNext(quote);
        positionEmitter.onNext(position);
    }

    @Scheduled(fixedRate = "500ms")
    void reader() {

    }

    private Quote generateQuote() {
        String symbol = SYMBOLS.get(random.nextInt(SYMBOLS.size()));
        double mid = 100 + random.nextDouble() * 60_000;
        double spread = mid * 0.0001;
        double bid = round(mid - spread);
        double ask = round(mid + spread);
        double last = round(bid + random.nextDouble() * spread * 2);
        double volume = round(random.nextDouble() * 50_000);
        return new Quote("QUOTE", symbol, bid, ask, last, volume, System.currentTimeMillis());


    }

    private Position generatePosition() {
        String accountId = ACCOUNT_IDS.get(random.nextInt(ACCOUNT_IDS.size()));
        String symbol = SYMBOLS.get(random.nextInt(SYMBOLS.size()));
        String side = SIDES.get(random.nextInt(SIDES.size()));
        int size = 1 + random.nextInt(100);
        double avgEntry = round(50 + random.nextDouble() * 30_000);
        double markPrice = round(avgEntry * (1 + (random.nextDouble() - 0.5) * 0.1));
        double unrealizedPnL = round((markPrice - avgEntry) * size * (side.equals("SHORT") ? -1 : 1));
        return new Position(accountId, symbol, side, size, avgEntry, markPrice, unrealizedPnL);
    }

    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
