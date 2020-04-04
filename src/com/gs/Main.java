package com.gs;

import com.gs.driver.OrderUpdateFileReader;
import com.gs.driver.OrderUpdateReceiver;
import com.gs.exchange.OrderBookExchange;
import com.gs.orderbook.Level2OrderBook;
import com.gs.orderbook.OrderBook;
import com.gs.orderbook.sender.OrderBookFileWriter;
import com.gs.orderbook.sender.OrderBookPrinter;
import com.gs.orderbook.sender.OrderBookSender;

import java.io.*;

public class Main {

    public static void main(String[] args) throws IOException {
        String myInputFileName = "orderBookExercise.data";
        String myOutputFileName = "orderBookExercise.output";

        OrderBook myBook = new Level2OrderBook();
        OrderUpdateReceiver myReceiver = new OrderUpdateFileReader(myInputFileName);
        OrderBookSender mySender = new OrderBookPrinter();
        OrderBookSender myFileSender = new OrderBookFileWriter(myOutputFileName);
        OrderBookExchange myExchange = new OrderBookExchange(myReceiver, myFileSender, myBook);
        myExchange.run();
    }
}
