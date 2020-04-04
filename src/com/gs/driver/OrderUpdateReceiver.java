package com.gs.driver;

import java.util.List;

public interface OrderUpdateReceiver {
    // Returns a list of order updates for the next sequence number
    // Returns empty list when there are no more updates, ideally block until more updates in the stream, but file
    // reading is pretty straightforward. Prod should probably be an event based architecture.
    // Performance trade-off, might be better to have an object pool of OrderUpdates so we limit memory churn
    List<OrderUpdate> getNextUpdate();

    void stop();
}
