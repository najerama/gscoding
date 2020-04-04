package com.gs.driver;

import java.util.Objects;

public class OrderUpdate {
    private final long theSequenceNumber;
    private final Action theAction;
    private final int theLevelNumber;
    private final Side theSide;
    private final double thePrice;
    private final int theSize;

    public OrderUpdate(long aSeqNum, Action anAction, int aLevelNum, Side aSide, double aPrice, int aSize) {
        theSequenceNumber = aSeqNum;
        theAction = anAction;
        theLevelNumber = aLevelNum;
        theSide = aSide;
        thePrice = aPrice;
        theSize = aSize;
    }

    public long getSequenceNumber() {
        return theSequenceNumber;
    }

    public Action getAction() {
        return theAction;
    }

    public int getLevelNumber() {
        return theLevelNumber;
    }

    public Side getSide() {
        return theSide;
    }

    public double getPrice() {
        return thePrice;
    }

    public int getSize() {
        return theSize;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderUpdate that = (OrderUpdate) o;
        return theSequenceNumber == that.theSequenceNumber &&
                theLevelNumber == that.theLevelNumber &&
                Double.compare(that.thePrice, thePrice) == 0 &&
                theSize == that.theSize &&
                theAction == that.theAction &&
                theSide == that.theSide;
    }

    @Override
    public int hashCode() {
        return Objects.hash(theSequenceNumber, theAction, theLevelNumber, theSide, thePrice, theSize);
    }

    @Override
    public String toString() {
        return "OrderUpdate{" +
                "theSequenceNumber=" + theSequenceNumber +
                ", theAction=" + theAction +
                ", theLevelNumber=" + theLevelNumber +
                ", theSide=" + theSide +
                ", thePrice=" + thePrice +
                ", theSize=" + theSize +
                '}';
    }

    public enum Action {
        DELETE_LEVEL,
        NEW_LEVEL,
        UPDATE_LEVEL;

        static Action getAction(char aChar) {
            if (aChar == 'U') { // assuming updates are most frequent
                return UPDATE_LEVEL;
            } else if (aChar == 'N') {
                return NEW_LEVEL;
            }
            return DELETE_LEVEL; // default case, assumption on no bad input
        }
    }

    public enum Side {
        BID,
        ASK;
    }
}
