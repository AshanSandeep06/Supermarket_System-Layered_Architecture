package lk.ijse.pos.entity;

import java.math.BigDecimal;

public class OrderDetail {
    private String orderID;
    private String itemCode;
    private String description;
    private BigDecimal unitPrice;
    private int orderQty;
    private BigDecimal discount;

    public OrderDetail() {

    }

    public OrderDetail(String orderID, String itemCode, String description, BigDecimal unitPrice, int orderQty, BigDecimal discount) {
        this.orderID = orderID;
        this.itemCode = itemCode;
        this.description = description;
        this.unitPrice = unitPrice;
        this.orderQty = orderQty;
        this.discount = discount;
    }

    public String getOrderID() {
        return orderID;
    }

    public void setOrderID(String orderID) {
        this.orderID = orderID;
    }

    public String getItemCode() {
        return itemCode;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public int getOrderQty() {
        return orderQty;
    }

    public void setOrderQty(int orderQty) {
        this.orderQty = orderQty;
    }

    public BigDecimal getDiscount() {
        return discount;
    }

    public void setDiscount(BigDecimal discount) {
        this.discount = discount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
