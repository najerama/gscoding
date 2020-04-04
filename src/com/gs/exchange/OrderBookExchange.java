package com.gs.exchange;

import com.gs.driver.OrderUpdate;
import com.gs.driver.OrderUpdateReceiver;
import com.gs.orderbook.OrderBook;
import com.gs.orderbook.sender.OrderBookSender;

import java.util.List;

public class OrderBookExchange {
    private final OrderUpdateReceiver theReceiver;
    private final OrderBookSender theSender;
    private final OrderBook theBook;
    private ExchangeState theState;

    public OrderBookExchange(OrderUpdateReceiver aReceiver, OrderBookSender aSender, OrderBook aBook) {
        theReceiver = aReceiver;
        theSender = aSender;
        theBook = aBook;
        theState = ExchangeState.INITIALIZATION;
    }
    
    public void run() {
        List<OrderUpdate> myUpdates;
        
        while (!(myUpdates = theReceiver.getNextUpdate()).isEmpty()) {
            theBook.update(myUpdates);
            if (theState == ExchangeState.STARTED || theBook.isComplete()) {
                theSender.sendOrderBookUpdate(theBook.getCurrentOrderBook());
                theState = ExchangeState.STARTED;
            }
        }
        
        cleanup();
    }
    
    private void cleanup() {
        theReceiver.stop();
        theSender.stop();
    }
    
    enum ExchangeState {
        INITIALIZATION,
        STARTED;
    }
}
