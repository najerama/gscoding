package com.gs.orderbook;

import com.gs.driver.OrderUpdate;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Level2OrderBook implements OrderBook {
    private static final int DEFAULT_MAX_LEVELS = 10;

    // naive to use an array, maybe use a linked list for easy removal and easy insert, but search sucks, especially
    // when updates are not ordered by level
    private final Level[] theBids;
    private final Level[] theAsks;
    private final int theMaxLevels;
    private long theSequenceNumber;
    private boolean theBidsAllSeen;
    private boolean theAsksAllSeen;

    public Level2OrderBook() {
        this(DEFAULT_MAX_LEVELS);
    }

    // @VisibleForTesting
    Level2OrderBook(int aMaxLevel) {
        theMaxLevels = aMaxLevel;
        theBids = new Level[theMaxLevels];
        theAsks = new Level[theMaxLevels];
        for (int i = 0; i < theMaxLevels; i++) {
            theBids[i] = new Level();
            theAsks[i] = new Level();
        }
        theBidsAllSeen = false;
        theAsksAllSeen = false;
        theSequenceNumber = 0;
    }

    @Override
    public void update(List<OrderUpdate> anOrderUpdates) {
        for (OrderUpdate myUpdate : anOrderUpdates) {
            theSequenceNumber = myUpdate.getSequenceNumber();
            checkIfHaveSeenAllLevelsNow(myUpdate);

            Level[] myLevels = myUpdate.getSide() == OrderUpdate.Side.BID ? theBids : theAsks;

            switch (myUpdate.getAction()) {
                case UPDATE_LEVEL:
                    myLevels[myUpdate.getLevelNumber()].update(myUpdate);
                    break;

                case NEW_LEVEL:
                    shiftLevelsUp(myLevels, myUpdate.getLevelNumber());
                    myLevels[myUpdate.getLevelNumber()] = new Level(myUpdate);
                    break;

                case DELETE_LEVEL:
                    // TODO: Sanity check that we're deleting the right level
                    Level myLevel = myLevels[myUpdate.getLevelNumber()];
                    myLevel.delete();
                    shiftLevelsDown(myLevels, myUpdate.getLevelNumber()+1);
                    myLevels[theMaxLevels -1] = myLevel;
                    break;

                default:
                    assert false : "Should never get here";
            }
        }
    }

    // @VisibleForTesting
    Level[] bids() {
        return theBids;
    }

    // @VisibleForTesting
    Level[] asks() {
        return theAsks;
    }

    private void shiftLevelsUp(Level[] aLevels, int aStartingPoint) {
        System.arraycopy(aLevels, aStartingPoint, aLevels, aStartingPoint + 1, aLevels.length - 1 - aStartingPoint);
    }

    private void shiftLevelsDown(Level[] aLevels, int aStartingPoint) {
        System.arraycopy(aLevels, aStartingPoint, aLevels, aStartingPoint-1, aLevels.length - aStartingPoint);
    }

    @Override
    public boolean isComplete() {
        return theBidsAllSeen && theAsksAllSeen;
    }

    @Override
    public String getCurrentOrderBook() {
        return toString();
    }

    @Override
    public String toString() {
        return theSequenceNumber + ","
                + Arrays.stream(theBids).map(Level::toString).collect(Collectors.joining(","))
                + Arrays.stream(theAsks).map(Level::toString).collect(Collectors.joining(","));
    }

    // Assume no malformed input, see below for malformed updates
    private void checkIfHaveSeenAllLevelsNow(OrderUpdate anOrderUpdate) {
        if (!isComplete() && anOrderUpdate.getLevelNumber() == theMaxLevels -1) { // zero indexed
            if (anOrderUpdate.getSide() == OrderUpdate.Side.BID) {
                theBidsAllSeen = true;
            } else {
                theAsksAllSeen = true;
            }
        }
    }

    // malformed updates allowed

//    private boolean theIsCompleted;
//    @Override
//    public boolean isComplete() {
//        return theIsCompleted;
//    }

//    private final boolean[] theSeenSoFar = new boolean[20]; // bids, asks
//    private void checkIfHaveSeenAllLevelsNow(List<OrderUpdate> anOrderUpdate) {
//        if (!theIsCompleted) {
//            for (OrderUpdate myUpdate : anOrderUpdate) {
//                int i = myUpdate.getSide() == OrderUpdate.Side.BID ? myUpdate.getLevelNumber() : myUpdate.getLevelNumber() + 10;
//                theSeenSoFar[i] = true;
//            }
//
//            boolean myIsComplete = true;
//            for (boolean mySeen : theSeenSoFar) {
//                myIsComplete &= mySeen;
//            }
//            theIsCompleted = myIsComplete;
//        }

    static class Level {
        private double thePrice;
        private int theSize;
        private boolean theEmpty;

        Level(OrderUpdate anUpdate) {
            update(anUpdate);
        }

        Level() {
            theEmpty = true;
        }

        void delete() {
            theEmpty = true;
        }

        void update(OrderUpdate anUpdate) {
            thePrice = anUpdate.getPrice();
            theSize = anUpdate.getSize();
            theEmpty = false;
        }

        public double getPrice() {
            return thePrice;
        }

        public int getSize() {
            return theSize;
        }

        public boolean isEmpty() {
            return theEmpty;
        }

        @Override
        public String toString() {
            return theEmpty ? "-,-" : "" + thePrice + "," + theSize;
        }
    }
}
