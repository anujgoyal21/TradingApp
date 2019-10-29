package com.mds.core.core;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.Objects;

public class PriceDepthKey implements Comparator<PriceDepthKey> {
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

    /*@Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PriceDepthKey that = (PriceDepthKey) o;
        return isBid == that.isBid &&
                Double.compare(that.price, price) == 0 &&
                symbol.equals(that.symbol);
    }

    @Override
    public int hashCode() {
        return Objects.hash(isBid, price, symbol);
    }*/

}
