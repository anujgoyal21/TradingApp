package com.mds.core.core;

import java.util.Objects;

public class TopLevelnstrument {

    private String symbol;
    private double bestBidPrice;
    private double bestBidQty;
    private double bestAskPrice;
    private double bestAskQty;


    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public double getBestBidPrice() {
        return bestBidPrice;
    }

    public void setBestBidPrice(double bestBidPrice) {
        this.bestBidPrice = bestBidPrice;
    }

    public double getBestBidQty() {
        return bestBidQty;
    }

    public void setBestBidQty(double bestBidQty) {
        this.bestBidQty = bestBidQty;
    }

    public double getBestAskPrice() {
        return bestAskPrice;
    }

    public void setBestAskPrice(double bestAskPrice) {
        this.bestAskPrice = bestAskPrice;
    }

    public double getBestAskQty() {
        return bestAskQty;
    }

    public void setBestAskQty(double bestAskQty) {
        this.bestAskQty = bestAskQty;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TopLevelnstrument that = (TopLevelnstrument) o;
        return Objects.equals(symbol, that.symbol);
    }

    @Override
    public int hashCode() {
        return Objects.hash(symbol);
    }
}

