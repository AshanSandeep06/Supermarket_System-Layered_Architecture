package lk.ijse.pos.dao.custom.impl;

import lk.ijse.pos.dao.custom.ItemDAO;
import lk.ijse.pos.dto.ItemDTO;
import lk.ijse.pos.entity.Item;
import lk.ijse.pos.util.CrudUtil;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class ItemDAOImpl implements ItemDAO {
    @Override
    public ArrayList<Item> getAll() throws SQLException, ClassNotFoundException {
        ResultSet result = CrudUtil.execute("SELECT * FROM Item");
        ArrayList<Item> itemList = new ArrayList<>();
        while(result.next()){
            itemList.add(new Item(
                    result.getString(1),
                    result.getString(2),
                    result.getString(3),
                    result.getBigDecimal(4),
                    result.getInt(5),
                    result.getBigDecimal(6)
            ));
        }
        return itemList;
    }

    @Override
    public boolean save(Item entity) throws SQLException, ClassNotFoundException {
        return CrudUtil.execute("INSERT INTO Item VALUES (?,?,?,?,?,?)",entity.getItemCode(),entity.getDescription(),entity.getPackSize(),entity.getUnitPrice(),entity.getQtyOnHand(),entity.getDiscount());
    }

    @Override
    public boolean update(Item entity) throws SQLException, ClassNotFoundException {
        return CrudUtil.execute("UPDATE Item SET description=?,packSize=?,unitPrice=?,qtyOnHand=?,discount=? WHERE itemCode=?",entity.getDescription(),entity.getPackSize(),entity.getUnitPrice(),entity.getQtyOnHand(),entity.getDiscount(),entity.getItemCode());
    }

    @Override
    public boolean delete(String itemCode) throws SQLException, ClassNotFoundException {
        return CrudUtil.execute("DELETE FROM Item WHERE itemCode=?",itemCode);
    }

    @Override
    public Item search(String itemCode) throws SQLException, ClassNotFoundException {
        ResultSet result = CrudUtil.execute("SELECT * FROM Item WHERE itemCode=?",itemCode);
        if(result.next()){
            return new Item(
                    result.getString(1),
                    result.getString(2),
                    result.getString(3),
                    result.getBigDecimal(4),
                    result.getInt(5),
                    result.getBigDecimal(6)
            );
        }
        return null;
    }

    @Override
    public boolean isExists(String itemCode) throws SQLException, ClassNotFoundException {
       ResultSet result = CrudUtil.execute("SELECT itemCode FROM Item WHERE itemCode=?",itemCode);
        return result.next();
    }

    @Override
    public String generateNewId() throws SQLException, ClassNotFoundException {
        ResultSet result = CrudUtil.execute("SELECT itemCode FROM Item ORDER BY itemCode DESC LIMIT 1");
        if(result.next()){
            return result.getString(1);
        }
        return null;
    }

    @Override
    public int getAllCount() throws SQLException, ClassNotFoundException {
        ResultSet result = CrudUtil.execute("SELECT COUNT(itemCode) FROM Item");
        int itemsCount = 0;
        while (result.next()){
            itemsCount = result.getInt(1);
        }
        return itemsCount;
    }
}
