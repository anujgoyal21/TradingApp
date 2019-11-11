package com.mds.core;

import org.junit.Assert;
import org.junit.Test;

import java.util.*;
import java.util.Random;

public class OrderDepthStoreTest {

    @Test
    public void testMarketDepthMultipleOrdersWithSamePriceAndDirectionToSumAllQuantities() {
        OrderDepthStore orderDepthStore = new OrderDepthStore();
        generateNewOrdersAndAddToStore(orderDepthStore, 10, "BUY", false);
        generateNewOrdersAndAddToStore(orderDepthStore, 10, "SELL", false);
        orderDepthStore.getMarketDataOrderDepth("TCS").stream().forEach(
                marketDepth -> {
                    Assert.assertEquals(100d, marketDepth.getBidPrice(), 1);
                    Assert.assertEquals(10000d, marketDepth.getBidSize(), 1);
                    Assert.assertEquals(100d, marketDepth.getOfferPrice(), 1);
                    Assert.assertEquals(10000d, marketDepth.getOfferSize(), 1);
                }

        );
    }

    @Test
    public void testMarketDepthMultipleOrdersWithDifferentPriceAndDirectionVerifyBidIsGreater() {
        OrderDepthStore orderDepthStore = new OrderDepthStore();
        generateNewOrdersAndAddToStore(orderDepthStore, 10, "BUY", true);
        generateNewOrdersAndAddToStore(orderDepthStore, 10, "SELL", true);
        orderDepthStore.getMarketDataOrderDepth("TCS").stream().forEach(
                marketDepth -> {
                     Assert.assertTrue( marketDepth.getBidPrice() >= marketDepth.getOfferPrice());
                }
        );

        Assert.assertTrue(orderDepthStore.getMarketDataOrderDepth("TCS").size()==5);
    }

    @Test
    public void testMarketDepthMultipleOrdersWithSamePriceAndOnlyBuyDirection() {
        OrderDepthStore orderDepthStore = new OrderDepthStore();
        generateNewOrdersAndAddToStore(orderDepthStore, 10, "BUY", false);
        orderDepthStore.getMarketDataOrderDepth("TCS").stream().forEach(
                marketDepth -> {
                    Assert.assertEquals(100d, marketDepth.getBidPrice(), 1);
                    Assert.assertEquals(10000d, marketDepth.getBidSize(), 1);
                    Assert.assertEquals(0d, marketDepth.getOfferPrice(), 1);
                    Assert.assertEquals(0d, marketDepth.getOfferSize(), 1);
                }

        );
    }

    @Test
    public void testMarketDepthMultipleOrdersWithSamePriceAndOnlySellDirection() {
        OrderDepthStore orderDepthStore = new OrderDepthStore();
        generateNewOrdersAndAddToStore(orderDepthStore, 10, "SELL", false);
        orderDepthStore.getMarketDataOrderDepth("TCS").stream().forEach(
                marketDepth -> {
                    Assert.assertEquals(0d, marketDepth.getBidPrice(), 1);
                    Assert.assertEquals(0d, marketDepth.getBidSize(), 1);
                    Assert.assertEquals(100d, marketDepth.getOfferPrice(), 1);
                    Assert.assertEquals(10000d, marketDepth.getOfferSize(), 1);
                }

        );
    }

    @Test
    public void testMarketDepthMultipleOrdersWithSamePriceAndAmendPriceQuantity() {
        OrderDepthStore orderDepthStore = new OrderDepthStore();
        ArrayList<OrderBook> orderList =
                generateNewOrdersAndAddToStore(orderDepthStore, 3, "SELL", false);
        orderDepthStore.getMarketDataOrderDepth("TCS").stream().forEach(
                marketDepth -> {
                    Assert.assertEquals(3000, marketDepth.getOfferSize(), 1);
                }
        );

        orderList.forEach(orderBook ->
        {
            orderBook.setQuantity(2000);
            orderBook.setType("AMEND");
            orderDepthStore.processOrder(orderBook);
        });

        orderDepthStore.getMarketDataOrderDepth("TCS").stream().forEach(
                marketDepth -> {
                    Assert.assertEquals(6000, marketDepth.getOfferSize(), 1);
                }
        );
    }

    @Test
    public void testMarketDepthMultipleOrdersWithSamePriceAndCancelQuantity() {
        OrderDepthStore orderDepthStore = new OrderDepthStore();
        ArrayList<OrderBook> orderList =
                generateNewOrdersAndAddToStore(orderDepthStore, 3, "SELL", false);
        orderDepthStore.getMarketDataOrderDepth("TCS").stream().forEach(
                marketDepth -> {
                    Assert.assertEquals(3000, marketDepth.getOfferSize(), 1);
                }
        );

        orderList.forEach(orderBook ->
        {
            orderBook.setType("CANCEL");
            orderDepthStore.processOrder(orderBook);
        });

        orderDepthStore.getMarketDataOrderDepth("TCS").stream().forEach(
                marketDepth -> {
                    Assert.assertEquals(0, marketDepth.getOfferSize(), 1);
                }
        );

        generateNewOrdersAndAddToStore(orderDepthStore, 3, "SELL", false);
        OrderBook orderBook1 = getNewOrder(1);
        orderBook1.setSide("SELL");
        orderBook1.setType("CANCEL");
        orderDepthStore.processOrder(orderBook1);

        orderDepthStore.getMarketDataOrderDepth("TCS").stream().forEach(
                marketDepth -> {
                    Assert.assertEquals(2000, marketDepth.getOfferSize(), 1);
                }
        );
    }

    @Test
    public void testMarketDepthMultipleOrdersAndCancelOneOrderQuantity() {
        OrderDepthStore orderDepthStore = new OrderDepthStore();

        generateNewOrdersAndAddToStore(orderDepthStore, 3, "SELL", false);
        OrderBook orderBook1 = getNewOrder(1);
        orderBook1.setSide("SELL");
        orderBook1.setType("CANCEL");
        orderDepthStore.processOrder(orderBook1);

        orderDepthStore.getMarketDataOrderDepth("TCS").stream().forEach(
                marketDepth -> {
                    Assert.assertEquals(2000, marketDepth.getOfferSize(), 1);
                }
        );
    }

    private ArrayList<OrderBook> generateNewOrdersAndAddToStore(OrderDepthStore orderDepthStore, int numberOfOrders, String side,
                                                boolean randomPrice) {
        Random random = new Random();
        ArrayList orderList = new ArrayList<OrderBook>();
        for (int i = 0; i < numberOfOrders; i++) {
            OrderBook orderBook = getNewOrder(i);
            orderBook.setSide(side);
            orderBook.setLimitPrice(randomPrice ? (
                    side.equalsIgnoreCase("BUY")?random.nextInt(10000): random.nextInt(2000))
                    : orderBook.getLimitPrice());
            orderDepthStore.processOrder(orderBook);
            orderList.add(orderBook);
        }
        return orderList;
    }

    private OrderBook getNewOrder(int orderNumber) {
        OrderBook order1 = new OrderBook();
        order1.setType("NEW");
        order1.setLimitPrice(100);
        order1.setOrder_id("ORDER "+orderNumber);
        order1.setQuantity(1000);
        order1.setSide("BUY");
        order1.setSymbol("TCS");
        return order1;
    }

    @Test
    public void testGetTopOfExchangeDepthForTwoMarketDepth() {
        OrderDepthStore orderDepthStore = new OrderDepthStore();
        generateTopOfExchangeDepthAndAddToStore(orderDepthStore);
        MarketDepth marketDepth = orderDepthStore.getTopOfMarketDepth("TCS");
        Assert.assertEquals(1200, marketDepth.getBidPrice(), 1);
        Assert.assertEquals(900, marketDepth.getOfferPrice(), 1);
        Assert.assertEquals(4000, marketDepth.getOfferSize(), 1);
        Assert.assertEquals(2000, marketDepth.getBidSize(), 1);
    }

    public void generateTopOfExchangeDepthAndAddToStore(OrderDepthStore exchangeDepthStore){

        MarketDepth marketDepth = new MarketDepth();
        marketDepth.setSymbol("TCS");
        marketDepth.setBidSize(1000);
        marketDepth.setBidPrice(1100);
        marketDepth.setOfferSize(2000);
        marketDepth.setOfferPrice(1000);
        exchangeDepthStore.processTopOfExchangeDepth(marketDepth);

        marketDepth = new MarketDepth();
        marketDepth.setSymbol("TCS");
        marketDepth.setBidSize(1000);
        marketDepth.setBidPrice(1200);
        marketDepth.setOfferSize(2000);
        marketDepth.setOfferPrice(900);
        exchangeDepthStore.processTopOfExchangeDepth(marketDepth);

        marketDepth = new MarketDepth();
        marketDepth.setSymbol("TCS");
        marketDepth.setBidSize(1000);
        marketDepth.setBidPrice(1200);
        marketDepth.setOfferSize(2000);
        marketDepth.setOfferPrice(900);
        exchangeDepthStore.processTopOfExchangeDepth(marketDepth);
    }
}

