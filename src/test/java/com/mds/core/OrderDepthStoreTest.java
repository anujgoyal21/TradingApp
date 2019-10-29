package com.mds.core;

import com.mds.core.core.OrderBook;
import com.mds.core.core.OrderDepthStore;
import org.junit.Test;

public class OrderDepthStoreTest {

    @Test
    public void testOrphanDepthStore(){
        OrderDepthStore store = new OrderDepthStore();
        OrderBook order1 = getNewOrder();
        store.processOrders(order1);
        OrderBook order2 = getNewOrder();
        order2.setLimitPrice(200);
        store.processOrders(order2);
        order2.setLimitPrice(150);
        store.processOrders(order2);
        order2.setSymbol("INFY");
        store.processOrders(order2);
        store.getResult();
    }

    private OrderBook getNewOrder() {
        OrderBook order1 =  new OrderBook();
        order1.setQuantity(1000);
        order1.setType("NEW_ORDER");
        order1.setLimitPrice(100);
        order1.setOrder_id("ORDER1");
        order1.setSide("BUY");
        order1.setSymbol("TCS");
        return order1;
    }

}

