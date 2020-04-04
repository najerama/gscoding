package com.gs.driver;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;


class OrderUpdateFileReaderTest {
    private OrderUpdateFileReader theOrderUpdateFileReader;

    @BeforeEach
    public void beforeEach() {
        BufferedReader myReader = null;
        theOrderUpdateFileReader = new OrderUpdateFileReader(myReader);
    }

    //debated adding a test for a full buffer reader, will leave this if I have time, either mocking or actual file read

    @Test
    public void decodeExample() {
        String myTestPacket = "7930251,U,1,B,132.891,36";
        OrderUpdate orderUpdate = theOrderUpdateFileReader.decodeExchangePacket(myTestPacket);

        assert orderUpdate.getSequenceNumber() == 7930251L;
        assert orderUpdate.getAction() == OrderUpdate.Action.UPDATE_LEVEL;
        assert orderUpdate.getLevelNumber() == 1;
        assert orderUpdate.getSide() == OrderUpdate.Side.BID;
        assert orderUpdate.getPrice() == 132.891;
        assert orderUpdate.getSize() == 36;
    }
}