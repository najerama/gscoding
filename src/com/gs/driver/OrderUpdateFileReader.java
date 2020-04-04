package com.gs.driver;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.gs.driver.OrderUpdate.*;

public class OrderUpdateFileReader implements OrderUpdateReceiver {
    private final BufferedReader theFileReader;
    private OrderUpdate theLastUpdateRead = null;
    private boolean theFileIsDone = false;

    public OrderUpdateFileReader(String anInputFileName) throws FileNotFoundException {
        theFileReader = new BufferedReader(new FileReader(anInputFileName));
    }

    // @VisibleForTesting
    OrderUpdateFileReader(BufferedReader aBufferedReader) {
        theFileReader = aBufferedReader;
    }

    @Override
    public List<OrderUpdate> getNextUpdate() {
        List<OrderUpdate> myNextUpdates = new ArrayList<>();

        if (theFileIsDone) {
            return myNextUpdates;
        }

        OrderUpdate myUpdate;
        long myCurrentSequenceNum;
        try {
            myUpdate = theLastUpdateRead != null ? theLastUpdateRead : decodeExchangePacket(theFileReader.readLine());
            myCurrentSequenceNum = myUpdate.getSequenceNumber();

            while (myUpdate.getSequenceNumber() == myCurrentSequenceNum) {
                myNextUpdates.add(myUpdate);

                String myNextLine = theFileReader.readLine();
                if (myNextLine == null || myNextLine.isEmpty()) {
                    theFileIsDone = true;
                    break;
                }
                myUpdate = decodeExchangePacket(myNextLine);
            }

        } catch (IOException anIgnoredEofException) { // We should not get here since we have a file done flag
            return myNextUpdates;
        }

        theLastUpdateRead = myUpdate;
        return myNextUpdates;
    }

    @Override
    public void stop() {
        try {
            theFileReader.close();
        } catch (IOException e) { // should not happen
            e.printStackTrace();
        }
    }

    // @VisibleForTesting
    OrderUpdate decodeExchangePacket(String aPacket) {
        String[] mySplitPacket = aPacket.split(",");
        assert mySplitPacket.length == 6 : "ERROR: Order level update has incorrect number of arguments";

        long mySeqNum = Long.parseLong(mySplitPacket[0]);
        Action myAction = Action.getAction(mySplitPacket[1].charAt(0));
        int myLevel = Integer.parseInt(mySplitPacket[2]);
        Side mySide = (mySplitPacket[3].equals("B")) ? Side.BID : Side.ASK;
        double myPrice = Double.parseDouble(mySplitPacket[4]);
        int mySize = Integer.parseInt(mySplitPacket[5]);
        return new OrderUpdate(mySeqNum, myAction, myLevel, mySide, myPrice, mySize);
    }
}
