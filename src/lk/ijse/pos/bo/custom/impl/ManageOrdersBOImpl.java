package lk.ijse.pos.bo.custom.impl;

import javafx.collections.ObservableList;
import lk.ijse.pos.bo.custom.ManageOrdersBO;
import lk.ijse.pos.dao.DAOFactory;
import lk.ijse.pos.dao.custom.*;
import lk.ijse.pos.db.DBConnection;
import lk.ijse.pos.dto.*;
import lk.ijse.pos.entity.*;
import lk.ijse.pos.view.tdm.OrderDetailTM;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanArrayDataSource;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.view.JasperViewer;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class ManageOrdersBOImpl implements ManageOrdersBO {
    CustomerDAO customerDAO = (CustomerDAO) DAOFactory.getDaoFactory().getDAO(DAOFactory.DAOTypes.CUSTOMER);
    ItemDAO itemDAO = (ItemDAO) DAOFactory.getDaoFactory().getDAO(DAOFactory.DAOTypes.ITEM);
    OrderDAO orderDAO = (OrderDAO) DAOFactory.getDaoFactory().getDAO(DAOFactory.DAOTypes.ORDER);
    OrderDetailDAO orderDetailDAO = (OrderDetailDAO) DAOFactory.getDaoFactory().getDAO(DAOFactory.DAOTypes.ORDERDETAIL);
    JoinQueryDAO joinQueryDAO = (JoinQueryDAO) DAOFactory.getDaoFactory().getDAO(DAOFactory.DAOTypes.JOINQUERYDAO);

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
    public ArrayList<OrderDTO> getOrdersByCustomerId(String customerId) throws SQLException, ClassNotFoundException {
        ArrayList<Order> ordersList = orderDAO.searchOrdersByCustomerId(customerId);
        ArrayList<OrderDTO> allOrders = new ArrayList<>();
        for (Order order : ordersList) {
            allOrders.add(new OrderDTO(
                    order.getOrderID(),
                    order.getOrderDate(),
                    order.getOrderTime(),
                    order.getCustID(),
                    order.getDiscount(),
                    order.getTotalCost()
            ));
        }
        return allOrders;
    }

    @Override
    public boolean customerIsExists(String id) throws SQLException, ClassNotFoundException {
        return customerDAO.isExists(id);
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
    public CustomerDTO searchCustomer(String customerId) throws SQLException, ClassNotFoundException {
        Customer entity = customerDAO.search(customerId);
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
    public ArrayList<CustomDTO> getOrderDetailsByOrderId(String orderId) throws SQLException, ClassNotFoundException{
        ArrayList<CustomDTO> orderDetails=new ArrayList<>();
        ArrayList<CustomEntity> entityList = joinQueryDAO.searchOrderDetailsByOrderId(orderId);
        for (CustomEntity entity : entityList) {
            orderDetails.add(new CustomDTO(
                    entity.getItemCode(),
                    entity.getDescription(),
                    entity.getPackSize(),
                    entity.getUnitPrice(),
                    entity.getOrderQty(),
                    entity.getDiscount()
            ));
        }
        return orderDetails;
    }

    @Override
    public OrderDTO searchOrder(String orderId) throws SQLException, ClassNotFoundException{
        Order order = orderDAO.search(orderId);
        if(order!=null){
            return new OrderDTO(
                    order.getOrderID(),
                    order.getOrderDate(),
                    order.getOrderTime(),
                    order.getCustID(),
                    order.getDiscount(),
                    order.getTotalCost()
            );
        }
        return null;
    }

    @Override
    public boolean orderIsExists(String orderId) throws SQLException, ClassNotFoundException {
        return orderDAO.isExists(orderId);
    }

    @Override
    public boolean updateOrder(OrderDTO dto) throws SQLException, ClassNotFoundException{
        Connection connection= DBConnection.getInstance().getConnection();
        connection.setAutoCommit(false);
        Order order = new Order(dto.getOrderID(),dto.getOrderDate(),dto.getOrderTime(),dto.getCustID(),dto.getDiscount(),dto.getTotalCost());

        boolean orderIsUpdated = orderDAO.update(order);
        if(!orderIsUpdated){
            connection.rollback();
            connection.setAutoCommit(true);
            return false;
        }

        ArrayList<OrderDetail> allDetails = orderDetailDAO.getAllOrderDetailsByOrderId(dto.getOrderID());
        boolean orderDetailsIsDeleted = orderDetailDAO.delete(dto.getOrderID());
        if(!orderDetailsIsDeleted){
            connection.rollback();
            connection.setAutoCommit(true);
            return false;
        }

        for (OrderDetail detail : allDetails) {
            Item item = itemDAO.search(detail.getItemCode());
            item.setQtyOnHand(item.getQtyOnHand() + detail.getOrderQty());

            boolean updateItem = itemDAO.update(item);
            if(!updateItem){
                connection.rollback();
                connection.setAutoCommit(true);
                return false;
            }
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
    public boolean deleteOrder(String orderId) throws SQLException, ClassNotFoundException{
        Connection connection=DBConnection.getInstance().getConnection();
        connection.setAutoCommit(false);

        ArrayList<OrderDetail> allDetails = orderDetailDAO.getAllOrderDetailsByOrderId(orderId);

        boolean orderIsDeleted= orderDAO.delete(orderId);
        if(!orderIsDeleted){
            connection.rollback();
            connection.setAutoCommit(true);
            return false;
        }

        if(allDetails.size()>0){
            /*boolean orderDetailsIsDeleted = orderDetailDAO.delete(orderId);
            if(!orderDetailsIsDeleted){
                connection.rollback();
                connection.setAutoCommit(true);
                return false;
            }*/
            for (OrderDetail detail : allDetails) {
                Item item = itemDAO.search(detail.getItemCode());
                item.setQtyOnHand(item.getQtyOnHand() + detail.getOrderQty());

                boolean updateItem = itemDAO.update(item);
                if(!updateItem){
                    connection.rollback();
                    connection.setAutoCommit(true);
                    return false;
                }
            }
        }
        connection.commit();
        connection.setAutoCommit(true);
        return true;
    }
}
