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
import lk.ijse.pos.bo.custom.ItemBO;
import lk.ijse.pos.bo.custom.impl.ItemBOImpl;
import lk.ijse.pos.dto.ItemDTO;
import lk.ijse.pos.util.ValidationUtil;
import lk.ijse.pos.view.tdm.ItemTM;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Optional;
import java.util.regex.Pattern;

public class ManageItemsFormController {
    public JFXTextField txtItemCode;
    public JFXTextField txtDescription;
    public JFXTextField txtPackSize;
    public JFXTextField txtQtyOnHand;
    public JFXTextField txtUnitPrice;
    public JFXTextField txtDiscount;
    public JFXButton btnAddNewItem;
    public TextField txtSearchItem;
    public JFXButton btnAddItem;
    public JFXButton btnUpdateItem;
    public JFXButton btnDeleteItem;
    public TableView<ItemTM> tblItem;
    public TableColumn colItemCode;
    public TableColumn colDescription;
    public TableColumn colPackSize;
    public TableColumn colUnitPrice;
    public TableColumn colQtyOnHand;
    public TableColumn colDiscount;
    public AnchorPane context;
    public JFXButton btnSearch;
    LinkedHashMap<TextField, Pattern> map = new LinkedHashMap<>();
    LinkedHashMap<TextField, Pattern> hashMap = new LinkedHashMap<>();

    //Property Injection
    private final ItemBO itemBO = (ItemBO) BOFactory.getBoFactory().getBO(BOFactory.BOTypes.ITEM);

    public void initialize() {
        btnSearch.setDisable(true);
        colItemCode.setCellValueFactory(new PropertyValueFactory<>("itemCode"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        colPackSize.setCellValueFactory(new PropertyValueFactory<>("packSize"));
        colUnitPrice.setCellValueFactory(new PropertyValueFactory<>("unitPrice"));
        colQtyOnHand.setCellValueFactory(new PropertyValueFactory<>("qtyOnHand"));
        colDiscount.setCellValueFactory(new PropertyValueFactory<>("discount"));
        btnAddItem.setDisable(true);
        btnUpdateItem.setDisable(true);
        btnDeleteItem.setDisable(true);

        storeValidations();
        txtItemCode.setText(generateNewItemCode());
        loadAllItems();

        tblItem.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, item) -> {
            if (item != null) {
                if (!tblItem.getItems().isEmpty()) {
                    btnUpdateItem.setDisable(false);
                    btnDeleteItem.setDisable(false);
                    setFontColor(txtItemCode,txtDescription,txtPackSize,txtUnitPrice,txtQtyOnHand,txtDiscount);
                    setItemData(new ItemDTO(item.getItemCode(),item.getDescription(),item.getPackSize(),item.getUnitPrice(),item.getQtyOnHand(),item.getDiscount()));
                }
            }
        });
    }

    public void setFontColor(TextField... textFields) {
        for (TextField textField : textFields) {
            textField.setStyle("-fx-text-fill: BLACK");
        }
    }

    public void searchItemKeyRealeasedOnAction(KeyEvent keyEvent) {
        if (txtSearchItem.getText().matches("^[I][0-9]{3,5}$")) {
            txtSearchItem.setStyle("-fx-text-fill: BLACK");
            btnSearch.setDisable(false);
        }else{
            if(txtSearchItem.getText().length()>0){
                txtSearchItem.setStyle("-fx-text-fill: RED");
                btnSearch.setDisable(true);
            }else{
                txtSearchItem.setStyle("-fx-text-fill: BLACK");
                btnSearch.setDisable(true);
            }
        }
    }

    private void loadAllItems() {
        tblItem.getItems().clear();
        try {
            ArrayList<ItemDTO> arrayList = itemBO.getAllItems();

            for (ItemDTO dto : arrayList) {
                tblItem.getItems().add(new ItemTM(dto.getItemCode(), dto.getDescription(), dto.getPackSize(), dto.getUnitPrice(), dto.getQtyOnHand(), dto.getDiscount()));
            }

        } catch (SQLException | ClassNotFoundException e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
        }
    }

    private String generateNewItemCode() {
        try {
            return itemBO.generateNewItemCode();

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void storeValidations() {
        map.put(txtItemCode, Pattern.compile("^[I][0-9]{3,5}$"));
        map.put(txtDescription, Pattern.compile("^[A-z0-9-& ]{3,45}$"));
        map.put(txtPackSize, Pattern.compile("^[A-z 0-9()]{3,20}$"));
        //map.put(txtUnitPrice, Pattern.compile("^[1-9][0-9]{0,5}(.[0-9]{2})?$"));
        map.put(txtUnitPrice,Pattern.compile("^[1-9][0-9]{0,7}([.][0-9]{2})?$"));
        map.put(txtQtyOnHand, Pattern.compile("^[0-9]{1,6}$"));
        map.put(txtDiscount, Pattern.compile("^[0-9]{1,6}([.][0-9]{2})?$"));
        hashMap = map;
    }

    private void setItemData(ItemDTO dto) {
        txtItemCode.setText(dto.getItemCode());
        txtDescription.setText(dto.getDescription());
        txtPackSize.setText(dto.getPackSize());
        txtUnitPrice.setText(String.valueOf(dto.getUnitPrice()));
        txtQtyOnHand.setText(String.valueOf(dto.getQtyOnHand()));
        txtDiscount.setText(String.valueOf(dto.getDiscount()));
    }

    public void txtSearchItemOnAction(ActionEvent event) {
        searchItem();
    }

    public void searchItemOnAction(ActionEvent event) {
        searchItem();
    }

    private void searchItem(){
        try {
            if (!txtSearchItem.getText().isEmpty()) {
                String itemCode = txtSearchItem.getText();
                if (itemCode.matches("^[I][0-9]{3,5}$")) {
                    ItemDTO itemDTO = itemBO.searchItem(itemCode);

                    if (itemDTO != null) {
                        setItemData(itemDTO);
                        btnUpdateItem.setDisable(false);
                        btnDeleteItem.setDisable(false);
                    } else {
                        Optional<ButtonType> buttonType = new Alert(Alert.AlertType.ERROR, "No item exists for this itemCode..! Do you want to add this item",ButtonType.YES,ButtonType.NO).showAndWait();
                        clearAll();
                        txtSearchItem.clear();
                        if(buttonType.get().equals(ButtonType.YES)){
                            txtDescription.requestFocus();
                        }else{

                        }
                    }
                } else {
                    clearAll();
                    txtSearchItem.clear();
                    new Alert(Alert.AlertType.WARNING, "Invalid ItemCode..!").show();
                }
            } else {
                clearAll();
                new Alert(Alert.AlertType.WARNING, "Empty field,try again..!").show();
            }

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        btnAddItem.setDisable(true);
    }

    public void validateKeyReleasedOnAction(KeyEvent keyEvent) {
        ValidationUtil.validate(hashMap,btnUpdateItem,btnDeleteItem);
        Object validate = ValidationUtil.validate(map,btnAddItem);

        try {
            if (!txtItemCode.getText().isEmpty()) {
                if (existItem(txtItemCode.getText())) {
                    btnAddItem.setDisable(true);
                }else{
                    btnUpdateItem.setDisable(true);
                    btnDeleteItem.setDisable(true);
                }
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        if (KeyCode.ENTER == keyEvent.getCode()) {
            if (validate instanceof TextField) {
                TextField textField = (TextField) validate;
                textField.requestFocus();
            } else {
                btnAddItem.fire();
            }
        }
    }

    private boolean existItem(String itemCode) throws SQLException, ClassNotFoundException {
        return itemBO.itemIsExists(itemCode);
    }

    public void addItemOnAction(ActionEvent event) {
        try {
            if (!txtItemCode.getText().isEmpty() && !txtDescription.getText().isEmpty() && !txtPackSize.getText().isEmpty() && !txtUnitPrice.getText().isEmpty() && !txtQtyOnHand.getText().isEmpty() && !txtDiscount.getText().isEmpty()) {
                if (!txtDescription.getText().matches("^[A-z0-9-& ]{3,45}$")) {
                    new Alert(Alert.AlertType.ERROR, "Description must be at least 3 characters long").show();
                    return;
                } else if (!txtPackSize.getText().matches("^[A-z 0-9()]{3,20}$")) {
                    new Alert(Alert.AlertType.ERROR, "Invalid Pack Size.(Ex :- 4)").show();
                    return;
                } else if (!txtQtyOnHand.getText().matches("^[0-9]{1,6}$")) {
                    new Alert(Alert.AlertType.ERROR, "Invalid Quantity.(Ex :- 100)").show();
                    return;
                } else if (!txtDiscount.getText().matches("^[0-9]+(.[0-9]{2})?$")) {
                    new Alert(Alert.AlertType.ERROR, "Invalid discount.(Ex :- 50.00)").show();
                    return;
                } else if (!txtUnitPrice.getText().matches("^[1-9][0-9]*(.[0-9]{2})?$")) {
                    new Alert(Alert.AlertType.ERROR, "Invalid unit price.(Ex :- 50.00)").show();
                    return;
                }

                BigDecimal unitPrice=new BigDecimal(txtUnitPrice.getText()).setScale(2);
                BigDecimal discount = new BigDecimal(txtDiscount.getText()).setScale(2);
                ItemDTO dto = new ItemDTO(txtItemCode.getText(), txtDescription.getText(), txtPackSize.getText(), unitPrice, Integer.parseInt(txtQtyOnHand.getText()), discount);

                boolean itemIsSaved = itemBO.saveItem(dto);

                if (itemIsSaved) {
                    tblItem.getItems().add(new ItemTM(dto.getItemCode(), dto.getDescription(), dto.getPackSize(), dto.getUnitPrice(), dto.getQtyOnHand(), dto.getDiscount()));
                    new Alert(Alert.AlertType.CONFIRMATION, "Saved Successfully", ButtonType.OK).show();
                } else {
                    new Alert(Alert.AlertType.ERROR, "Try again..!").show();
                }

            } else {
                new Alert(Alert.AlertType.WARNING, "Empty fields,Please fill the fields & try again..!").show();
            }

        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Failed to save the item, Try again..!").show();
        }
        clearAll();
        txtSearchItem.clear();
    }

    public void updateItemOnAction(ActionEvent event) {
        try {
            if (!tblItem.getItems().isEmpty()) {
                if (tblItem.getSelectionModel().getSelectedItem() != null || !txtSearchItem.getText().isEmpty()) {
                    if (!txtItemCode.getText().isEmpty() && !txtDescription.getText().isEmpty() && !txtPackSize.getText().isEmpty() && !txtUnitPrice.getText().isEmpty() && !txtQtyOnHand.getText().isEmpty() && !txtDiscount.getText().isEmpty()) {
                        if (!txtDescription.getText().matches("^[A-z0-9-& ]{3,45}$")) {
                            new Alert(Alert.AlertType.ERROR, "Description must be at least 3 characters long").show();
                            return;
                        } else if (!txtPackSize.getText().matches("^[A-z 0-9()]{3,20}$")) {
                            new Alert(Alert.AlertType.ERROR, "Invalid Pack Size.(Ex :- 4)").show();
                            return;
                        } else if (!txtQtyOnHand.getText().matches("^[0-9]{1,6}$")) {
                            new Alert(Alert.AlertType.ERROR, "Invalid Quantity.(Ex :- 100)").show();
                            return;
                        } else if (!txtDiscount.getText().matches("^[0-9]+(.[0-9]{2})?$")) {
                            new Alert(Alert.AlertType.ERROR, "Invalid discount.(Ex :- 50.00)").show();
                            return;
                        } else if (!txtUnitPrice.getText().matches("^[1-9][0-9]*(.[0-9]{2})?$")) {
                            new Alert(Alert.AlertType.ERROR, "Invalid unit price.(Ex :- 50.00)").show();
                            return;
                        }

                        BigDecimal unitPrice=new BigDecimal(txtUnitPrice.getText()).setScale(2);
                        BigDecimal discount = new BigDecimal(txtDiscount.getText()).setScale(2);

                        boolean itemIsUpdated = itemBO.updateItem(new ItemDTO(txtItemCode.getText(), txtDescription.getText(), txtPackSize.getText(), unitPrice, Integer.parseInt(txtQtyOnHand.getText()), discount));

                        if (itemIsUpdated) {
                            new Alert(Alert.AlertType.CONFIRMATION, "Updated Successfully", ButtonType.OK).show();
                        } else {
                            new Alert(Alert.AlertType.ERROR, "Try again..!").show();
                        }

                    } else {
                        new Alert(Alert.AlertType.WARNING, "Empty fields,Please fill the fields & try again..!").show();
                    }
                } else {
                    new Alert(Alert.AlertType.ERROR, "Please select an item for update or enter ItemCode which item want to update..!").show();
                }
            } else {
                new Alert(Alert.AlertType.ERROR, "Item table is empty therefore,can't update data..!").show();
            }

        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Failed to update item, Try again..!").show();
        }
        clearAll();
        txtSearchItem.clear();
        loadAllItems();
        tblItem.refresh();
    }

    public void deleteItemOnAction(ActionEvent event) {
        try {
            if (!tblItem.getItems().isEmpty()) {
                if (tblItem.getSelectionModel().getSelectedItem() != null || !txtSearchItem.getText().isEmpty()) {
                    ItemTM tm = tblItem.getSelectionModel().getSelectedItem();
                    String itemCode = txtSearchItem.getText();

                    boolean itemIsDeleted = itemBO.deleteItem(tm != null ? tm.getItemCode() : itemCode);

                    if (itemIsDeleted) {
                        new Alert(Alert.AlertType.CONFIRMATION, "Deleted Successfully", ButtonType.OK).show();
                    } else {
                        new Alert(Alert.AlertType.ERROR, "Try again..!").show();
                    }
                } else {
                    new Alert(Alert.AlertType.ERROR, "Please select an item for delete or enter ItemCode which item want to delete..!").show();
                }
            } else {
                new Alert(Alert.AlertType.ERROR, "Item table is empty therefore,can't delete data..!").show();
            }
        } catch (SQLException | ClassNotFoundException e) {
            new Alert(Alert.AlertType.ERROR, "Failed to delete item, Try again..!").show();
        }
        clearAll();
        txtSearchItem.clear();
        loadAllItems();
        tblItem.refresh();
    }

    public void itemClearAllOnAction(ActionEvent event) {
        clearAll();
        txtSearchItem.clear();
    }

    public void btnAddNextItem(ActionEvent event) {
        clearAll();
        txtSearchItem.clear();
    }

    private void clearAll() {
        txtItemCode.setText(generateNewItemCode());

        txtDescription.clear();
        txtPackSize.clear();
        txtUnitPrice.clear();
        txtQtyOnHand.clear();
        txtDescription.requestFocus();
        txtDiscount.clear();
        tblItem.getSelectionModel().clearSelection();
        btnAddItem.setDisable(true);
        btnUpdateItem.setDisable(true);
        btnDeleteItem.setDisable(true);
        //txtSearchItem.clear();
        setBorders(txtItemCode, txtDescription, txtPackSize, txtUnitPrice, txtQtyOnHand, txtDiscount);
    }

    //reset border colors
    public void setBorders(TextField... textFields) {
        for (TextField textField : textFields) {
            textField.setStyle("-fx-border-color:  #F3F3F3"); //null
        }
    }
}
