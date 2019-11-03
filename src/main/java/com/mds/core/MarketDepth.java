package com.mds.core;

import java.util.Comparator;
import java.util.Objects;

public class MarketDepth implements Comparator<MarketDepth>{

    private String symbol;
    private double bidSize;
    private double bidPrice;
    private double offerSize;
    private double offerPrice;

    public MarketDepth(){

    }

    public MarketDepth(String symbol, double bidSize, double bidPrice, double offerSize, double offerPrice){
        this.symbol = symbol;
        this.bidSize = bidSize;
        this.bidPrice =bidPrice;
        this.offerSize = offerSize;
        this.offerPrice = offerPrice;
    }

    public String getSymbol()
    {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public double getBidSize() {
        return bidSize;
    }

    public void setBidSize(double bidSize) {
        this.bidSize = bidSize;
    }

    public double getBidPrice() {
        return bidPrice;
    }

    public void setBidPrice(double bidPrice) {
        this.bidPrice = bidPrice;
    }

    public double getOfferSize() {
        return offerSize;
    }

    public void setOfferSize(double offerSize) {
        this.offerSize = offerSize;
    }

    public double getOfferPrice() {
        return offerPrice;
    }

    public void setOfferPrice(double offerPrice) {
        this.offerPrice = offerPrice;
    }

    @Override
    public int compare(MarketDepth o1, MarketDepth o2) {
        if (!o1.symbol.equalsIgnoreCase(o2.symbol)) {
            return -1;
        }

        if (o1.bidPrice > o2.bidPrice && o1.offerPrice < o2.offerPrice) {
            return -1;
        } else if (o1.bidPrice == o2.bidPrice && o1.offerPrice == o2.offerPrice) {
            return 0;
        } else {
            return 1;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MarketDepth that = (MarketDepth) o;
        return Double.compare(that.bidSize, bidSize) == 0 &&
                Double.compare(that.bidPrice, bidPrice) == 0 &&
                Double.compare(that.offerSize, offerSize) == 0 &&
                Double.compare(that.offerPrice, offerPrice) == 0 &&
                Objects.equals(symbol, that.symbol);
    }

    @Override
    public String toString() {
        return "MarketDepth{" +
                ", bidSize=" + bidSize +
                ", bidPrice=" + bidPrice +
                ", offerSize=" + offerSize +
                ", offerPrice=" + offerPrice +
                '}';
    }
}
