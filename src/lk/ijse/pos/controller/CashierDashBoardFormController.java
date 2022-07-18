package lk.ijse.pos.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import javafx.util.Duration;
import lk.ijse.pos.bo.BOFactory;
import lk.ijse.pos.bo.custom.CashierDashBoardBO;
import lk.ijse.pos.bo.custom.impl.CashierDashBoardBOImpl;
import lk.ijse.pos.dto.CustomerDTO;
import lk.ijse.pos.dto.ItemDTO;
import lk.ijse.pos.dto.OrderDTO;
import lk.ijse.pos.dto.OrderDetailDTO;
import lk.ijse.pos.util.NotificationUtil;
import lk.ijse.pos.view.tdm.CartTM;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanArrayDataSource;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.view.JasperViewer;
import org.controlsfx.control.Notifications;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CashierDashBoardFormController {
    public Text lblOrderId;
    public Text lblCashierName;
    public Text lblDate;
    public Text lblTime;
    public AnchorPane context;
    public ComboBox<CustomerDTO> cmbCustomerId;
    public JFXTextField txtCusName;
    public JFXTextField txtCusMobileNumber;
    public JFXTextField txtCusTitle;
    public JFXTextField txtCusAddress;
    public JFXTextField txtCusCity;
    public JFXTextField txtProvince;
    public JFXTextField txtPostalCode;
    public JFXTextField txtSearchMobileNumber;
    public JFXButton btnSearchCustomer;
    public ComboBox<ItemDTO> cmbItemCode;
    public JFXTextField txtDescription;
    public JFXTextField txtPackSize;
    public JFXTextField txtDiscount;
    public JFXTextField txtUnitPrice;
    public JFXTextField txtQtyOnHand;
    public JFXButton btnAddItem;
    public TextField txtQuantity;
    public TableColumn colItemCode;
    public TableColumn colDescription;
    public TableColumn colPackSize;
    public TableColumn colQuantity;
    public TableColumn colUnitPrice;
    public TableColumn colDiscount;
    public TableColumn colTotalCost;
    public Label lblTotalCost;
    public JFXButton btnRemoveItem;
    public JFXButton btnCancelOrder;
    public JFXButton btnPlaceOrder;
    public Text lblGrossAmount;
    public Text lblTotalDiscount;
    public Text lblNetAmount;
    public TableView<CartTM> tblCart;

    //Property Injection
    private CashierDashBoardBO cashierDashBoardBO = (CashierDashBoardBO) BOFactory.getBoFactory().getBO(BOFactory.BOTypes.CASHIERDASHBOARDBO);

    public void initialize() {
        colItemCode.setCellValueFactory(new PropertyValueFactory<>("itemCode"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        colPackSize.setCellValueFactory(new PropertyValueFactory<>("packSize"));
        colQuantity.setCellValueFactory(new PropertyValueFactory<>("qty"));
        colUnitPrice.setCellValueFactory(new PropertyValueFactory<>("unitPrice"));
        colDiscount.setCellValueFactory(new PropertyValueFactory<>("discount"));
        colTotalCost.setCellValueFactory(new PropertyValueFactory<>("total"));

        loadDateAndTime();
        lblOrderId.setText(generateNewOrderId());

        btnPlaceOrder.setDisable(true);
        btnAddItem.setDisable(true);
        btnSearchCustomer.setDisable(true);
        btnRemoveItem.setDisable(true);

        loadAllCustomerIds();
        loadAllItemCodes();

        cmbCustomerId.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                enableOrDisablePlaceOrderButton();
                try {
                    if (!existCustomer(newValue.getCustID())) {
                        new Alert(Alert.AlertType.ERROR, "There is no such customer associated with the id " + newValue.getCustID() + "").show();
                    } else {
                        setCustomerData(newValue);
                    }
                } catch (SQLException | ClassNotFoundException e) {
                    NotificationUtil.setNotifications("Failed, Didn't find ?", "Failed to find the customer..!", 3);
                }

            } else {
                clearCustomerFields();
            }
        });

        cmbItemCode.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                try {
                    if (!existItem(newValue.getItemCode())) {
                        new Alert(Alert.AlertType.ERROR, "There is no such item associated with this itemCode " + newValue.getItemCode() + "").show();
                    } else {
                        setItemData(newValue);

                        CartTM tm = isExists(newValue.getItemCode());
                        if(tm!=null){
                            txtQtyOnHand.setText(String.valueOf(newValue.getQtyOnHand() - tm.getQty()));
                        }else{
                            txtQtyOnHand.setText(String.valueOf(newValue.getQtyOnHand()));
                        }
                    }
                } catch (SQLException | ClassNotFoundException e) {
                    NotificationUtil.setNotifications("Failed, Didn't find ?", "Failed to find the item..!", 3);
                }

            } else {
                clearItemFields();
            }
        });

        tblCart.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, selectedItem) -> {
            try {
                if (selectedItem != null) {
                    cmbItemCode.setDisable(true);
                    btnAddItem.setDisable(false);
                    cmbItemCode.setValue(searchItem(selectedItem.getItemCode()));
                    btnAddItem.setText("      Update Item");
                    int qtyOnHand = Integer.parseInt(txtQtyOnHand.getText());
                    int selectedQty = selectedItem.getQty();
                    int totalQty = qtyOnHand + selectedQty;
                    txtQtyOnHand.setText(String.valueOf(totalQty));
                    txtQuantity.setText(String.valueOf(selectedQty));
                } else {
                    btnAddItem.setDisable(true);
                    btnAddItem.setText("      Add to cart");
                    cmbItemCode.setDisable(false);
                    cmbItemCode.getSelectionModel().clearSelection();
                    txtQuantity.clear();
                }

            } catch (Exception e) {

            }
        });
    }

    private void loadDateAndTime() {
        lblDate.setText(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));

        Timeline timeline = new Timeline(new KeyFrame(Duration.ZERO, (ev) -> {
            LocalTime currentTime = LocalTime.now();
            int hour = currentTime.getHour();
            String second = currentTime.getSecond()<10 ? "0"+currentTime.getSecond() : String.valueOf(currentTime.getSecond());
            if (hour >= 12) {
                lblTime.setText(currentTime.getHour() + ":" + currentTime.getMinute() + ":" + second + " PM");
            } else {
                lblTime.setText(currentTime.getHour() + ":" + currentTime.getMinute() + ":" + second + " AM");
            }
        }),
                new KeyFrame(Duration.seconds(1)));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

    private void setUI(String title, String location) throws IOException {
        Stage stage = new Stage();
        stage.setResizable(false);
        stage.initStyle(StageStyle.UTILITY);
        stage.setTitle(title);
        stage.initOwner(context.getScene().getWindow());
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("../view/" + location + ".fxml"))));
        stage.show();
    }

    private boolean existCustomer(String id) throws SQLException, ClassNotFoundException {
        return cashierDashBoardBO.customerIsExists(id);
    }

    private boolean existItem(String itemCode) throws SQLException, ClassNotFoundException {
        return cashierDashBoardBO.itemIsExists(itemCode);
    }

    private boolean existOrder(String orderId) throws SQLException, ClassNotFoundException {
        return cashierDashBoardBO.orderIsExists(orderId);
    }

    private void loadAllCustomerIds() {
        cmbCustomerId.getItems().clear();
        try {
            ArrayList<CustomerDTO> allCustomers = cashierDashBoardBO.getAllCustomers();
            for (CustomerDTO dto : allCustomers) {
                cmbCustomerId.getItems().add(dto);
            }

        } catch (SQLException | ClassNotFoundException e) {
            new Alert(Alert.AlertType.ERROR, "Failed to load customer ids").show();
        }
    }

    private ItemDTO searchItem(String itemCode) throws SQLException, ClassNotFoundException {
        return cashierDashBoardBO.searchItem(itemCode);
    }

    private void clearCustomerFields() {
        cmbCustomerId.setValue(null);
        txtCusName.clear();
        txtCusMobileNumber.clear();
        txtCusTitle.clear();
        txtPostalCode.clear();
        txtCusAddress.clear();
        txtCusCity.clear();
        txtProvince.clear();
        txtSearchMobileNumber.clear();
        btnSearchCustomer.setDisable(true);
        txtSearchMobileNumber.setStyle("-fx-text-fill: BLACK");
    }

    private void clearItemFields() {
        btnAddItem.setText("      Add to cart");
        //cmbItemCode.getSelectionModel().clearSelection();
        tblCart.getSelectionModel().clearSelection();
        cmbItemCode.setDisable(false);
        cmbItemCode.setValue(null);
        txtDescription.clear();
        txtPackSize.clear();
        txtUnitPrice.clear();
        txtQtyOnHand.clear();
        txtDiscount.clear();
        txtQuantity.clear();
        txtQuantity.setStyle("-fx-border-color: null");
        txtQuantity.setStyle("-fx-text-fill: BLACK");
        txtDiscount.setStyle("-fx-text-fill: BLACK");
        btnAddItem.setDisable(true);
    }

    private void loadAllItemCodes() {
        cmbItemCode.getItems().clear();
        try {
            ArrayList<ItemDTO> allItems = cashierDashBoardBO.getAllItems();

            for (ItemDTO dto : allItems) {
                cmbItemCode.getItems().add(dto);
            }

        } catch (SQLException | ClassNotFoundException e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
        }
    }

    public void logOutOnAction(ActionEvent event) throws IOException {
        Stage window = (Stage) context.getScene().getWindow();
        window.close();
        Stage stage = new Stage();
        stage.centerOnScreen();
        stage.setResizable(false);
        stage.setTitle("Super Market Login");
        stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("../view/LoginForm.fxml"))));
        stage.show();
    }

    public void manageOrdersOnAction(ActionEvent event) throws IOException {
        setUI("Manage Orders", "ManageOrdersForm");
    }

    public void manageCustomerFormOnAction(ActionEvent event) throws IOException {
        setUI("Manage Customers", "ManageCustomerForm");
    }

    public void openStockReportOnAction(ActionEvent event) throws IOException {
        setUI("Stock Balance Report", "StockReportForm");
    }

    public void mobileNumberValidateOnAction(KeyEvent keyEvent) {
        if (txtSearchMobileNumber.getText().matches("^(0){1}[0-9]{9}$")) {
            txtSearchMobileNumber.setStyle("-fx-text-fill: BLACK");
            btnSearchCustomer.setDisable(false);
        } else {
            if(txtSearchMobileNumber.getText().length()>0){
                txtSearchMobileNumber.setStyle("-fx-text-fill: RED");
                btnSearchCustomer.setDisable(true);
            }else{
                txtSearchMobileNumber.setStyle("-fx-text-fill: BLACK");
                btnSearchCustomer.setDisable(true);
            }
        }
    }

    public void txtSearchOnAction(ActionEvent event) {
        searchCustomerByMobileNumber();
    }

    public void mobileNumberSearchOnAction(ActionEvent event) {
        searchCustomerByMobileNumber();
    }

    private void searchCustomerByMobileNumber() {
        try {
            if (!txtSearchMobileNumber.getText().isEmpty()) {
                String mobileNumber = txtSearchMobileNumber.getText();
                if (mobileNumber.matches("^(0){1}[0-9]{9}$")) {

                    CustomerDTO customerDTO = cashierDashBoardBO.getCustomerByMobileNumber(mobileNumber);

                    if (customerDTO != null) {
                        setCustomerData(customerDTO);
                    } else {
                        NotificationUtil.setNotifications("Customer doesn't exist", "There is no customer associated with this Mobile number - " +mobileNumber, 3);
                    }
                } else {
                    NotificationUtil.setNotifications("Invalid Mobile Number", "Please enter a valid Mobile Number..!", 3);
                }
            } else {
                NotificationUtil.setNotifications("Empty field", "Please enter a Mobile Number..!", 3);
            }

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void setCustomerData(CustomerDTO dto) {
        cmbCustomerId.setValue(dto);
        txtCusName.setText(dto.getCustName());
        txtCusMobileNumber.setText(dto.getMobileNumber());
        txtCusTitle.setText(dto.getCustTitle());
        txtPostalCode.setText(dto.getPostalCode());
        txtCusAddress.setText(dto.getCustAddress());
        txtCusCity.setText(dto.getCity());
        txtProvince.setText(dto.getProvince());
    }

    private void setItemData(ItemDTO dto) {
        cmbItemCode.setValue(dto);
        txtDescription.setText(dto.getDescription());
        txtPackSize.setText(dto.getPackSize());
        txtUnitPrice.setText(dto.getUnitPrice().toString());
        txtQtyOnHand.setText(String.valueOf(dto.getQtyOnHand()));
        txtDiscount.setText(dto.getDiscount().toString());
    }

    public void clearOnAction(ActionEvent event) {
        clearItemFields();
        btnAddItem.setDisable(true);
    }

    public void checkOnAction(KeyEvent keyEvent) {
        try {
            if (txtQuantity.getText().matches("^[1-9][0-9]{0,4}$") && txtDiscount.getText().matches("^[0-9]{1,6}([.][0-9]{2})?$")) {
                txtQuantity.setStyle("-fx-border-color: GREEN");
                txtQuantity.setStyle("-fx-text-fill: BLACK");
                txtDiscount.setStyle("-fx-text-fill: BLACK");
                if (cmbItemCode.getSelectionModel().getSelectedItem() != null && !txtDescription.getText().isEmpty() && !txtPackSize.getText().isEmpty() && !txtUnitPrice.getText().isEmpty() && !txtQtyOnHand.getText().isEmpty() && !txtDiscount.getText().isEmpty()) {
                    btnAddItem.setDisable(false);
                    // btnAddItem.fire();
                }
            } else {
                btnAddItem.setDisable(true);
                if (txtQuantity.getText().length() > 0) {
                    if(txtQuantity.getText().matches("^[1-9][0-9]{0,4}$") ){
                        txtQuantity.setStyle("-fx-text-fill: BLACK");
                        txtQuantity.setStyle("-fx-border-color: #F3F3F3");
                    }else {
                        txtQuantity.setStyle("-fx-text-fill: RED");
                        txtQuantity.setStyle("-fx-border-color: RED");
                    }
                }else{
                    txtQuantity.setStyle("-fx-text-fill: BLACK");
                }
                if(txtDiscount.getText().length() > 0){
                    if(txtDiscount.getText().matches("^[0-9]{1,6}([.][0-9]{2})?$")){
                        txtDiscount.setStyle("-fx-text-fill: BLACK");
                    }else{
                        txtDiscount.setStyle("-fx-text-fill: RED");
                    }
                }else{
                    txtDiscount.setStyle("-fx-text-fill: BLACK");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String generateNewOrderId() {
        try {
            return cashierDashBoardBO.generateNewOrderId();

        } catch (SQLException | ClassNotFoundException e) {
            new Alert(Alert.AlertType.ERROR, "Failed to generate a new order Id").show();
        }
        return "OI-0001";
    }

    private CartTM isExists(String itemCode) {
        for (CartTM tm : tblCart.getItems()) {
            if (tm.getItemCode().equals(itemCode)) {
                return tm;
            }
        }
        return null;
    }

    public void addToCartOnAction(ActionEvent event) {
//        tblCart.getSelectionModel().clearSelection();
        if (!txtQuantity.getText().matches("^[1-9][0-9]{0,4}$") || Integer.parseInt(txtQuantity.getText()) <= 0 ||
                Integer.parseInt(txtQuantity.getText()) > Integer.parseInt(txtQtyOnHand.getText())) {
            new Alert(Alert.AlertType.ERROR, "Invalid qty").show();
            txtQuantity.requestFocus();
            txtQuantity.selectAll();
            return;
        }

        try {
            if (Integer.parseInt(txtQtyOnHand.getText()) > 0) {
                if (cmbItemCode.getSelectionModel().getSelectedItem() != null && !txtQuantity.getText().isEmpty()) {
                    if (txtQuantity.getText().matches("^[1-9][0-9]{0,4}$")) {
                        String itemCode = cmbItemCode.getValue().getItemCode();
                        String description = txtDescription.getText();
                        String packSize = txtPackSize.getText();
                        BigDecimal unitPrice = new BigDecimal(txtUnitPrice.getText()).setScale(2);
                        BigDecimal discount = new BigDecimal(txtDiscount.getText()).setScale(2);
                        int qtyOnHand = Integer.parseInt(txtQtyOnHand.getText());
                        int qty = Integer.parseInt(txtQuantity.getText());

                        BigDecimal totalDiscount = discount.multiply(new BigDecimal(qty)).setScale(2);
                        BigDecimal totalCost = unitPrice.multiply(new BigDecimal(qty)).setScale(2);

                        CartTM tmIsExist = isExists(cmbItemCode.getValue().getItemCode());

                        if (tmIsExist != null) {
                            if (btnAddItem.getText().equalsIgnoreCase("      Update Item")) {
                                tmIsExist.setQty(qty);
                                tmIsExist.setDiscount(totalDiscount);
                                tmIsExist.setTotal(totalCost);
                                tblCart.getSelectionModel().clearSelection();
                            } else {
                                tmIsExist.setQty(tmIsExist.getQty() + qty);
                                totalCost = new BigDecimal(tmIsExist.getQty()).multiply(unitPrice).setScale(2);
                                tmIsExist.setTotal(totalCost);
                                totalDiscount = new BigDecimal(tmIsExist.getQty()).multiply(discount).setScale(2);
                                tmIsExist.setDiscount(totalDiscount);
                            }
                        } else {
                            tblCart.getItems().add(new CartTM(itemCode, description, packSize, qty, unitPrice, totalDiscount, totalCost));
                        }
                    } else {
                        NotificationUtil.setNotifications("Invalid Quantity", "Please enter a valid quantity..!", 3);
                    }
                } else {
                    NotificationUtil.setNotifications("Failed to adding items..!", "Please select an item & input item quantity", 3);
                }
            } else {
                NotificationUtil.setNotifications("Out Of Stock..!", "This item has zero quantity, Therefore Choose another item..!", 3);
            }

        } catch (Exception e) {
            NotificationUtil.setNotifications("Error", "Item can't be added to the itemList..!", 3);
        }
        btnAddItem.setDisable(true);
        calculateTotalCost();
        tblCart.refresh();
        cmbItemCode.requestFocus();
        clearItemFields();
        enableOrDisablePlaceOrderButton();
    }

    private void enableOrDisablePlaceOrderButton() {
        btnPlaceOrder.setDisable(!(cmbCustomerId.getSelectionModel().getSelectedItem() != null && !tblCart.getItems().isEmpty()));
    }

    private void calculateTotalCost() {
        BigDecimal grossAmount = new BigDecimal(0).setScale(2);
        BigDecimal discount = new BigDecimal(0).setScale(2);
        BigDecimal netAmount;
        for (CartTM tm : tblCart.getItems()) {
            grossAmount = grossAmount.add(tm.getTotal());
        }
        for (CartTM tm : tblCart.getItems()) {
            discount = discount.add(tm.getDiscount());
        }
        double value = grossAmount.doubleValue() - discount.doubleValue();
        netAmount = new BigDecimal(value).setScale(2);
        lblGrossAmount.setText(grossAmount.toString());
        lblTotalDiscount.setText(discount.toString());
        lblNetAmount.setText(netAmount.toString());
        lblTotalCost.setText(grossAmount.toString());
    }

    public void tblOnAction(MouseEvent event) {
        btnRemoveItem.setDisable(false);
        if(tblCart.getItems().isEmpty()){
            btnRemoveItem.setDisable(true);
        }
    }

    public void removeItemOnAction(ActionEvent event) {
        if (!tblCart.getItems().isEmpty()) {
            CartTM tm = tblCart.getSelectionModel().getSelectedItem();
            if (tm != null) {
                for (CartTM cartTM : tblCart.getItems()) {
                    if (cartTM.getItemCode().equals(tm.getItemCode())) {
                        tblCart.getItems().remove(cartTM);
                        enableOrDisablePlaceOrderButton();
                        break;
                    }
                }
                tblCart.refresh();
                calculateTotalCost();
                btnRemoveItem.setDisable(true);

            } else {
                btnRemoveItem.setDisable(true);
                NotificationUtil.setNotifications("Warning..!", "Select Item from table..!", 3);
            }

        } else {
            btnRemoveItem.setDisable(true);
            NotificationUtil.setNotifications("Warning..!", "Table is empty therefore,can't delete data from table", 3);
        }
    }

    public void clearTableOnAction(ActionEvent event) {
        tblCart.getItems().clear();
        calculateTotalCost();
        enableOrDisablePlaceOrderButton();
    }

    public void decreaseQtyOnAction(ActionEvent event) {
        if(!tblCart.getItems().isEmpty()){
            if(!txtDiscount.getText().isEmpty()){
                if(txtDiscount.getText().matches("^[0-9]{1,6}([.][0-9]{2})?$")){
                    CartTM selectedItem = tblCart.getSelectionModel().getSelectedItem();
                    if (selectedItem != null) {
                        if (selectedItem.getQty() > 1) {
                            selectedItem.setQty(selectedItem.getQty() - 1);
//                selectedItem.setTotal(selectedItem.getQty() * selectedItem.getUnitPrice());
                            selectedItem.setDiscount(new BigDecimal(String.valueOf(selectedItem.getDiscount())).subtract(new BigDecimal(txtDiscount.getText())));
                            selectedItem.setTotal(new BigDecimal(selectedItem.getQty()).multiply(selectedItem.getUnitPrice()).setScale(2));
                            tblCart.refresh();
                            calculateTotalCost();
                        } else {
                            NotificationUtil.setNotifications("Warning..!", "Your selected Item's quantity is reached one,therefore,if you want to remove that item from the table, then use Remove item Option", 3);
                        }
                    } else {
                        NotificationUtil.setNotifications("Warning..!", "Select Item From The Table..!", 3);
                    }
                }else{
                    NotificationUtil.setNotifications("Error..!", "Please enter valid discount Price..!", 3);
                }
            }else{
                NotificationUtil.setNotifications("Warning..!", "Please enter discount price..!", 3);
            }
        }else{
            NotificationUtil.setNotifications("Warning..!", "Table is empty therefore,can't decrease qty", 3);
        }
    }

    public void increaseQtyOnAction(ActionEvent event) {
        if(!tblCart.getItems().isEmpty()){
            if(!txtDiscount.getText().isEmpty()){
                if(txtDiscount.getText().matches("^[0-9]{1,6}([.][0-9]{2})?$")){
                    CartTM selectedItem = tblCart.getSelectionModel().getSelectedItem();
                    if (selectedItem != null) {
                        selectedItem.setQty(selectedItem.getQty() + 1);
                        selectedItem.setDiscount(new BigDecimal(selectedItem.getDiscount().toString()).add(new BigDecimal(txtDiscount.getText())));
                        selectedItem.setTotal(new BigDecimal(selectedItem.getQty()).multiply(selectedItem.getUnitPrice()).setScale(2));
                        tblCart.refresh();
                        calculateTotalCost();
                    } else {
                        NotificationUtil.setNotifications("Warning..!", "Select Item From The Table..!", 3);
                    }
                }else{
                    NotificationUtil.setNotifications("Error..!", "Please enter valid discount Price..!", 3);
                }
            }else{
                NotificationUtil.setNotifications("Warning..!", "Please enter discount price..!", 3);
            }
        }else{
            NotificationUtil.setNotifications("Warning..!", "Table is empty therefore,can't increase qty", 3);
        }
    }

    public void cancelOrderOnAction(ActionEvent event) {
        clearCustomerFields();
        clearItemFields();
        btnAddItem.setDisable(true);
        btnPlaceOrder.setDisable(true);
        clearTableOnAction(null);
        btnSearchCustomer.setDisable(true);
        btnRemoveItem.setDisable(true);
        calculateTotalCost();
        cmbItemCode.setDisable(false);
        lblOrderId.setText(generateNewOrderId());
    }

    public void clearCustomerFieldsOnAction(ActionEvent event) {
        clearCustomerFields();
    }

    public void placeOrderOnAction(ActionEvent event) {
        try{
            if(!existOrder(lblOrderId.getText())){
                if(!tblCart.getItems().isEmpty() && cmbCustomerId.getValue()!=null && !txtCusName.getText().isEmpty() && !txtCusMobileNumber.getText().isEmpty() && !txtCusTitle.getText().isEmpty() && !txtPostalCode.getText().isEmpty() && !txtCusAddress.getText().isEmpty() && !txtCusCity.getText().isEmpty() && !txtProvince.getText().isEmpty()){
                    ArrayList<OrderDetailDTO> orderDetails = new ArrayList<>();
                    for (CartTM tm : tblCart.getItems()) {
                        orderDetails.add(new OrderDetailDTO(
                                lblOrderId.getText(),
                                tm.getItemCode(),
                                tm.getDescription(),
                                tm.getUnitPrice(),
                                tm.getQty(),
                                tm.getDiscount()
                        ));
                    }

                    OrderDTO orderDTO = new OrderDTO(lblOrderId.getText(), LocalDate.now(), lblTime.getText(), cmbCustomerId.getValue().getCustID(), new BigDecimal(lblTotalDiscount.getText()), new BigDecimal(lblNetAmount.getText()), orderDetails);
                    boolean orderIsSaved = saveOrder(orderDTO);
                    if(orderIsSaved){
                        String url ="lk/ijse/pos/assets/superm.jpg";
                        printBill(tblCart.getItems(),lblOrderId.getText(),cmbCustomerId.getValue().getCustID(),txtCusName.getText(),txtCusMobileNumber.getText(),new BigDecimal(lblGrossAmount.getText()),new BigDecimal(lblTotalDiscount.getText()),new BigDecimal(lblNetAmount.getText()),url);
                        NotificationUtil.setNotifications("Order Placement successful..!", "Order has been placed successfully..!", new ImageView(new Image("lk/ijse/pos/assets/Done.png")),3);
                        //stage.setFullScreen(false);
                        //Alert alert = new Alert(Alert.AlertType.INFORMATION, "Order has been placed successfully", ButtonType.OK);

                    }else{
                        NotificationUtil.setNotifications("Failed..!", "Order Placement failed, try again..!",3);
                    }

                }else{
                    NotificationUtil.setNotifications("Failed..!", "Add items to the cart, Fill the customer data correctly & try again..!",3);
                }
            }else{
                NotificationUtil.setNotifications("Warning..!", "This Order is already exists..!",3);
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        lblOrderId.setText(generateNewOrderId());
        cmbCustomerId.getSelectionModel().clearSelection();
        cmbItemCode.getSelectionModel().clearSelection();
        tblCart.getItems().clear();
        clearCustomerFields();
        clearItemFields();
        loadAllItemCodes();
        calculateTotalCost();
        enableOrDisablePlaceOrderButton();
    }

    private boolean saveOrder(OrderDTO orderDTO) throws SQLException, ClassNotFoundException{
        return cashierDashBoardBO.placeOrder(orderDTO);
    }

    public void printBill(ObservableList<CartTM> tmList,String orderId,String customerId,String customerName,String mobileNumber,BigDecimal grossAmount,BigDecimal totalDiscount,BigDecimal netAmount,String url){
        HashMap map = new HashMap();
        map.put("orderId",orderId);
        map.put("customerName",customerName);
        map.put("mobileNumber",mobileNumber);
        map.put("grossAmount",grossAmount);
        map.put("totalDiscount",totalDiscount);
        map.put("netAmount",netAmount);
        map.put("customerId",customerId);
        map.put("url",url);

        try {
            JasperReport compileReport = (JasperReport) JRLoader.loadObject(this.getClass().getResource("../view/reports/Payment Bill.jasper"));
            JasperPrint jasperPrint = JasperFillManager.fillReport(compileReport, map, new JRBeanArrayDataSource(tmList.toArray()));
            JasperViewer.viewReport(jasperPrint, false);

        } catch (JRException e) {
            e.printStackTrace();
        }
    }
}