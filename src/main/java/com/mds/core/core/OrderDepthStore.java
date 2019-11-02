package com.mds.core.core;

import java.util.*;
import java.util.stream.Collectors;

public class OrderDepthStore {

    /**
     * This map stores all symbols so as to provide efficient calls to the client
     * This stores bids and asks in the sorted way.
     */
    private HashMap<String, List<TreeMap<PriceDepthKey, PriceDepthData>>> symbolMap = new HashMap<>();
    /**
     * This map stores reference of original order id and qty for handling amendment and cancellation
     * and adjusting the same.
     */
    private HashMap<String, Double> orderQtyMap = new HashMap<>();
    private static int BID_INDEX = 0;
    private static int ASK_INDEX = 1;
    private static int LEVEL_ALLOWED_5 = 5;

    /**
     * This method insert/amend/cancel order from the cache which internally sorts it into
     * 2 Maps containing bid and price data
     *
     * @param orderBook
     */
    public void processOrder(OrderBook orderBook) {

        List<TreeMap<PriceDepthKey, PriceDepthData>> symbolBidAskList = symbolMap.get(orderBook.getSymbol());

        if (symbolBidAskList == null) {
            symbolBidAskList = new ArrayList<>();
            Comparator<PriceDepthKey> PriceDepthKeyComparator = new PriceDepthKey("", true, (0));
            TreeMap<PriceDepthKey, PriceDepthData> bidPriceDepth = new TreeMap<PriceDepthKey, PriceDepthData>(PriceDepthKeyComparator);
            TreeMap<PriceDepthKey, PriceDepthData> askPriceDepth = new TreeMap<PriceDepthKey, PriceDepthData>(PriceDepthKeyComparator);
            symbolBidAskList.add(BID_INDEX, bidPriceDepth);
            symbolBidAskList.add(ASK_INDEX, askPriceDepth);
        }

        PriceDepthKey priceDepthKey = getPriceDepthKey(orderBook.getSymbol(), orderBook.getSide(),
                orderBook.getLimitPrice());

        PriceDepthData priceDepthData;

        int directionIndex = isBid(orderBook.getSide()) ? BID_INDEX : ASK_INDEX;
        priceDepthData = (symbolBidAskList.get(directionIndex)).get(priceDepthKey);

        if (priceDepthData == null) {
            priceDepthData = new PriceDepthData(priceDepthKey);

            priceDepthData.setSymbol(orderBook.getSymbol());
        }

        if ("NEW".equalsIgnoreCase(orderBook.getType())) {
            priceDepthData.setQuantity(priceDepthData.getQuantity() + orderBook.getQuantity());
            priceDepthData.setNumOfOrders(priceDepthData.numOfOrders + 1);
            orderQtyMap.put(orderBook.getOrder_id(), orderBook.getQuantity());
        } else if ("AMEND".equalsIgnoreCase(orderBook.getType())) {
            priceDepthData.setQuantity(priceDepthData.getQuantity() - orderQtyMap.get(orderBook.getOrder_id()) +
                    orderBook.getQuantity());
            orderQtyMap.put(orderBook.getOrder_id(), orderBook.getQuantity());
        } else {
            priceDepthData.setQuantity(priceDepthData.getQuantity() - orderQtyMap.get(orderBook.getOrder_id()));
            orderQtyMap.remove(orderBook.getOrder_id());
        }

        symbolBidAskList.get(directionIndex).put(priceDepthKey, priceDepthData);
        symbolMap.putIfAbsent(orderBook.getSymbol(), symbolBidAskList);

    }

    /**
     * This method is utility method to retrieve order depth by passing symbol
     * Only Depth up to 5 levels is supported which is kept configurable
     *
     * @param symbol
     * @return
     */
    public List<MarketDepth> getMarketDataOrderDepth(String symbol) {
        List<TreeMap<PriceDepthKey, PriceDepthData>> symbolTreeList = symbolMap.get(symbol);

        if (symbolTreeList != null && symbolTreeList.size() > 0) {
            TreeMap<PriceDepthKey, PriceDepthData> bidPriceDepth = symbolTreeList.get(BID_INDEX);
            TreeMap<PriceDepthKey, PriceDepthData> askPriceDepth = symbolTreeList.get(ASK_INDEX);

            int bidDepthSize = Math.min(bidPriceDepth.size(), LEVEL_ALLOWED_5);
            int askDepthSize = Math.min(askPriceDepth.size(), LEVEL_ALLOWED_5);

            Map<Integer, MarketDepth> marketDepthMap = new HashMap<>(Math.max(bidDepthSize, askDepthSize));

            Iterator<PriceDepthData> iterBidData = bidPriceDepth.values().iterator();
            int levelBid = 0;
            while (iterBidData.hasNext() && levelBid < bidDepthSize) {
                MarketDepth marketDepth = new MarketDepth();
                PriceDepthData priceDepthData = iterBidData.next();
                marketDepth.setBidPrice(priceDepthData.getPrice());
                marketDepth.setBidSize(priceDepthData.getQuantity());
                marketDepthMap.put(levelBid, marketDepth);
                levelBid++;
            }

            Iterator<PriceDepthData> iterAskData = askPriceDepth.values().iterator();
            int levelAsk = 0;
            while (iterAskData.hasNext() && levelAsk < askDepthSize) {
                MarketDepth marketDepth = marketDepthMap.get(levelAsk);
                if (marketDepth == null) {
                    marketDepth = new MarketDepth();
                }
                PriceDepthData priceDepthData = iterAskData.next();
                marketDepth.setOfferPrice(priceDepthData.getPrice());
                marketDepth.setOfferSize(priceDepthData.getQuantity());
                marketDepthMap.put(levelAsk, marketDepth);
                levelAsk++;
            }
            return marketDepthMap.values().stream().collect(Collectors.toList());
        }
        return Collections.EMPTY_LIST;
    }

    private PriceDepthKey getPriceDepthKey(String symbol, String side, double price) {
        return new PriceDepthKey(symbol, isBid(side), price);
    }

    private boolean isBid(String side) {
        return "BUY".equalsIgnoreCase(side);
    }

}

