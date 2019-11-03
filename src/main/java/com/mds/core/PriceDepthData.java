package com.mds.core;

public class PriceDepthData {
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
