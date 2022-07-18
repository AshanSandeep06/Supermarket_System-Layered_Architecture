package lk.ijse.pos.bo.custom.impl;

import lk.ijse.pos.bo.custom.CashierDashBoardBO;
import lk.ijse.pos.dao.DAOFactory;
import lk.ijse.pos.dao.custom.CustomerDAO;
import lk.ijse.pos.dao.custom.ItemDAO;
import lk.ijse.pos.dao.custom.OrderDAO;
import lk.ijse.pos.dao.custom.OrderDetailDAO;
import lk.ijse.pos.db.DBConnection;
import lk.ijse.pos.dto.CustomerDTO;
import lk.ijse.pos.dto.ItemDTO;
import lk.ijse.pos.dto.OrderDTO;
import lk.ijse.pos.dto.OrderDetailDTO;
import lk.ijse.pos.entity.Customer;
import lk.ijse.pos.entity.Item;
import lk.ijse.pos.entity.Order;
import lk.ijse.pos.entity.OrderDetail;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

public class CashierDashBoardBOImpl implements CashierDashBoardBO {
    private final CustomerDAO customerDAO = (CustomerDAO) DAOFactory.getDaoFactory().getDAO(DAOFactory.DAOTypes.CUSTOMER);
    private final ItemDAO itemDAO = (ItemDAO) DAOFactory.getDaoFactory().getDAO(DAOFactory.DAOTypes.ITEM);
    private final OrderDAO orderDAO = (OrderDAO) DAOFactory.getDaoFactory().getDAO(DAOFactory.DAOTypes.ORDER);
    private final OrderDetailDAO orderDetailDAO = (OrderDetailDAO) DAOFactory.getDaoFactory().getDAO(DAOFactory.DAOTypes.ORDERDETAIL);

    @Override
    public boolean placeOrder(OrderDTO dto) throws SQLException, ClassNotFoundException {
        Connection connection= DBConnection.getInstance().getConnection();
        connection.setAutoCommit(false);

        Order order = new Order(dto.getOrderID(),dto.getOrderDate(),dto.getOrderTime(),dto.getCustID(),dto.getDiscount(),dto.getTotalCost());

        boolean orderIsSaved = orderDAO.save(order);
        if(!orderIsSaved){
            connection.rollback();
            connection.setAutoCommit(true);
            return false;
        }

        for(OrderDetailDTO detail : dto.getOrderDetails()){
            boolean orderDetailIsSaved = orderDetailDAO.save(new OrderDetail(detail.getOrderID(),detail.getItemCode(),detail.getDescription(),detail.getUnitPrice(),detail.getOrderQty(),detail.getDiscount()));
            if(!orderDetailIsSaved){
                connection.rollback();
                connection.setAutoCommit(true);
                return false;
            }

            Item item = itemDAO.search(detail.getItemCode());
            item.setQtyOnHand(item.getQtyOnHand() - detail.getOrderQty());

            boolean updateItem = itemDAO.update(item);
            if(!updateItem){
                connection.rollback();
                connection.setAutoCommit(true);
                return false;
            }
        }

        connection.commit();
        connection.setAutoCommit(true);
        return true;
    }

    @Override
    public CustomerDTO getCustomerByMobileNumber(String mobileNumber) throws SQLException, ClassNotFoundException {
        Customer entity = customerDAO.searchCustomerByMobileNumber(mobileNumber);
        if (entity != null) {
            return new CustomerDTO(
                    entity.getCustID(),
                    entity.getCustTitle(),
                    entity.getCustName(),
                    entity.getCustAddress(),
                    entity.getCity(),
                    entity.getProvince(),
                    entity.getPostalCode(),
                    entity.getMobileNumber()
            );
        }
        return null;
    }

    @Override
    public ArrayList<CustomerDTO> getAllCustomers() throws SQLException, ClassNotFoundException {
        ArrayList<CustomerDTO> allCustomers = new ArrayList<>();
        for (Customer entity : customerDAO.getAll()) {
            allCustomers.add(new CustomerDTO(
                    entity.getCustID(),
                    entity.getCustTitle(),
                    entity.getCustName(),
                    entity.getCustAddress(),
                    entity.getCity(),
                    entity.getProvince(),
                    entity.getPostalCode(),
                    entity.getMobileNumber()
            ));
        }
        return allCustomers;
    }

    @Override
    public ArrayList<ItemDTO> getAllItems() throws SQLException, ClassNotFoundException {
        ArrayList<ItemDTO> allItems = new ArrayList<>();
        for (Item entity : itemDAO.getAll()) {
            allItems.add(new ItemDTO(
                    entity.getItemCode(),
                    entity.getDescription(),
                    entity.getPackSize(),
                    entity.getUnitPrice(),
                    entity.getQtyOnHand(),
                    entity.getDiscount()
            ));
        }
        return allItems;
    }

    @Override
    public boolean customerIsExists(String id) throws SQLException, ClassNotFoundException {
        return customerDAO.isExists(id);
    }

    @Override
    public boolean itemIsExists(String id) throws SQLException, ClassNotFoundException {
        return itemDAO.isExists(id);
    }

    @Override
    public boolean orderIsExists(String orderId) throws SQLException, ClassNotFoundException {
        return orderDAO.isExists(orderId);
    }

    @Override
    public ItemDTO searchItem(String itemCode) throws SQLException, ClassNotFoundException {
        Item entity = itemDAO.search(itemCode);
        if (entity != null) {
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

    @Override
    public String generateNewOrderId() throws SQLException, ClassNotFoundException {
        String orderId = orderDAO.generateNewId();
        String finalId = "OI-0001";

        if (orderId != null) {

            String[] splitString = orderId.split("-");
            int id = Integer.parseInt(splitString[1]);
            id++;

            if (id < 10) {
                finalId = "OI-000" + id;
            } else if (id < 100) {
                finalId = "OI-00" + id;
            } else if (id < 1000) {
                finalId = "OI-0" + id;
            } else if (id < 10000) {
                finalId = "OI-" + id;
            }
            return finalId;
        } else {
            return finalId;
        }
    }
}
