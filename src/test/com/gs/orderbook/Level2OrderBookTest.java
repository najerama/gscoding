package com.gs.orderbook;

import com.gs.driver.OrderUpdate;
import com.gs.orderbook.Level2OrderBook.Level;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static com.gs.driver.OrderUpdate.Action.DELETE_LEVEL;
import static com.gs.driver.OrderUpdate.Action.NEW_LEVEL;
import static com.gs.driver.OrderUpdate.Side.ASK;
import static com.gs.driver.OrderUpdate.Side.BID;

class Level2OrderBookTest {
    private final static double EPSILON = 0.00001;
    private static final int MAX_LEVEL = 3;
    private Level2OrderBook theBook;
    private long theSequenceNumber = 1;
    private List<OrderUpdate> theUpdates;

    @BeforeEach
    void setUp()  {
        theBook = new Level2OrderBook(MAX_LEVEL);
        theUpdates = new ArrayList<>();
    }

    @Test
    void simpleBookTest() {
        addUpdate(NEW_LEVEL, BID, 0, 10., 1);
        addUpdate(NEW_LEVEL, ASK, 0, 11., 1);
        sendUpdate();

        assertLevel(theBook.bids()[0], 10., 1);
        assertLevel(theBook.asks()[0], 11., 1);
        assertEmptyLevels(theBook.bids(), 1);
        assertEmptyLevels(theBook.asks(), 1);
        assert !theBook.isComplete();
    }

    @Test
    void fullBook() {
        sendFullBookUpdate();

        assertNonEmptyLevels(theBook.bids(), 0);
        assertNonEmptyLevels(theBook.asks(), 0);
        assert theBook.isComplete();
    }

    @Test
    void fullBookOnSubsequentUpdates() {
        addUpdate(NEW_LEVEL, BID, 0, 10., 1);
        addUpdate(NEW_LEVEL, BID, 1, 9., 1);
        addUpdate(NEW_LEVEL, BID, 2, 8., 1);
        sendUpdate();

        assert !theBook.isComplete();
        assertNonEmptyLevels(theBook.bids(), 0);
        assertEmptyLevels(theBook.asks(), 0);

        addUpdate(DELETE_LEVEL, BID, 2, 8., 1);
        addUpdate(NEW_LEVEL, ASK, 0, 11., 1);
        addUpdate(NEW_LEVEL, ASK, 1, 12., 1);
        addUpdate(NEW_LEVEL, ASK, 2, 13., 1);
        sendUpdate();

        assertEmptyLevels(theBook.bids(), 2);
        assertNonEmptyLevels(theBook.asks(), 0);
        assert theBook.isComplete();
    }

    @Test
    void deleteFirstLevel() {
        sendFullBookUpdate();
        addUpdate(DELETE_LEVEL, BID, 0, 10., 1);
        addUpdate(DELETE_LEVEL, ASK, 0, 11., 1);
        sendUpdate();

        assertEmptyLevels(theBook.bids(), 2);
        assertEmptyLevels(theBook.asks(), 2);
    }

    @Test
    void insertAfterDeletionsLevel() {
        sendFullBookUpdate();
        addUpdate(DELETE_LEVEL, BID, 1, 9., 1);
        sendUpdate();
        addUpdate(DELETE_LEVEL, BID, 0, 10., 1);
        sendUpdate();
        addUpdate(NEW_LEVEL, BID, 1, 7., 1);
        sendUpdate();

        assertLevel(theBook.bids()[0], 8., 1);
        assertLevel(theBook.bids()[1], 7., 1);
        assertEmptyLevels(theBook.bids(), 2);
        assertNonEmptyLevels(theBook.asks(), 0);
    }

    private void sendFullBookUpdate() {
        addUpdate(NEW_LEVEL, BID, 0, 10., 1);
        addUpdate(NEW_LEVEL, BID, 1, 9., 1);
        addUpdate(NEW_LEVEL, BID, 2, 8., 1);
        addUpdate(NEW_LEVEL, ASK, 0, 11., 1);
        addUpdate(NEW_LEVEL, ASK, 1, 12., 1);
        addUpdate(NEW_LEVEL, ASK, 2, 13., 1);
        sendUpdate();
    }

    private void assertLevel(Level aLevel, double aPrice, int aSize) {
        assert equals(aPrice, aLevel.getPrice());
        assert aSize == aLevel.getSize();
        assert !aLevel.isEmpty();
    }

    private void assertEmptyLevels(Level[] aLevels, int aStart) {
        for (int i = aStart; i < MAX_LEVEL; i++) {
            assert aLevels[i].isEmpty();
        }
    }

    private void assertNonEmptyLevels(Level[] aLevels, int aStart) {
        for (int i = aStart; i < MAX_LEVEL; i++) {
            assert !aLevels[i].isEmpty();
        }
    }

    private void addUpdate(OrderUpdate.Action anAction, OrderUpdate.Side aSide, int aLevel, double aPrice, int aSize) {
        theUpdates.add(new OrderUpdate(theSequenceNumber, anAction, aLevel, aSide, aPrice, aSize));
    }

    private void sendUpdate() {
        theBook.update(theUpdates);
        theUpdates.clear();
        theSequenceNumber++;
    }

    private static boolean equals(double a, double b){
        return a == b || Math.abs(a - b) < EPSILON;
    }
}