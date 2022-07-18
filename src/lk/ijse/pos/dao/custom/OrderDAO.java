package lk.ijse.pos.dao.custom;

import lk.ijse.pos.dao.CrudDAO;
import lk.ijse.pos.dto.OrderDTO;
import lk.ijse.pos.entity.Order;
import lk.ijse.pos.util.CrudUtil;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;

public interface OrderDAO extends CrudDAO<Order, String> {
    ArrayList<Order> searchOrdersByCustomerId(String customerId) throws SQLException, ClassNotFoundException;

    double getTotalOrdersCostByCustomerId(String customerId) throws SQLException, ClassNotFoundException;

    double getTotalOrdersCostByOrderDate(LocalDate date) throws SQLException, ClassNotFoundException;

    double getTotalOrdersCostByYearAndMonth(String year, String month) throws SQLException, ClassNotFoundException;

    double getTotalOrdersCostByYear(String year) throws SQLException, ClassNotFoundException;
}
