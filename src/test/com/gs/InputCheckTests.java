package com.gs;

import com.gs.driver.OrderUpdate;
import com.gs.driver.OrderUpdateReceiver;
import com.gs.driver.OrderUpdateFileReader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.util.List;

class InputCheckTests {
    private OrderUpdateReceiver theEngine;

    @BeforeEach
    void setUp() throws FileNotFoundException {
        theEngine = new OrderUpdateFileReader("orderBookExercise.data");
    }

    @Test
    void sequenceNumberFitsInLong() {
        List<OrderUpdate> myUpdates;
        while (!(myUpdates = theEngine.getNextUpdate()).isEmpty()) {
            OrderUpdate myUpdate = myUpdates.get(0);
            assert myUpdate.getSequenceNumber() < Integer.MAX_VALUE;
        }
    }

    @Test
    void isNotOutOfSequence() {
        long mySeq = 0;
        List<OrderUpdate> myUpdates;
        while (!(myUpdates = theEngine.getNextUpdate()).isEmpty()) {
            OrderUpdate myUpdate = myUpdates.get(0);
            assert myUpdate.getSequenceNumber() > mySeq;
            mySeq = myUpdate.getSequenceNumber();
        }
    }

    @Test
    void areLevelsOrdered() {
        List<OrderUpdate> myUpdates;
        while (!(myUpdates = theEngine.getNextUpdate()).isEmpty()) {
            int myBidLevel = -1;
            int myAskLevel = -1;
            for (OrderUpdate myUpdate : myUpdates) {
                if (myUpdate.getSide() == OrderUpdate.Side.BID) {
                    assert myUpdate.getLevelNumber() >= myBidLevel : myUpdate.getSequenceNumber();
//                    if (myUpdate.getLevelNumber() < myBidLevel)
//                        System.out.println(myUpdate.getSequenceNumber());
                    myBidLevel = myUpdate.getLevelNumber();
                } else {
                    assert myUpdate.getLevelNumber() >= myAskLevel : myUpdate.getSequenceNumber();
//                    if (myUpdate.getLevelNumber() < myAskLevel)
//                        System.out.println(myUpdate.getSequenceNumber());
                    myAskLevel = myUpdate.getLevelNumber();
                }
            }
        }
    }

    @Test
    void levelsAreBelow10() {
        List<OrderUpdate> myUpdates;
        while (!(myUpdates = theEngine.getNextUpdate()).isEmpty()) {
            for (OrderUpdate myUpdate : myUpdates) {
                assert myUpdate.getLevelNumber() < 10;
            }
        }
    }
}