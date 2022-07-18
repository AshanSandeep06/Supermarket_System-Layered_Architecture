package lk.ijse.pos.dto;

import java.math.BigDecimal;

public class ItemDTO {
    private String itemCode;
    private String description;
    private String packSize;
    private BigDecimal unitPrice;
    private int qtyOnHand;
    private BigDecimal discount;

    public ItemDTO() {

    }

    public ItemDTO(String itemCode) {
        this.itemCode = itemCode;
    }

    public ItemDTO(String itemCode, String description, String packSize, BigDecimal unitPrice, int qtyOnHand, BigDecimal discount) {
        this.itemCode = itemCode;
        this.description = description;
        this.packSize = packSize;
        this.unitPrice = unitPrice;
        this.qtyOnHand = qtyOnHand;
        this.discount = discount;
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

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public int getQtyOnHand() {
        return qtyOnHand;
    }

    public void setQtyOnHand(int qtyOnHand) {
        this.qtyOnHand = qtyOnHand;
    }

    public BigDecimal getDiscount() {
        return discount;
    }

    public void setDiscount(BigDecimal discount) {
        this.discount = discount;
    }

    public String toString(){
        return itemCode;
    }

    public boolean equals(Object obj){
        return obj instanceof ItemDTO ? ((ItemDTO)obj).getItemCode() == this.getItemCode() : false;
    }
}
