package lk.ijse.pos.bo.custom;

import lk.ijse.pos.bo.SuperBO;
import lk.ijse.pos.dto.CustomerDTO;
import lk.ijse.pos.entity.Customer;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;

public interface IncomeReportsBO extends SuperBO {
    ArrayList<CustomerDTO> getAllCustomers() throws SQLException, ClassNotFoundException;

    double getTotalOrdersCostByCustomerId(String customerId) throws SQLException, ClassNotFoundException;

    double getTotalOrdersCostByDay(String day) throws SQLException, ClassNotFoundException;

    double getTotalOrdersCostByYearAndMonth(String year, String month) throws SQLException, ClassNotFoundException;

    double getTotalOrdersCostByYear(String year) throws SQLException, ClassNotFoundException;
}
