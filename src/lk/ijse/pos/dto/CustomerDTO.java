package lk.ijse.pos.dto;

public class CustomerDTO {
    private String custID;
    private String custTitle;
    private String custName;
    private String custAddress;
    private String city;
    private String province;
    private String postalCode;
    private String mobileNumber;

    public CustomerDTO() {

    }

    public CustomerDTO(String custID, String custTitle, String custName, String custAddress, String city, String province, String postalCode, String mobileNumber) {
        this.custID = custID;
        this.custTitle = custTitle;
        this.custName = custName;
        this.custAddress = custAddress;
        this.city = city;
        this.province = province;
        this.postalCode = postalCode;
        this.mobileNumber = mobileNumber;
    }

    public String getCustID() {
        return custID;
    }

    public void setCustID(String custID) {
        this.custID = custID;
    }

    public String getCustTitle() {
        return custTitle;
    }

    public void setCustTitle(String custTitle) {
        this.custTitle = custTitle;
    }

    public String getCustName() {
        return custName;
    }

    public void setCustName(String custName) {
        this.custName = custName;
    }

    public String getCustAddress() {
        return custAddress;
    }

    public void setCustAddress(String custAddress) {
        this.custAddress = custAddress;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String toString(){
        return custID;
    }
}
