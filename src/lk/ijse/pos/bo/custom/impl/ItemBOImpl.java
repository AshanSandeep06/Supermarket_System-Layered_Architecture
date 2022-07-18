package lk.ijse.pos.bo.custom.impl;

import lk.ijse.pos.bo.custom.ItemBO;
import lk.ijse.pos.dao.DAOFactory;
import lk.ijse.pos.dao.custom.ItemDAO;
import lk.ijse.pos.dto.ItemDTO;
import lk.ijse.pos.entity.Item;

import java.sql.SQLException;
import java.util.ArrayList;

public class ItemBOImpl implements ItemBO {

    //Property Injection
    private final ItemDAO itemDAO = (ItemDAO) DAOFactory.getDaoFactory().getDAO(DAOFactory.DAOTypes.ITEM);

    @Override
    public ArrayList<ItemDTO> getAllItems() throws SQLException, ClassNotFoundException {
        ArrayList<ItemDTO> allItems = new ArrayList<>();
        for (Item i : itemDAO.getAll()) {
            allItems.add(new ItemDTO(
                    i.getItemCode(),
                    i.getDescription(),
                    i.getPackSize(),
                    i.getUnitPrice(),
                    i.getQtyOnHand(),
                    i.getDiscount()
            ));
        }
        return allItems;
    }

    @Override
    public String generateNewItemCode() throws SQLException, ClassNotFoundException {
        String itemCode = itemDAO.generateNewId();
        String finalId = "I001";

        if (itemCode != null) {

            String[] splitString = itemCode.split("I");
            int id = Integer.parseInt(splitString[1]);
            id++;

            if (id < 10) {
                finalId = "I00" + id;
            } else if (id < 100) {
                finalId = "I0" + id;
            } else {
                finalId = "I" + id;
            }
            return finalId;
        } else {
            return finalId;
        }
    }

    @Override
    public boolean itemIsExists(String itemCode) throws SQLException, ClassNotFoundException {
        return itemDAO.isExists(itemCode);
    }

    @Override
    public boolean saveItem(ItemDTO dto) throws SQLException, ClassNotFoundException {
        return itemDAO.save(new Item(
                dto.getItemCode(),
                dto.getDescription(),
                dto.getPackSize(),
                dto.getUnitPrice(),
                dto.getQtyOnHand(),
                dto.getDiscount()
        ));
    }

    @Override
    public boolean updateItem(ItemDTO dto) throws SQLException, ClassNotFoundException {
        return itemDAO.update(new Item(
                dto.getItemCode(),
                dto.getDescription(),
                dto.getPackSize(),
                dto.getUnitPrice(),
                dto.getQtyOnHand(),
                dto.getDiscount()
        ));
    }

    @Override
    public boolean deleteItem(String itemCode) throws SQLException, ClassNotFoundException {
        return itemDAO.delete(itemCode);
    }

    @Override
    public ItemDTO searchItem(String itemCode) throws SQLException, ClassNotFoundException {
        Item entity = itemDAO.search(itemCode);
        if(entity!=null){
            return new ItemDTO(
                    entity.getItemCode(),
                    entity.getDescription(),
                    entity.getPackSize(),
                    entity.getUnitPrice(),
                    entity.getQtyOnHand(),
                    entity.getDiscount()
            );
        }
        return null;
    }
}
