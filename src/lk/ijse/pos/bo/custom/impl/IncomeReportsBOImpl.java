package lk.ijse.pos.bo.custom.impl;

import lk.ijse.pos.bo.custom.IncomeReportsBO;
import lk.ijse.pos.dao.DAOFactory;
import lk.ijse.pos.dao.custom.CustomerDAO;
import lk.ijse.pos.dao.custom.OrderDAO;
import lk.ijse.pos.dto.CustomerDTO;
import lk.ijse.pos.dto.OrderDTO;
import lk.ijse.pos.entity.Customer;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;

public class IncomeReportsBOImpl implements IncomeReportsBO {

    CustomerDAO customerDAO = (CustomerDAO) DAOFactory.getDaoFactory().getDAO(DAOFactory.DAOTypes.CUSTOMER);
    OrderDAO orderDAO = (OrderDAO) DAOFactory.getDaoFactory().getDAO(DAOFactory.DAOTypes.ORDER);

    @Override
    public ArrayList<CustomerDTO> getAllCustomers() throws SQLException, ClassNotFoundException {
        ArrayList<CustomerDTO> allCustomers=new ArrayList<>();
        for (Customer c1 : customerDAO.getAll()) {
            allCustomers.add(new CustomerDTO(
                    c1.getCustID(),
                    c1.getCustTitle(),
                    c1.getCustName(),
                    c1.getCustAddress(),
                    c1.getCity(),
                    c1.getProvince(),
                    c1.getPostalCode(),
                    c1.getMobileNumber()
            ));
        }
        return allCustomers;
    }

    @Override
    public double getTotalOrdersCostByCustomerId(String customerId) throws SQLException, ClassNotFoundException {
        return orderDAO.getTotalOrdersCostByCustomerId(customerId);
    }

    @Override
    public double getTotalOrdersCostByDay(String day) throws SQLException, ClassNotFoundException {
        LocalDate date = LocalDate.parse(day);
        return orderDAO.getTotalOrdersCostByOrderDate(date);
    }

    @Override
    public double getTotalOrdersCostByYearAndMonth(String year, String month) throws SQLException, ClassNotFoundException{
        return orderDAO.getTotalOrdersCostByYearAndMonth(year,month);
    }

    @Override
    public double getTotalOrdersCostByYear(String year) throws SQLException, ClassNotFoundException{
        return orderDAO.getTotalOrdersCostByYear(year);
    }
}
