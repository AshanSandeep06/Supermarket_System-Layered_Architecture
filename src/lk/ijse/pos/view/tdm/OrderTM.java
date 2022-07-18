package lk.ijse.pos.view.tdm;

import java.math.BigDecimal;
import java.time.LocalDate;

public class OrderTM {
    private String orderID;
    private LocalDate orderDate;
    private String orderTime;
    private String custID;
    private BigDecimal discount;
    private BigDecimal totalCost;

    public OrderTM() {

    }

    public OrderTM(String orderID, LocalDate orderDate, String orderTime, String custID, BigDecimal discount, BigDecimal totalCost) {
        this.orderID = orderID;
        this.orderDate = orderDate;
        this.orderTime = orderTime;
        this.custID = custID;
        this.discount = discount;
        this.totalCost = totalCost;
    }

    public String getOrderID() {
        return orderID;
    }

    public void setOrderID(String orderID) {
        this.orderID = orderID;
    }

    public LocalDate getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDate orderDate) {
        this.orderDate = orderDate;
    }

    public String getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(String orderTime) {
        this.orderTime = orderTime;
    }

    public String getCustID() {
        return custID;
    }

    public void setCustID(String custID) {
        this.custID = custID;
    }

    public BigDecimal getDiscount() {
        return discount;
    }

    public void setDiscount(BigDecimal discount) {
        this.discount = discount;
    }

    public BigDecimal getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(BigDecimal totalCost) {
        this.totalCost = totalCost;
    }
}
