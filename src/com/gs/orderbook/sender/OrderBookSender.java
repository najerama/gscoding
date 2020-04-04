package com.gs.orderbook.sender;

public interface OrderBookSender {
    void sendOrderBookUpdate(String anOrderBookUpdate);
    void stop();
}
