package com.gs.orderbook;

import com.gs.driver.OrderUpdate;

import java.util.List;

public interface OrderBook {
    // Update internal order book based on sorted updates
    void update(List<OrderUpdate> anOrderUpdate);

    // have all levels received an update?
    boolean isComplete();

    // Debated having a separate "RetailState" class for the output format, but for time will keep it to toString() here
    String getCurrentOrderBook();
}
