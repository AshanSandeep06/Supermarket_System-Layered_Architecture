package lk.ijse.pos.bo.custom.impl;

import lk.ijse.pos.bo.custom.StockReportBO;
import lk.ijse.pos.dao.DAOFactory;
import lk.ijse.pos.dao.custom.ItemDAO;
import lk.ijse.pos.dto.ItemDTO;
import lk.ijse.pos.entity.Item;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;

public class StockReportBOImpl implements StockReportBO {
    private final ItemDAO itemDAO = (ItemDAO) DAOFactory.getDaoFactory().getDAO(DAOFactory.DAOTypes.ITEM);

    @Override
    public ArrayList<ItemDTO> getAllItems() throws SQLException, ClassNotFoundException {
        ArrayList<ItemDTO> allItems = new ArrayList<>();
        for (Item i : itemDAO.getAll()) {
            allItems.add(new ItemDTO(i.getItemCode(), i.getDescription(), i.getPackSize(), i.getUnitPrice(), i.getQtyOnHand(), i.getDiscount()));
        }
        return allItems;
    }
}