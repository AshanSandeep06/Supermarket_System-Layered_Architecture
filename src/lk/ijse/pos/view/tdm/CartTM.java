package lk.ijse.pos.view.tdm;

import java.math.BigDecimal;

public class CartTM {
    private String itemCode;
    private String description;
    private String packSize;
    private int qty;
    private BigDecimal unitPrice;
    private BigDecimal discount;
    private BigDecimal total;

    public CartTM() {

    }

    public CartTM(String itemCode, String description, String packSize, int qty, BigDecimal unitPrice, BigDecimal discount, BigDecimal total) {
        this.itemCode = itemCode;
        this.description = description;
        this.packSize = packSize;
        this.qty = qty;
        this.unitPrice = unitPrice;
        this.discount = discount;
        this.total = total;
    }

    public String getItemCode() {
        return itemCode;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPackSize() {
        return packSize;
    }

    public void setPackSize(String packSize) {
        this.packSize = packSize;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public BigDecimal getDiscount() {
        return discount;
    }

    public void setDiscount(BigDecimal discount) {
        this.discount = discount;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }
}
