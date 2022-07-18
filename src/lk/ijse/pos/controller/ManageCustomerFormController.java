package lk.ijse.pos.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import lk.ijse.pos.bo.BOFactory;
import lk.ijse.pos.bo.custom.CustomerBO;
import lk.ijse.pos.bo.custom.impl.CustomerBOImpl;
import lk.ijse.pos.dto.CustomerDTO;
import lk.ijse.pos.util.ValidationUtil;
import lk.ijse.pos.view.tdm.CustomerTM;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Optional;
import java.util.regex.Pattern;

public class ManageCustomerFormController {
    public JFXTextField txtCustomerId;
    public JFXTextField txtCusName;
    public JFXButton btnUpdate;
    public JFXButton btnRemove;
    public TableView<CustomerTM> tblCustomer;
    public TableColumn colCusId;
    public TableColumn colCusTitle;
    public TableColumn colCusName;
    public TableColumn colCusAddress;
    public TableColumn colCusCity;
    public TableColumn colCusProvince;
    public TableColumn colCusPostalCode;
    public AnchorPane context;
    public JFXTextField txtCusAddress;
    public JFXTextField txtCusCity;
    public JFXTextField txtCusProvince;
    public JFXTextField txtPostalCode;
    public JFXTextField txtMobileNumber;
    public JFXButton btnAddNewCustomer;
    public JFXButton btnAdd;
    public ComboBox<String> cmbCustomerTitle;
    public TableColumn colCusMobileNumber;
    public TextField txtSearchCustomer;
    public JFXButton btnSearch;
    LinkedHashMap<TextField, Pattern> map = new LinkedHashMap<>();
    LinkedHashMap<TextField, Pattern> hashMap = new LinkedHashMap<>();

    //Property Injection (DI)
    private final CustomerBO customerBO = (CustomerBO) BOFactory.getBoFactory().getBO(BOFactory.BOTypes.CUSTOMER);

    public void initialize() {
        btnSearch.setDisable(true);
        colCusId.setCellValueFactory(new PropertyValueFactory<>("custID"));
        colCusTitle.setCellValueFactory(new PropertyValueFactory<>("custTitle"));
        colCusName.setCellValueFactory(new PropertyValueFactory<>("custName"));
        colCusAddress.setCellValueFactory(new PropertyValueFactory<>("custAddress"));
        colCusCity.setCellValueFactory(new PropertyValueFactory<>("city"));
        colCusProvince.setCellValueFactory(new PropertyValueFactory<>("province"));
        colCusPostalCode.setCellValueFactory(new PropertyValueFactory<>("postalCode"));
        colCusMobileNumber.setCellValueFactory(new PropertyValueFactory<>("mobileNumber"));
        cmbCustomerTitle.getItems().addAll("Mrs.", "Mr.", "Ms.", "Dr.");
        btnAdd.setDisable(true);
        btnUpdate.setDisable(true);
        btnRemove.setDisable(true);

        txtCustomerId.setText(generateNewCustomerId());

        storeValidations();
        loadAllCustomers();

        tblCustomer.getSelectionModel().selectedItemProperty().addListener( (observable, oldValue, c1) -> {
            if (c1 != null) {
                if (!tblCustomer.getItems().isEmpty()) {
                    btnUpdate.setDisable(false);
                    btnRemove.setDisable(false);
                    setFontColor(txtCustomerId, txtCusName, txtMobileNumber, txtCusAddress, txtCusCity, txtCusProvince, txtPostalCode);
                    setCustomerData(new CustomerDTO(c1.getCustID(), c1.getCustTitle(), c1.getCustName(), c1.getCustAddress(), c1.getCity(), c1.getProvince(), c1.getPostalCode(), c1.getMobileNumber()));
                }
            }
        });
    }

    public void searchCustomerKeyReleasedOnAction(KeyEvent keyEvent) {
        if (txtSearchCustomer.getText().matches("^[C][0-9]{3,5}$")) {
            txtSearchCustomer.setStyle("-fx-text-fill: BLACK");
            btnSearch.setDisable(false);
        }else{
            if(txtSearchCustomer.getText().length()>0){
                txtSearchCustomer.setStyle("-fx-text-fill: RED");
                btnSearch.setDisable(true);
            }else{
                txtSearchCustomer.setStyle("-fx-text-fill: BLACK");
                btnSearch.setDisable(true);
            }
        }
    }

    public void setFontColor(TextField... textFields) {
        for (TextField textField : textFields) {
            textField.setStyle("-fx-text-fill: BLACK");
        }
    }

    private String generateNewCustomerId() {
        try {
            return customerBO.generateNewCustomerId();

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void loadAllCustomers() {
        tblCustomer.getItems().clear();
        try {
            ArrayList<CustomerDTO> arrayList = customerBO.getAllCustomers();

            for (CustomerDTO dto : arrayList) {
                tblCustomer.getItems().add(new CustomerTM(dto.getCustID(), dto.getCustTitle(), dto.getCustName(), dto.getCustAddress(), dto.getCity(), dto.getProvince(), dto.getPostalCode(), dto.getMobileNumber()));
            }

        } catch (SQLException | ClassNotFoundException e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
        }
    }

    private void storeValidations() {
        map.put(txtCustomerId, Pattern.compile("^[C][0-9]{3,5}$"));
        map.put(txtCusName, Pattern.compile("^[A-z ]{3,25}$"));
        map.put(txtMobileNumber, Pattern.compile("^(0){1}[0-9]{9}$"));
        map.put(txtCusAddress, Pattern.compile("^[A-z0-9 ,/]{4,45}$"));
        map.put(txtCusCity, Pattern.compile("^[A-z0-9 ]{3,20}$"));
        map.put(txtCusProvince, Pattern.compile("^[A-z ]{3,25}$"));
        map.put(txtPostalCode, Pattern.compile("^[0-9]{5}$"));
        hashMap = map;
    }

    private void setCustomerData(CustomerDTO dto) {
        txtCustomerId.setText(dto.getCustID());
        cmbCustomerTitle.setValue(dto.getCustTitle());
        txtCusName.setText(dto.getCustName());
        txtCusAddress.setText(dto.getCustAddress());
        txtCusCity.setText(dto.getCity());
        txtCusProvince.setText(dto.getProvince());
        txtPostalCode.setText(dto.getPostalCode());
        txtMobileNumber.setText(dto.getMobileNumber());
    }

    private CustomerDTO searchCustomer(String customerId) throws SQLException, ClassNotFoundException {
        return customerBO.searchCustomer(customerId);
    }

    public void txtSearchCustomerOnAction(ActionEvent event) {
        searchCustomer();
    }

    public void SearchCustomerOnAction(ActionEvent event) {
        searchCustomer();
    }

    private void searchCustomer() {
        try {
            if (!txtSearchCustomer.getText().isEmpty()) {
                String customerId = txtSearchCustomer.getText();
                if (customerId.matches("^[C][0-9]{3,5}$")) {

                    CustomerDTO customerDTO = searchCustomer(customerId);

                    if (customerDTO != null) {
                        setCustomerData(customerDTO);
                        btnUpdate.setDisable(false);
                        btnRemove.setDisable(false);
                    } else {
                        Optional<ButtonType> buttonType = new Alert(Alert.AlertType.ERROR, "No Customer exists for this id..! Do you want to add a customer", ButtonType.YES, ButtonType.NO).showAndWait();
                        clearAll();
                        txtSearchCustomer.clear();
                        if(buttonType.get().equals(ButtonType.YES)){
                            txtCusName.requestFocus();
                        }else{

                        }
                    }
                } else {
                    clearAll();
                    txtSearchCustomer.clear();
                    new Alert(Alert.AlertType.WARNING, "Invalid Customer ID..!").show();
                }
            } else {
                clearAll();
                txtSearchCustomer.requestFocus();
                new Alert(Alert.AlertType.WARNING, "Empty field, Please enter data to field & try again..!").show();
            }

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        btnAdd.setDisable(true);
    }

    private void clearAll() {
        txtCustomerId.setText(generateNewCustomerId());

        cmbCustomerTitle.getSelectionModel().clearSelection();
        txtCusName.clear();
        txtCusAddress.clear();
        txtCusCity.clear();
        txtCusProvince.clear();
        txtPostalCode.clear();
        txtMobileNumber.clear();
        tblCustomer.getSelectionModel().clearSelection();
        //txtCusName.requestFocus();
        btnAdd.setDisable(true);
        btnUpdate.setDisable(true);
        //txtSearchCustomer.clear();
        btnRemove.setDisable(true);
        setBorders(txtCustomerId, txtCusName, txtCusAddress, txtCusCity, txtCusProvince, txtPostalCode, txtMobileNumber);
    }

    public void setBorders(TextField... textFields) {
        for (TextField textField : textFields) {
            textField.setStyle("-fx-border-color:  #F3F3F3"); //null
        }
    }

    private boolean existCustomer(String customerId) throws SQLException, ClassNotFoundException {
        return customerBO.customerIsExists(customerId);
    }

    public void addCustomerOnAction(ActionEvent event) {
        try {
            if (!txtCustomerId.getText().isEmpty() && cmbCustomerTitle.getValue() != null && !txtCusName.getText().isEmpty() && !txtCusAddress.getText().isEmpty() && !txtCusCity.getText().isEmpty() && !txtCusProvince.getText().isEmpty() && !txtPostalCode.getText().isEmpty() && !txtMobileNumber.getText().isEmpty()) {
                if (!txtCustomerId.getText().matches("^[C][0-9]{3,5}$")) {
                    new Alert(Alert.AlertType.ERROR, "Invalid customerId").show();
                    return;
                } else if (!txtCusName.getText().matches("^[A-z ]{3,25}$")) {
                    new Alert(Alert.AlertType.ERROR, "Invalid name").show();
                    return;
                } else if (!txtCusAddress.getText().matches("^[A-z0-9 ,/]{4,45}$")) {
                    new Alert(Alert.AlertType.ERROR, "Invalid address").show();
                    return;
                } else if (!txtCusCity.getText().matches("^[A-z0-9 ]{3,20}$")) {
                    new Alert(Alert.AlertType.ERROR, "Invalid city").show();
                    return;
                } else if (!txtCusProvince.getText().matches("^[A-z ]{3,25}$")) {
                    new Alert(Alert.AlertType.ERROR, "Invalid province").show();
                    return;
                } else if (!txtMobileNumber.getText().matches("^(0){1}[0-9]{9}$")) {
                    new Alert(Alert.AlertType.ERROR, "Invalid Mobile Number").show();
                    return;
                } else if (!txtPostalCode.getText().matches("^[0-9]{5}$")) {
                    new Alert(Alert.AlertType.ERROR, "Postal code must be at least 5 characters long").show();
                    return;
                } else if(cmbCustomerTitle.getValue() == null){
                    new Alert(Alert.AlertType.ERROR, "Select a customer's title").show();
                }

                if (existCustomer(txtCustomerId.getText())) {
                    new Alert(Alert.AlertType.WARNING, "Customer already exists", ButtonType.CLOSE).show();
                } else {
                    CustomerDTO dto = new CustomerDTO(
                            txtCustomerId.getText(),
                            cmbCustomerTitle.getValue(),
                            txtCusName.getText(),
                            txtCusAddress.getText(),
                            txtCusCity.getText(),
                            txtCusProvince.getText(),
                            txtPostalCode.getText(),
                            txtMobileNumber.getText()
                    );

                    boolean customerIsSaved = customerBO.saveCustomer(dto);

                    if (customerIsSaved) {
                        tblCustomer.getItems().add(new CustomerTM(dto.getCustID(), dto.getCustTitle(), dto.getCustName(), dto.getCustAddress(), dto.getCity(), dto.getProvince(), dto.getPostalCode(), dto.getMobileNumber()));
                        new Alert(Alert.AlertType.CONFIRMATION, "Saved Successfully", ButtonType.OK).show();
                    } else {
                        new Alert(Alert.AlertType.ERROR, "Try again..!").show();
                    }
                }

            } else {
                new Alert(Alert.AlertType.WARNING, "Empty fields,Please fill the fields & try again..!").show();
            }

        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Failed to save the customer, Try again..!").show();
        }
        clearAll();
        txtSearchCustomer.clear();
        txtCusName.requestFocus();
    }

    public void updateCustomerOnAction(ActionEvent event) {
        try {
            if (!tblCustomer.getItems().isEmpty()) {
                if (tblCustomer.getSelectionModel().getSelectedItem() != null || !txtSearchCustomer.getText().isEmpty()) {
                    if (!txtCustomerId.getText().isEmpty() && cmbCustomerTitle.getValue() != null && !txtCusName.getText().isEmpty() && !txtCusAddress.getText().isEmpty() && !txtCusCity.getText().isEmpty() && !txtCusProvince.getText().isEmpty() && !txtPostalCode.getText().isEmpty() && !txtMobileNumber.getText().isEmpty()) {
                        if (!txtCustomerId.getText().matches("^[C][0-9]{3,5}$")) {
                            new Alert(Alert.AlertType.ERROR, "Invalid customerId").show();
                            return;
                        } else if (!txtCusName.getText().matches("^[A-z ]{3,25}$")) {
                            new Alert(Alert.AlertType.ERROR, "Invalid name").show();
                            return;
                        } else if (!txtCusAddress.getText().matches("^[A-z0-9 ,/]{4,45}$")) {
                            new Alert(Alert.AlertType.ERROR, "Invalid address").show();
                            return;
                        } else if (!txtCusCity.getText().matches("^[A-z0-9 ]{3,20}$")) {
                            new Alert(Alert.AlertType.ERROR, "Invalid city").show();
                            return;
                        } else if (!txtCusProvince.getText().matches("^[A-z ]{3,25}$")) {
                            new Alert(Alert.AlertType.ERROR, "Invalid province").show();
                            return;
                        } else if (!txtMobileNumber.getText().matches("^(0){1}[0-9]{9}$")) {
                            new Alert(Alert.AlertType.ERROR, "Invalid Mobile Number").show();
                            return;
                        } else if (!txtPostalCode.getText().matches("^[0-9]{5}$")) {
                            new Alert(Alert.AlertType.ERROR, "Postal code must be at least 5 characters long").show();
                            return;
                        }

                        if (!existCustomer(txtCustomerId.getText())) {
                            new Alert(Alert.AlertType.WARNING, "Customer doesn't exist", ButtonType.CLOSE).show();
                        } else {
                            boolean customerIsUpdated = customerBO.updateCustomer(new CustomerDTO(txtCustomerId.getText(), cmbCustomerTitle.getValue(), txtCusName.getText(), txtCusAddress.getText(), txtCusCity.getText(), txtCusProvince.getText(), txtPostalCode.getText(), txtMobileNumber.getText()));

                            if (customerIsUpdated) {
                                new Alert(Alert.AlertType.CONFIRMATION, "Updated Successfully..!", ButtonType.OK).show();
                            } else {
                                new Alert(Alert.AlertType.ERROR, "Try again..!").show();
                            }
                        }

                    } else {
                        new Alert(Alert.AlertType.WARNING, "Empty fields,Please fill the fields & try again..!").show();
                    }
                } else {
                    new Alert(Alert.AlertType.ERROR, "Please select a customer for update or enter customerId for which customer want to update..!").show();
                }
            } else {
                new Alert(Alert.AlertType.ERROR, "Customer table is empty therefore,can't update data..!").show();
            }

        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Failed to update customer, Try again..!").show();
        }
        clearAll();
        txtSearchCustomer.clear();
        loadAllCustomers();
        tblCustomer.refresh();
    }

    public void removeCustomerOnAction(ActionEvent event) {
        try {
            if (!tblCustomer.getItems().isEmpty()) {
                if (tblCustomer.getSelectionModel().getSelectedItem() != null || !txtSearchCustomer.getText().isEmpty()) {
                    CustomerTM tm = tblCustomer.getSelectionModel().getSelectedItem();
                    String customerId = txtSearchCustomer.getText();

                    boolean customerIsDeleted = customerBO.deleteCustomer(tm != null ? tm.getCustID() : customerId);

                    if (customerIsDeleted) {
                        new Alert(Alert.AlertType.CONFIRMATION, "Deleted Successfully", ButtonType.OK).show();
                    } else {
                        new Alert(Alert.AlertType.ERROR, "Try again..!").show();
                    }
                } else {
                    new Alert(Alert.AlertType.ERROR, "Please select a customer for delete or enter CustomerId which customer want to delete..!").show();
                }
            } else {
                new Alert(Alert.AlertType.ERROR, "Customer Table is empty therefore,can't delete data..!").show();
            }
        } catch (SQLException | ClassNotFoundException e) {
            new Alert(Alert.AlertType.ERROR, "Failed to delete Customer, Try again..!").show();
        }
        clearAll();
        txtSearchCustomer.clear();
        loadAllCustomers();
        tblCustomer.refresh();
    }

    public void resetOnAction(ActionEvent event) {
        txtSearchCustomer.clear();
        clearAll();
        txtCusName.requestFocus();
    }

    public void btnAddNextCusOnAction(ActionEvent event) {
        clearAll();
        txtSearchCustomer.clear();
        txtCusName.requestFocus();
    }

    public void validateKeyReleasedOnAction(KeyEvent keyEvent) {
        ValidationUtil.validate(hashMap,btnUpdate,btnRemove);
        Object validate = ValidationUtil.validate(map, btnAdd);

        try {
            if (!txtCustomerId.getText().isEmpty()) {
                if (existCustomer(txtCustomerId.getText())) {
                    btnAdd.setDisable(true);
                }else{
                    btnUpdate.setDisable(true);
                    btnRemove.setDisable(true);
                }
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        if (cmbCustomerTitle.getValue() == null) {
            btnAdd.setDisable(true);
        }

        if (KeyCode.ENTER == keyEvent.getCode()) {
            if (validate instanceof TextField) {
                TextField textField = (TextField) validate;
                textField.requestFocus();
            } else {
                btnAdd.fire();
            }
        }
    }
}
