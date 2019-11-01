package com.mds.core.core;

public class MarketDepth {
    private double bidSize;
    private double bidPrice;
    private double offerSize;
    private double offerPrice;

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
}
