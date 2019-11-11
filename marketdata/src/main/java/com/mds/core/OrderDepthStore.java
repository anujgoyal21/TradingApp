package com.mds.core;

import java.util.*;
import java.util.stream.Collectors;

public class OrderDepthStore {

    private static final OrderDepthStore orderDepthStore = new OrderDepthStore();

    public static OrderDepthStore getInstance(){
        return orderDepthStore;
    }

    /**
     * This map stores all symbols so as to provide efficient calls to the client
     * This stores bids and asks in the sorted way.
     */
    private HashMap<String, List<TreeMap<PriceDepthKey, PriceDepthData>>> symbolMap = new HashMap<>();

    /**
     * This map stores all symbols so as to provide efficient calls to the client
     * This stores for topDepthOfAllExchanges.
     */
    private HashMap<String, TreeMap<MarketDepth, MarketDepth>> topOfExchangeDepth = new HashMap<>();

    /**
     * This map stores reference of original order id and qty for handling amendment and cancellation
     * and adjusting the same.
     */
    private HashMap<String, OrderBook> orderBookMap = new HashMap<>();

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
            orderBookMap.putIfAbsent(orderBook.getOrder_id(), orderBook.clone());
        } else if ("AMEND".equalsIgnoreCase(orderBook.getType())) {
            priceDepthData.setQuantity(priceDepthData.getQuantity() - orderBookMap.get(orderBook.getOrder_id()).getQuantity() +
                    orderBook.getQuantity());
            orderBookMap.put(orderBook.getOrder_id(), orderBook.clone());
        } else {
            priceDepthData.setQuantity(priceDepthData.getQuantity() - orderBookMap.get(orderBook.getOrder_id()).getQuantity());
            orderBookMap.remove(orderBook.getOrder_id());
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

    /**
     * This method process new market depth from different exchanges and collate similar bid and ask together.
     *
     * @param marketDepth
     */
    public void processTopOfExchangeDepth(MarketDepth marketDepth) {
        TreeMap<MarketDepth, MarketDepth> treeMap = topOfExchangeDepth.get(marketDepth.getSymbol());

        if (treeMap == null) {
            Comparator<MarketDepth> depthKeyComparator = new MarketDepth("", 0, 0, 0, 0);
            treeMap = new TreeMap<>(depthKeyComparator);
        }

        MarketDepth existingDepth = treeMap.get(marketDepth);
        if (existingDepth == null) {
            treeMap.put(marketDepth, marketDepth);
        } else {
            marketDepth.setOfferSize(existingDepth.getOfferSize() + marketDepth.getOfferSize());
            marketDepth.setBidSize(existingDepth.getBidSize() + marketDepth.getBidSize());
            treeMap.put(marketDepth, marketDepth);
        }

        topOfExchangeDepth.put(marketDepth.getSymbol(), treeMap);
    }

    /**
     * Returns best bid and ask for a symbol from different exchanges.
     *
     * @param symbol
     * @return
     */
    public MarketDepth getTopOfMarketDepth(String symbol) {
        if (topOfExchangeDepth.get(symbol) != null) {
            MarketDepth marketDepth = topOfExchangeDepth.get(symbol).firstKey();
            return topOfExchangeDepth.get(symbol).get(marketDepth);
        }

        return new MarketDepth();
    }

    private PriceDepthKey getPriceDepthKey(String symbol, String side, double price) {
        return new PriceDepthKey(symbol, isBid(side), price);
    }

    public Collection<OrderBook> getOrderBooks() {
        return orderBookMap.values();
    }

    private boolean isBid(String side) {
        return "BUY".equalsIgnoreCase(side);
    }

}

class PriceDepthData {
    String symbol;
    double quantity;
    int numOfOrders;
    PriceDepthKey priceDepthKey;

    PriceDepthData(PriceDepthKey priceDepthKey){
        this.priceDepthKey = priceDepthKey;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return priceDepthKey.getPrice();
    }

    public int getNumOfOrders() {
        return numOfOrders;
    }

    public void setNumOfOrders(int numOfOrders) {
        this.numOfOrders = numOfOrders;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public PriceDepthKey getPriceDepthKey() {
        return priceDepthKey;
    }

    public void setPriceDepthKey(PriceDepthKey priceDepthKey) {
        this.priceDepthKey = priceDepthKey;
    }

    @Override
    public String toString() {
        return "PriceDepthData{" +
                "symbol='" + symbol + '\'' +
                ", quantity=" + quantity +
                ", numOfOrders=" + numOfOrders +
                ", priceDepthKey=" + priceDepthKey +
                '}';
    }

}

class PriceDepthKey implements Comparator<PriceDepthKey> {
    @Override
    public String toString() {
        return "PriceDepthKey{" +
                "isBid=" + isBid +
                ", price=" + price +
                ", symbol='" + symbol + '\'' +
                '}';
    }

    boolean isBid;
    double price;
    String symbol;

    PriceDepthKey(String symbol, boolean isBid, double price) {
        this.symbol = symbol;
        this.isBid = isBid;
        this.price = price;
    }

    public boolean isBid() {
        return isBid;
    }

    public void setBid(boolean bid) {
        isBid = bid;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    @Override
    public int compare(PriceDepthKey o1, PriceDepthKey o2) {

        if (!o1.symbol.equalsIgnoreCase(o2.symbol)) {
            return -1;
        }

        if (o1.isBid()) {
            if (o1.getPrice() < (o2.getPrice())) {
                return 1;
            } else if (o1.getPrice() == (o2.getPrice())) {
                return 0;
            } else {
                return -1;
            }
        } else {
            if (o1.getPrice() < (o2.getPrice())) {
                return -1;
            } else if (o1.getPrice() == (o2.getPrice())) {
                return 0;
            } else {
                return 1;
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PriceDepthKey that = (PriceDepthKey) o;
        return isBid == that.isBid &&
                Double.compare(that.price, price) == 0 &&
                symbol.equals(that.symbol);
    }
}
