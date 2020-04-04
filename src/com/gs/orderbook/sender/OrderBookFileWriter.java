package com.gs.orderbook.sender;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class OrderBookFileWriter implements OrderBookSender {
    private final BufferedWriter theBufferedWriter;

    public OrderBookFileWriter(String aFileName) throws IOException {
        theBufferedWriter = new BufferedWriter(new FileWriter(aFileName));
    }

    @Override
    public void sendOrderBookUpdate(String anOrderBookUpdate) {
        try {
            theBufferedWriter.write(anOrderBookUpdate);
            theBufferedWriter.newLine();
        } catch (IOException ignored) {
            ignored.printStackTrace(); // Should not happen
        }
    }

    @Override
    public void stop() {
        try {
            theBufferedWriter.flush();
            theBufferedWriter.close();
        } catch (IOException ignored) {
            ignored.printStackTrace(); // Should not happen
        }
    }
}
