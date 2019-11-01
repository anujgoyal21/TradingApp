package com.mds.core;

import com.mds.core.core.OrderBook;
import com.mds.core.core.OrderDepthStore;
import org.junit.Assert;
import org.junit.Test;

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
                    Assert.assertTrue( marketDepth.getBidPrice() > marketDepth.getOfferPrice());
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

    private void generateNewOrdersAndAddToStore(OrderDepthStore orderDepthStore, int numberOfOrders, String side,
                                                boolean randomPrice) {
        Random random = new Random();
        for (int i = 0; i < numberOfOrders; i++) {
            OrderBook orderBook = getNewOrder();
            orderBook.setSide(side);
            orderBook.setLimitPrice(randomPrice ? random.nextInt(10000) : orderBook.getLimitPrice());
            orderDepthStore.processOrder(orderBook);
        }
    }

    private OrderBook getNewOrder() {
        OrderBook order1 = new OrderBook();
        order1.setType("NEW");
        order1.setLimitPrice(100);
        order1.setOrder_id("ORDER1");
        order1.setQuantity(1000);
        order1.setSide("BUY");
        order1.setSymbol("TCS");
        return order1;
    }
}

