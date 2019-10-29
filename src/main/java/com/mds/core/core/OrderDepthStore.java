package com.mds.core.core;

import com.mds.core.MainController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.*;

public class OrderDepthStore {

    private static final Logger log = LoggerFactory.getLogger(OrderDepthStore.class);

    private HashMap <String, List<PriceDepthData>> symbolMap = new HashMap();
    private ArrayList<OrderBook> orderBookArrayList;
    private Comparator<PriceDepthKey> PriceDepthKeyComparator = new PriceDepthKey("",true, (0));
    private TreeMap<PriceDepthKey, PriceDepthData> bidPriceDepth = new TreeMap<PriceDepthKey, PriceDepthData>(PriceDepthKeyComparator);
    private TreeMap<PriceDepthKey, PriceDepthData> askPriceDepth = new TreeMap<PriceDepthKey, PriceDepthData>(PriceDepthKeyComparator);


    public void setOrderBookArrayList(ArrayList<OrderBook> orderBookArrayList) {
        processOrders(orderBookArrayList);
    }

    private void processOrders(ArrayList<OrderBook> orderBookArrayList) {
        for (OrderBook orderBook : orderBookArrayList) {
            if ("NEW_ORDER".equalsIgnoreCase(orderBook.getType())) {
                addOrder(orderBook);
            } else if ("AMEND_ORDER".equalsIgnoreCase(orderBook.getType())) {
                amendOrder(orderBook);
            } else {
                deleteOrder(orderBook);
            }
        }
    }

    public void processOrders(OrderBook orderBook) {
        if ("NEW_ORDER".equalsIgnoreCase(orderBook.getType())) {
            addOrder(orderBook);
        } else if ("AMEND_ORDER".equalsIgnoreCase(orderBook.getType())) {
            amendOrder(orderBook);
        } else {
            deleteOrder(orderBook);
        }

    }

    private void addOrder(OrderBook orderBook) {

        PriceDepthKey priceDepthKey = getPriceDepthKey(orderBook.getSymbol(), orderBook.getSide(), orderBook.getLimitPrice());
        PriceDepthData priceDepthData = null;
        if (isBid(orderBook.getSide())) {
            priceDepthData = bidPriceDepth.get(priceDepthKey);
            if (priceDepthData == null) {
                priceDepthData = new PriceDepthData(priceDepthKey);
                bidPriceDepth.put(priceDepthKey, priceDepthData);
            }
        } else {
            priceDepthData = askPriceDepth.get(priceDepthKey);
            if (priceDepthData == null) {
                priceDepthData = new PriceDepthData(priceDepthKey);
                askPriceDepth.put(priceDepthKey, priceDepthData);
            }
        }
        priceDepthData.setQuantity(priceDepthData.getQuantity() + orderBook.getQuantity());
        priceDepthData.setNumOfOrders(priceDepthData.numOfOrders + 1);
        priceDepthData.setSymbol(orderBook.getSymbol());


    }


    public void getResult(){
        bidPriceDepth.keySet().stream().forEach(
                x -> System.out.println(x)
        )   ;
    }

    private void amendOrder(OrderBook orderBook) {

    }

    private void deleteOrder(OrderBook orderBook) {

    }

    private PriceDepthKey getPriceDepthKey( String symbol, String side, double price) {
        return new PriceDepthKey( symbol, isBid(side), price);
    }

    private boolean isBid(String side) {
        return "BUY".equalsIgnoreCase(side);
    }

}

