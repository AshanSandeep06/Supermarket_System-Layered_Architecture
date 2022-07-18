package lk.ijse.pos.controller;

import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import lk.ijse.pos.bo.BOFactory;
import lk.ijse.pos.bo.custom.StockReportBO;
import lk.ijse.pos.bo.custom.impl.StockReportBOImpl;
import lk.ijse.pos.dto.ItemDTO;
import lk.ijse.pos.view.tdm.ItemTM;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;

public class StockReportFormController {
    public TableView<ItemTM> tblItem;
    public TableColumn colItemCode;
    public TableColumn colDescription;
    public TableColumn colPackSize;
    public TableColumn colUnitPrice;
    public TableColumn colDiscount;
    public TableColumn colQtyOnHand;

    StockReportBO stockReportBO = (StockReportBO) BOFactory.getBoFactory().getBO(BOFactory.BOTypes.STOCKREPORTBO);

    public void initialize(){
        colItemCode.setCellValueFactory(new PropertyValueFactory<>("itemCode"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        colPackSize.setCellValueFactory(new PropertyValueFactory<>("packSize"));
        colQtyOnHand.setCellValueFactory(new PropertyValueFactory<>("qtyOnHand"));
        colDiscount.setCellValueFactory(new PropertyValueFactory<>("discount"));
        colUnitPrice.setCellValueFactory(new PropertyValueFactory<>("unitPrice"));
        loadAllItemsToTheTable();
    }

    private void loadAllItemsToTheTable() {
        tblItem.getItems().clear();
        try {
            ArrayList<ItemDTO> arrayList = stockReportBO.getAllItems();

            for (ItemDTO dto : arrayList) {
                tblItem.getItems().add(new ItemTM(dto.getItemCode(), dto.getDescription(), dto.getPackSize(), dto.getUnitPrice(), dto.getQtyOnHand(), dto.getDiscount()));
            }

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
