package lk.ijse.pos.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.beans.InvalidationListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
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
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.Duration;
import lk.ijse.pos.bo.BOFactory;
import lk.ijse.pos.bo.custom.ManageOrdersBO;
import lk.ijse.pos.bo.custom.impl.ManageOrdersBOImpl;
import lk.ijse.pos.dto.*;
import lk.ijse.pos.util.NotificationUtil;
import lk.ijse.pos.view.tdm.CartTM;
import lk.ijse.pos.view.tdm.OrderDetailTM;
import lk.ijse.pos.view.tdm.OrderTM;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;

public class ManageOrdersFormController {

    public TextField txtEnterCusID;
    public TableView<OrderTM> tblOrderID;
    public TableColumn colOrderID;
    public JFXTextField txtPackSize;
    public JFXTextField txtDiscount;
    public JFXTextField txtUnitPrice;
    public JFXTextField txtQtyOnHand;
    public JFXButton btnAddItem;
    public TextField txtQuantity;
    public TableView<OrderDetailTM> tblOrderDetail;
    public TableColumn colItemCode;
    public TableColumn colDescription;
    public TableColumn colPackSize;
    public TableColumn colQuantity;
    public TableColumn colUnitPrice;
    public TableColumn colDiscount;
    public TableColumn colTotalCost;
    public Label lblGrossAmount;
    public Label lblTotalDiscount;
    public Label lblNetAmount;
    public TextField txtCash;
    public Label lblBalance;
    public JFXButton btnUpdateOrder;
    public JFXButton btnDeleteOrder;
    public JFXButton btnRemoveItem;
    public ComboBox<String> cmbItemCode;
    public JFXTextField txtDescription;
    public JFXButton btnSearchOrders;
    public Label lblTotalCost;
    public AnchorPane context;
    ArrayList<ItemDTO> itemList = new ArrayList<>();

    //Property Injection
    ManageOrdersBO manageOrdersBO = (ManageOrdersBO) BOFactory.getBoFactory().getBO(BOFactory.BOTypes.MANAGEORDERSBO);

    public void initialize() {
        /*tblOrderDetail.getItems().addListener((InvalidationListener) c -> {
            if(!tblOrderID.getItems().isEmpty() && !tblOrderDetail.getItems().isEmpty()){
                btnUpdateOrder.setDisable(false);
            }
        });*/

        colOrderID.setCellValueFactory(new PropertyValueFactory<>("orderID"));

        colItemCode.setCellValueFactory(new PropertyValueFactory<>("itemCode"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        colPackSize.setCellValueFactory(new PropertyValueFactory<>("packSize"));
        colQuantity.setCellValueFactory(new PropertyValueFactory<>("qty"));
        colUnitPrice.setCellValueFactory(new PropertyValueFactory<>("unitPrice"));
        colDiscount.setCellValueFactory(new PropertyValueFactory<>("discount"));
        colTotalCost.setCellValueFactory(new PropertyValueFactory<>("totalCost"));

        tblOrderID.setFixedCellSize(50.0);

        btnSearchOrders.setDisable(true);
        btnAddItem.setDisable(true);
        btnUpdateOrder.setDisable(true);
        btnRemoveItem.setDisable(true);
        loadAllItemCodes();

        cmbItemCode.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            try {
                if (newValue != null) {
                    ItemDTO itemDTO = manageOrdersBO.searchItem(newValue);
                    if (itemDTO != null) {
                        setItemData(itemDTO);
                    } else {
                        clearItemFields();
                    }
                } else {
                    clearItemFields();
                }

            } catch (SQLException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        });

        tblOrderDetail.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, selectedItem) -> {
            try {
                if (selectedItem != null) {
                    cmbItemCode.setDisable(true);
                    btnAddItem.setDisable(false);
                    cmbItemCode.setValue(selectedItem.getItemCode());  // I003
                    btnAddItem.setText("      Update Item");

                    ItemDTO itemDTO = manageOrdersBO.searchItem(selectedItem.getItemCode());
                    txtQtyOnHand.setText(String.valueOf(itemDTO.getQtyOnHand()));
                    txtQuantity.setText(String.valueOf(selectedItem.getQty()));
                } else {
                    btnAddItem.setDisable(true);
                    btnAddItem.setText("      Add to cart");
                    cmbItemCode.setDisable(false);
                    cmbItemCode.getSelectionModel().clearSelection();
                    //txtQuantity.clear();
                }

            } catch (Exception e) {

            }
        });

        tblOrderID.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            try {
                if (newValue != null) {
                    loadAllOrderDetailsToTheTable();
                    calculateTotalCost();
                    txtCash.clear();
                    lblBalance.setText("0.00");
                } else {
                    tblOrderDetail.getItems().clear();
                    calculateTotalCost();
                }

            } catch (Exception e) {

            }
        });
    }

    private void setItemData(ItemDTO dto) {
        cmbItemCode.setValue(dto.getItemCode());
        txtDescription.setText(dto.getDescription());
        txtPackSize.setText(dto.getPackSize());
        txtUnitPrice.setText(dto.getUnitPrice().toString());
        txtQtyOnHand.setText(String.valueOf(dto.getQtyOnHand()));
        txtDiscount.setText(dto.getDiscount().toString());
    }

    private void loadAllItemCodes() {
        cmbItemCode.getItems().clear();
        try {
            itemList = manageOrdersBO.getAllItems();
            for (ItemDTO dto : itemList) {
                cmbItemCode.getItems().add(dto.getItemCode());
            }

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void calculateTotalCost() {
        BigDecimal grossAmount = new BigDecimal(0).setScale(2);
        BigDecimal discount = new BigDecimal(0).setScale(2);
        BigDecimal netAmount;
        for (OrderDetailTM tm : tblOrderDetail.getItems()) {
            grossAmount = grossAmount.add(tm.getTotalCost());
        }
        for (OrderDetailTM tm : tblOrderDetail.getItems()) {
            discount = discount.add(tm.getDiscount());
        }
        double value = grossAmount.doubleValue() - discount.doubleValue();
        netAmount = new BigDecimal(value).setScale(2);
        lblGrossAmount.setText(grossAmount.toString());
        lblTotalDiscount.setText(discount.toString());
        lblNetAmount.setText(netAmount.toString());
        lblTotalCost.setText(grossAmount.toString());
    }

    public void checkCusIdKeyReleasedOnAction(KeyEvent keyEvent) {
        btnUpdateOrder.setDisable(true);
        try {
            if (!txtEnterCusID.getText().isEmpty()) {
                if (txtEnterCusID.getText().matches("^[C][0-9]{3,5}$")) {
                    txtEnterCusID.setStyle("-fx-text-fill : BLACK");
                    btnSearchOrders.setDisable(false);

                    if (!tblOrderID.getItems().isEmpty()) {
                        OrderTM tm = tblOrderID.getItems().get(0);
                        if (tm != null) {
                            OrderDTO dto = manageOrdersBO.searchOrder(tm.getOrderID());
                            if (dto != null) {
                                if (!dto.getCustID().equals(txtEnterCusID.getText())) {
                                    tblOrderID.getItems().clear();
                                    tblOrderDetail.getItems().clear();
                                }
                            }
                        }
                    }

                } else {
                    tblOrderID.getItems().clear();
                    tblOrderDetail.getItems().clear();
                    txtEnterCusID.setStyle("-fx-text-fill : RED");
                    btnSearchOrders.setDisable(true);
                }
            } else {
                tblOrderID.getItems().clear();
                tblOrderDetail.getItems().clear();
                txtEnterCusID.setStyle("-fx-text-fill : BLACK");
                btnSearchOrders.setDisable(true);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void searchOrdersOnAction(ActionEvent event) {
        searchAllOrdersByCustomerId();
    }

    public void txtSearchOrdersOnAction(ActionEvent event) {
        searchAllOrdersByCustomerId();
    }

    private void clearOrdersListTable() {
        tblOrderID.getItems().clear();
    }

    private boolean existCustomer(String id) throws SQLException, ClassNotFoundException {
        return manageOrdersBO.customerIsExists(id);
    }

    private void searchAllOrdersByCustomerId() {
        try {
            if (!txtEnterCusID.getText().isEmpty()) {
                String customerId = txtEnterCusID.getText();
                if (customerId.matches("^[C][0-9]{3,5}$")) {

                    if (existCustomer(customerId)) {
                        ArrayList<OrderDTO> allOrders = manageOrdersBO.getOrdersByCustomerId(customerId);

                        if (allOrders.size() > 0) {
                            clearOrdersListTable();
                            //NotificationUtil.setNotifications("List of Orders successfully loaded", "Orders list has been loaded successfully..!", new ImageView(new Image("lk/ijse/pos/assets/icons8-ok-100.png")), 2);
                            for (OrderDTO orderDTO : allOrders) {
                                tblOrderID.getItems().add(new OrderTM(
                                        orderDTO.getOrderID(),
                                        orderDTO.getOrderDate(),
                                        orderDTO.getOrderTime(),
                                        orderDTO.getCustID(),
                                        orderDTO.getDiscount(),
                                        orderDTO.getTotalCost()
                                ));
                            }
                        } else {
                            clearOrdersListTable();
                            NotificationUtil.setNotifications("List of Orders loading unsuccessful", "This Customer hasn't been placed any Orders yet..!", new ImageView(new Image("lk/ijse/pos/assets/icons8-help-100.png")), 3);
                        }
                    } else {
                        clearOrdersListTable();
                        NotificationUtil.setNotifications("Unknown Customer", "No Customers exist for this Customer Id..!", new ImageView(new Image("lk/ijse/pos/assets/unknown.png")), 3);
                    }

                } else {
                    clearOrdersListTable();
                    NotificationUtil.setNotifications("WARNING", "Invalid Customer ID, Please enter real one..!", new ImageView(new Image("lk/ijse/pos/assets/invalid.png")), 3);
                }
            } else {
                clearOrdersListTable();
                NotificationUtil.setNotifications("Empty Field", "Please input a Customer ID & try again..!", new ImageView(new Image("lk/ijse/pos/assets/empty.png")), 3);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void clearOnAction(ActionEvent event) {
        clearItemFields();
        btnAddItem.setDisable(true);
    }

    public void addToCartOnAction(ActionEvent event) {
        if (!tblOrderDetail.getItems().isEmpty()) {
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
                            String itemCode = cmbItemCode.getValue();
                            String description = txtDescription.getText();
                            String packSize = txtPackSize.getText();
                            BigDecimal unitPrice = new BigDecimal(txtUnitPrice.getText()).setScale(2);
                            BigDecimal discount = new BigDecimal(txtDiscount.getText()).setScale(2);
                            int qtyOnHand = Integer.parseInt(txtQtyOnHand.getText());
                            int qty = Integer.parseInt(txtQuantity.getText());

                            BigDecimal totalDiscount = discount.multiply(new BigDecimal(qty)).setScale(2);
                            BigDecimal totalCost = unitPrice.multiply(new BigDecimal(qty)).setScale(2);

                            OrderDetailTM tmIsExist = isExists(cmbItemCode.getValue());

                            if (tmIsExist != null) {
                                if (btnAddItem.getText().equalsIgnoreCase("      Update Item")) {
                                    tmIsExist.setQty(qty);
                                    tmIsExist.setDiscount(totalDiscount);
                                    tmIsExist.setTotalCost(totalCost);
                                    tblOrderDetail.getSelectionModel().clearSelection();
                                } else {
                                    tmIsExist.setQty(tmIsExist.getQty() + qty);
                                    totalCost = new BigDecimal(tmIsExist.getQty()).multiply(unitPrice).setScale(2);
                                    tmIsExist.setTotalCost(totalCost);
                                    totalDiscount = new BigDecimal(tmIsExist.getQty()).multiply(discount).setScale(2);
                                    tmIsExist.setDiscount(totalDiscount);
                                }
                            } else {
                                tblOrderDetail.getItems().add(new OrderDetailTM(itemCode, description, packSize, qty, unitPrice, totalDiscount, totalCost));
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
            tblOrderDetail.refresh();
            cmbItemCode.requestFocus();
            clearItemFields();
            enableOrDisableUpdateOrderButton();
        } else {
            NotificationUtil.setNotifications("Error", "Items can't be added to the itemList, because Order List is empty..!", 3);
        }
    }

    private OrderDetailTM isExists(String itemCode) {
        for (OrderDetailTM tm : tblOrderDetail.getItems()) {
            if (tm.getItemCode().equals(itemCode)) {
                return tm;
            }
        }
        return null;
    }

    private boolean existOrder(String orderId) throws SQLException, ClassNotFoundException {
        return manageOrdersBO.orderIsExists(orderId);
    }

    public void updateOrderOnAction(ActionEvent event) {
        try {
            if (!txtEnterCusID.getText().isEmpty()) {
                if (txtEnterCusID.getText().matches("^[C][0-9]{3,5}$")) {
                    if (!tblOrderID.getItems().isEmpty() && !tblOrderDetail.getItems().isEmpty()) {
                        OrderTM orderTM = tblOrderID.getSelectionModel().getSelectedItem();
                        if (orderTM != null) {
                            if (existOrder(orderTM.getOrderID())) {

                                ArrayList<OrderDetailDTO> orderDetails = new ArrayList<>();
                                for (OrderDetailTM tm : tblOrderDetail.getItems()) {
                                    orderDetails.add(new OrderDetailDTO(
                                            orderTM.getOrderID(),
                                            tm.getItemCode(),
                                            tm.getDescription(),
                                            tm.getUnitPrice(),
                                            tm.getQty(),
                                            tm.getDiscount()
                                    ));
                                }

                                OrderDTO orderDTO = new OrderDTO(orderTM.getOrderID(), null, null, orderTM.getCustID(), new BigDecimal(lblTotalDiscount.getText()), new BigDecimal(lblNetAmount.getText()), orderDetails);
                                boolean orderIsUpdated = manageOrdersBO.updateOrder(orderDTO);

                                if (orderIsUpdated) {
                                    Notifications notificationBuilder = Notifications.create()
                                            .title("Warning")
                                            .text("Order has been Updated successfully..!")
                                            .graphic(new ImageView(new Image("lk/ijse/pos/assets/Done.png")))
                                            .hideAfter(Duration.seconds(4))
                                            .position(Pos.CENTER);
                                    notificationBuilder.darkStyle();
                                    notificationBuilder.showWarning();
                                    //NotificationUtil.setNotifications("Warning", "Order has been Updated successfully..!", new ImageView(new Image("lk/ijse/pos/assets/Done.png")), 4);
                                } else {
                                    NotificationUtil.setNotifications("Error", "Try again,Something went wrong..!", 3);
                                }
                            } else {
                                NotificationUtil.setNotifications("Unknown Order", "This Order doesn't exist, therefore, can't update this Order..!", new ImageView(new Image("lk/ijse/pos/assets/unknown.png")), 3);
                            }
                        } else {
                            NotificationUtil.setNotifications("Warning", "Please select an OrderId..!", 3);
                        }
                    } else {
                        NotificationUtil.setNotifications("Warning", "Tables are empty..!", 3);
                    }
                } else {
                    NotificationUtil.setNotifications("Invalid Data Fields", "Please input valid customer Id..!", 3);
                }
            } else {
                NotificationUtil.setNotifications("Empty Data Fields", "Empty fields & try again later..!", 3);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        btnUpdateOrder.setDisable(true);
    }

    private CustomerDTO searchCustomer(String customerId) throws SQLException, ClassNotFoundException {
        return manageOrdersBO.searchCustomer(customerId);
    }

    public void cashPaymentOnAction(ActionEvent event) {
        try {
            if (!txtEnterCusID.getText().isEmpty()) {
                if (txtEnterCusID.getText().matches("^[C][0-9]{3,5}$")) {
                    if (!tblOrderID.getItems().isEmpty() && !tblOrderDetail.getItems().isEmpty()) {
                        if (!txtCash.getText().isEmpty()) {
                            if (txtCash.getText().matches("^[1-9][0-9]{0,7}([.][0-9]{2})?$")) {
                                if ((Double.parseDouble(lblNetAmount.getText()) <= Double.parseDouble(txtCash.getText()))) {
                                    double netAmount = Double.parseDouble(lblNetAmount.getText());
                                    double paidAmount = Double.parseDouble(txtCash.getText());
                                    double balance = paidAmount - netAmount;
                                    lblBalance.setText(String.valueOf(balance));
                                    String url = "lk/ijse/pos/assets/superm.jpg";
                                    CustomerDTO dto = searchCustomer(txtEnterCusID.getText());
                                    printInvoice(tblOrderDetail.getItems(), tblOrderID.getSelectionModel().getSelectedItem().getOrderID(), txtEnterCusID.getText(), dto.getCustName(), dto.getMobileNumber(), new BigDecimal(lblGrossAmount.getText()), new BigDecimal(lblTotalDiscount.getText()), new BigDecimal(lblNetAmount.getText()), new BigDecimal(txtCash.getText()), new BigDecimal(lblBalance.getText()), url);
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void printInvoice(ObservableList<OrderDetailTM> tmList, String orderId, String customerId, String customerName, String mobileNumber, BigDecimal grossAmount, BigDecimal totalDiscount, BigDecimal netAmount, BigDecimal cash, BigDecimal balance, String url) {
        HashMap map = new HashMap();
        map.put("orderId", orderId);
        map.put("customerName", customerName);
        map.put("mobileNumber", mobileNumber);
        map.put("grossAmount", grossAmount);
        map.put("totalDiscount", totalDiscount);
        map.put("netAmount", netAmount);
        map.put("cash", cash);
        map.put("balance", balance);
        map.put("customerId", customerId);
        map.put("url", url);

        try {
            JasperReport compileReport = (JasperReport) JRLoader.loadObject(getClass().getResource("../view/reports/Invoice.jasper"));
            JasperPrint jasperPrint = JasperFillManager.fillReport(compileReport, map, new JRBeanArrayDataSource(tmList.toArray()));
            JasperViewer.viewReport(jasperPrint, false);

        } catch (JRException e) {
            e.printStackTrace();
        }
    }

    public void deleteOrderOnAction(ActionEvent event) throws IOException {
        Stage window = (Stage) context.getScene().getWindow();
        Stage stage = (Stage) window.getOwner();
        stage.setFullScreen(false);
        try {
            if (!txtEnterCusID.getText().isEmpty()) {
                if (txtEnterCusID.getText().matches("^[C][0-9]{3,5}$")) {
                    if (!tblOrderID.getItems().isEmpty()) {
                        OrderTM orderTM = tblOrderID.getSelectionModel().getSelectedItem();
                        if (orderTM != null) {
                            if (existOrder(orderTM.getOrderID())) {
                                Optional<ButtonType> type = new Alert(Alert.AlertType.WARNING, "Do you want to delete this Order, are you sure ?", ButtonType.YES, ButtonType.NO).showAndWait();
                                if (type.get().equals(ButtonType.YES)) {
                                    boolean orderIsDeleted = manageOrdersBO.deleteOrder(orderTM.getOrderID());
                                    if (orderIsDeleted) {
                                        searchAllOrdersByCustomerId();
                                        Notifications notificationBuilder = Notifications.create()
                                                .title("Warning")
                                                .text("Order has been Deleted successfully..!")
                                                .graphic(new ImageView(new Image("lk/ijse/pos/assets/Done.png")))
                                                .hideAfter(Duration.seconds(4))
                                                .position(Pos.CENTER);
                                        notificationBuilder.darkStyle();
                                        notificationBuilder.showWarning();
                                    } else {
                                        NotificationUtil.setNotifications("Error", "Try again,Something went wrong..!", 3);
                                    }
                                } else {
                                    NotificationUtil.setNotifications("Confirmation", "Order Deletion Cancelled..!", 3);
                                }

                            } else {
                                NotificationUtil.setNotifications("Unknown Order", "This Order doesn't exist..!", new ImageView(new Image("lk/ijse/pos/assets/unknown.png")), 3);
                            }
                        } else {
                            NotificationUtil.setNotifications("Warning", "Please select an OrderId..!", 3);
                        }
                    } else {
                        NotificationUtil.setNotifications("Warning", "Orders list is empty..!, please try again", 3);
                    }
                } else {
                    NotificationUtil.setNotifications("Invalid Data Fields", "Please input valid customer Id..!", 3);
                }
            } else {
                NotificationUtil.setNotifications("Empty Data Fields", "Empty fields & try again later..!", 3);
            }
            //btnDeleteOrder.setDisable(true);
        } catch (Exception e) {
            NotificationUtil.setNotifications("Confirmation", "Order Deletion Cancelled..!", 3);
        }
        stage.setFullScreen(true);
    }

    private void enableOrDisableUpdateOrderButton() {
        btnUpdateOrder.setDisable(!(!tblOrderID.getItems().isEmpty() && !tblOrderDetail.getItems().isEmpty()));
    }

    public void removeItemOnAction(ActionEvent event) {
        if (!tblOrderDetail.getItems().isEmpty()) {
            OrderDetailTM tm = tblOrderDetail.getSelectionModel().getSelectedItem();
            if (tm != null) {
                for (OrderDetailTM cartTM : tblOrderDetail.getItems()) {
                    if (cartTM.getItemCode().equals(tm.getItemCode())) {
                        tblOrderDetail.getItems().remove(cartTM);
                        enableOrDisableUpdateOrderButton();
                        break;
                    }
                }
                tblOrderDetail.refresh();
                calculateTotalCost();
                btnRemoveItem.setDisable(true);

            } else {
                NotificationUtil.setNotifications("Warning..!", "Select Item from table..!", 3);
            }

        } else {
            btnRemoveItem.setDisable(true);
            NotificationUtil.setNotifications("Warning..!", "Table is empty therefore,can't delete data from table", 3);
        }
    }

    private void loadAllOrderDetailsToTheTable() {
        try {
            if (!tblOrderID.getItems().isEmpty()) {
                OrderTM selectedOrderId = tblOrderID.getSelectionModel().getSelectedItem();
                if (selectedOrderId != null) {
                    tblOrderDetail.getItems().clear();
                    ArrayList<CustomDTO> details = manageOrdersBO.getOrderDetailsByOrderId(selectedOrderId.getOrderID());
                    for (CustomDTO dto : details) {
                        tblOrderDetail.getItems().add(new OrderDetailTM(
                                dto.getItemCode(),
                                dto.getDescription(),
                                dto.getPackSize(),
                                dto.getOrderQty(),
                                dto.getUnitPrice(),
                                dto.getDiscount(),
                                new BigDecimal(dto.getOrderQty()).multiply(dto.getUnitPrice())
                        ));
                    }

                } else {
                    tblOrderDetail.getItems().clear();
                }

            } else {
                tblOrderDetail.getItems().clear();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void pressOrderIdTblOnAction(MouseEvent event) {
        txtCash.clear();
        lblBalance.setText("0.00");
        loadAllOrderDetailsToTheTable();
    }

    public void decreaseQtyOnAction(ActionEvent event) {
        if (!tblOrderDetail.getItems().isEmpty()) {
            OrderDetailTM selectedItem = tblOrderDetail.getSelectionModel().getSelectedItem();
            if (selectedItem != null) {
                if (!txtDiscount.getText().isEmpty()) {
                    if (txtDiscount.getText().matches("^[0-9]{1,6}([.][0-9]{2})?$")) {
                        if (selectedItem.getQty() > 1) {
                            selectedItem.setQty(selectedItem.getQty() - 1);
//                selectedItem.setTotal(selectedItem.getQty() * selectedItem.getUnitPrice());
                            selectedItem.setDiscount(new BigDecimal(String.valueOf(selectedItem.getDiscount())).subtract(new BigDecimal(txtDiscount.getText())));
                            selectedItem.setTotalCost(new BigDecimal(selectedItem.getQty()).multiply(selectedItem.getUnitPrice()).setScale(2));
                            tblOrderDetail.refresh();
                            enableOrDisableUpdateOrderButton();
                            calculateTotalCost();
                        } else {
                            NotificationUtil.setNotifications("Warning..!", "Your selected Item's quantity is reached one,therefore,if you want to remove that item from the table, then use Remove item Option", 3);
                        }
                    } else {
                        NotificationUtil.setNotifications("Error..!", "Please enter valid discount Price..!", 3);
                    }
                } else {
                    NotificationUtil.setNotifications("Warning..!", "Please enter discount price..!", 3);
                }
            } else {
                NotificationUtil.setNotifications("Warning..!", "Select Item From The Table..!", 3);
            }
        } else {
            NotificationUtil.setNotifications("Warning..!", "Order List is empty..!", 3);
        }
    }

    public void increaseQtyOnAction(ActionEvent event) {
        if (!tblOrderDetail.getItems().isEmpty()) {
            OrderDetailTM selectedItem = tblOrderDetail.getSelectionModel().getSelectedItem();
            if (selectedItem != null) {
                if (!txtDiscount.getText().isEmpty()) {
                    if (txtDiscount.getText().matches("^[0-9]{1,6}([.][0-9]{2})?$")) {
                        selectedItem.setQty(selectedItem.getQty() + 1);
                        selectedItem.setDiscount(new BigDecimal(String.valueOf(selectedItem.getDiscount())).add(new BigDecimal(txtDiscount.getText())));
                        selectedItem.setTotalCost(new BigDecimal(selectedItem.getQty()).multiply(selectedItem.getUnitPrice()).setScale(2));
                        tblOrderDetail.refresh();
                        enableOrDisableUpdateOrderButton();
                        calculateTotalCost();
                    } else {
                        NotificationUtil.setNotifications("Error..!", "Please enter valid discount Price..!", 3);
                    }
                } else {
                    NotificationUtil.setNotifications("Warning..!", "Please enter discount price..!", 3);
                }
            } else {
                NotificationUtil.setNotifications("Warning..!", "Select Item From The Table..!", 3);
            }
        } else {
            NotificationUtil.setNotifications("Warning..!", "Order List is empty..!", 3);
        }
    }

    public void clearAllOnAction(ActionEvent event) {
        clearItemFields();
        txtEnterCusID.clear();
        btnAddItem.setDisable(true);
        btnUpdateOrder.setDisable(true);
        //btnDeleteOrder.setDisable(true);
        tblOrderDetail.getItems().clear();
        tblOrderID.getItems().clear();
        txtCash.clear();
        lblBalance.setText("0.00");
        calculateTotalCost();
        btnSearchOrders.setDisable(true);
    }

    private void clearItemFields() {
        btnAddItem.setText("      Add to cart");
        cmbItemCode.setDisable(false);
        cmbItemCode.getSelectionModel().clearSelection();
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
    }

    public void tblOnAction(MouseEvent event) {
        btnRemoveItem.setDisable(false);
        if (tblOrderDetail.getItems().isEmpty()) {
            btnUpdateOrder.setDisable(true);
            btnRemoveItem.setDisable(true);
        }
    }

    private void checkFields() {
        try {
            if (txtQuantity.getText().matches("^[1-9][0-9]{0,4}$") && txtDiscount.getText().matches("^[0-9]{1,6}([.][0-9]{2})?$")) {
                txtQuantity.setStyle("-fx-border-color: GREEN");
                txtQuantity.setStyle("-fx-text-fill: BLACK");
                txtDiscount.setStyle("-fx-text-fill: BLACK");
                // btnAddItem.fire();
                btnAddItem.setDisable(cmbItemCode.getSelectionModel().getSelectedItem() == null || txtDescription.getText().isEmpty() || txtPackSize.getText().isEmpty() || txtUnitPrice.getText().isEmpty() || txtQtyOnHand.getText().isEmpty() || txtDiscount.getText().isEmpty() || txtQuantity.getText().isEmpty());
            } else {
                btnAddItem.setDisable(true);
                if (txtQuantity.getText().length() > 0) {
                    if (txtQuantity.getText().matches("^[1-9][0-9]{0,4}$")) {
                        txtQuantity.setStyle("-fx-text-fill: BLACK");
                        txtQuantity.setStyle("-fx-border-color: #F3F3F3");
                    } else {
                        txtQuantity.setStyle("-fx-text-fill: RED");
                        txtQuantity.setStyle("-fx-border-color: RED");
                    }
                } else {
                    txtQuantity.setStyle("-fx-text-fill: BLACK");
                }
                if (txtDiscount.getText().length() > 0) {
                    if (txtDiscount.getText().matches("^[0-9]{1,6}([.][0-9]{2})?$")) {
                        txtDiscount.setStyle("-fx-text-fill: BLACK");
                    } else {
                        txtDiscount.setStyle("-fx-text-fill: RED");
                    }
                } else {
                    txtDiscount.setStyle("-fx-text-fill: BLACK");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onHidingOnAction(Event event) {
        checkFields();
    }

    public void checkOnAction(KeyEvent keyEvent) {
        /*try {
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
                    if (txtQuantity.getText().matches("^[1-9][0-9]{0,4}$")) {

                    } else {
                        txtQuantity.setStyle("-fx-text-fill: RED");
                        txtQuantity.setStyle("-fx-border-color: RED");
                    }
                    if (txtDiscount.getText().matches("^[0-9]{1,6}([.][0-9]{2})?$")) {

                    } else {
                        txtDiscount.setStyle("-fx-text-fill: RED");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }*/
        checkFields();
    }

    public void txtCashKeyReleasedOnAction(KeyEvent keyEvent) {
        try {
            if (!txtCash.getText().isEmpty()) {
                if (txtCash.getText().matches("^[1-9][0-9]{0,7}([.][0-9]{2})?$")) {
                    if ((Double.parseDouble(lblNetAmount.getText()) <= Double.parseDouble(txtCash.getText()))) {
                        txtCash.setStyle("-fx-text-fill: BLACK");
                    } else {
                        txtCash.setStyle("-fx-text-fill: RED");
                    }
                } else {
                    txtCash.setStyle("-fx-text-fill: RED");
                }
            } else {
                txtCash.setStyle("-fx-text-fill: BLACK");
            }
        } catch (Exception e) {

        }
    }
}
