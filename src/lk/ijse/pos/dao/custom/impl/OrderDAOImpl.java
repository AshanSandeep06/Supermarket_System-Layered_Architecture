package lk.ijse.pos.dao.custom.impl;

import lk.ijse.pos.dao.CrudDAO;
import lk.ijse.pos.dao.custom.OrderDAO;
import lk.ijse.pos.dto.OrderDTO;
import lk.ijse.pos.entity.Customer;
import lk.ijse.pos.entity.Order;
import lk.ijse.pos.util.CrudUtil;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;

public class OrderDAOImpl implements OrderDAO {
    @Override
    public ArrayList<Order> getAll() throws SQLException, ClassNotFoundException {
        return null;
    }

    @Override
    public boolean save(Order entity) throws SQLException, ClassNotFoundException {
        return CrudUtil.execute("INSERT INTO `Order` VALUES(?,?,?,?,?,?)",entity.getOrderID(),entity.getOrderDate(),entity.getOrderTime(),entity.getCustID(),entity.getDiscount(),entity.getTotalCost());
    }

    @Override
    public boolean update(Order entity) throws SQLException, ClassNotFoundException {
        return CrudUtil.execute("UPDATE `Order` SET custID=?,discount=?,totalCost=? WHERE orderID=?", entity.getCustID(),entity.getDiscount(),entity.getTotalCost(),entity.getOrderID());
    }

    @Override
    public boolean delete(String orderId) throws SQLException, ClassNotFoundException {
        return CrudUtil.execute("DELETE FROM `Order` WHERE orderID=?",orderId);
    }

    @Override
    public Order search(String orderId) throws SQLException, ClassNotFoundException {
        ResultSet result = CrudUtil.execute("SELECT * FROM `Order` WHERE orderID=?",orderId);
        if(result.next()){
            return new Order(
                    result.getString(1),
                    LocalDate.parse(result.getString(2)),
                    result.getString(3),
                    result.getString(4),
                    result.getBigDecimal(5),
                    result.getBigDecimal(6)
            );
        }
        return null;
    }

    @Override
    public boolean isExists(String orderId) throws SQLException, ClassNotFoundException {
        ResultSet result = CrudUtil.execute("SELECT orderID FROM `Order` WHERE orderID=?",orderId);
        return result.next();
    }

    @Override
    public String generateNewId() throws SQLException, ClassNotFoundException {
        ResultSet result = CrudUtil.execute("SELECT orderID FROM `Order` ORDER BY orderID DESC LIMIT 1");
        if(result.next()){
            return result.getString(1);
        }
        return null;
    }

    @Override
    public int getAllCount() throws SQLException, ClassNotFoundException {
        ResultSet result = CrudUtil.execute("SELECT COUNT(orderID) FROM `Order`");
        int ordersCount = 0;
        while (result.next()){
            ordersCount = result.getInt(1);
        }
        return ordersCount;
    }

    @Override
    public ArrayList<Order> searchOrdersByCustomerId(String customerId) throws SQLException, ClassNotFoundException{
        ResultSet result = CrudUtil.execute("SELECT * FROM `Order` WHERE custID=?",customerId);
        ArrayList<Order> allOrders = new ArrayList<>();
        while(result.next()){
            allOrders.add(new Order(
                    result.getString(1),
                    LocalDate.parse(result.getString(2)),
                    result.getString(3),
                    result.getString(4),
                    result.getBigDecimal(5),
                    result.getBigDecimal(6)
            ));
        }
        return allOrders;
    }

    @Override
    public double getTotalOrdersCostByCustomerId(String customerId) throws SQLException, ClassNotFoundException {
        ResultSet result = CrudUtil.execute("SELECT SUM(totalCost) FROM `Order` WHERE custID=?",customerId);
        double totalCost = 0.00;
        while(result.next()){
            totalCost+=result.getDouble(1);
        }
        return totalCost;
    }

    @Override
    public double getTotalOrdersCostByOrderDate(LocalDate date) throws SQLException, ClassNotFoundException{
        ResultSet result = CrudUtil.execute("SELECT SUM(totalCost) FROM `Order` WHERE orderDate=?",date);
        double totalCost = 0.00;
        while(result.next()){
            totalCost+=result.getDouble(1);
        }
        return totalCost;
    }

    @Override
    public double getTotalOrdersCostByYearAndMonth(String year,String month) throws SQLException, ClassNotFoundException{
        ResultSet result = CrudUtil.execute("SELECT SUM(totalCost) FROM `Order` WHERE MONTHNAME(orderDate) =? AND year(orderDate)=?",month,year);
        double totalCost = 0.00;
        while(result.next()){
            totalCost+=result.getDouble(1);
        }
        return totalCost;
    }

    @Override
    public double getTotalOrdersCostByYear(String year) throws SQLException, ClassNotFoundException{
        ResultSet result = CrudUtil.execute("SELECT SUM(totalCost) FROM `Order` WHERE year(orderDate)=?",year);
        double totalCost = 0.00;
        while(result.next()){
            totalCost+=result.getDouble(1);
        }
        return totalCost;
    }
}
