package com.gs.orderbook.sender;

public class OrderBookPrinter implements OrderBookSender {
    @Override
    public void sendOrderBookUpdate(String anOrderBookUpdate) {
        System.out.println(anOrderBookUpdate);
    }

    @Override
    public void stop() {
        System.out.println("STOP");
    }
}
