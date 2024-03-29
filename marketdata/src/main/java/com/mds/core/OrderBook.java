package com.mds.core;

public class OrderBook implements Cloneable
{
    private String type;
    private String order_id;
    private double quantity;
    private String symbol;
    private String side;
    private double limitPrice;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getSide() {
        return side;
    }

    public void setSide(String side) {
        this.side = side;
    }

    public double getLimitPrice() {
        return limitPrice;
    }

    public void setLimitPrice(double limitPrice) {
        this.limitPrice = limitPrice;
    }

    @Override
    public OrderBook clone(){
        try {
            return (OrderBook) super.clone();
        }catch (CloneNotSupportedException ex){
            throw new AssertionError();
        }

    }


}
