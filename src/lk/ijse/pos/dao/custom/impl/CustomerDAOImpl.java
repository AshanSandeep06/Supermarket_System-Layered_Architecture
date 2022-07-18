package lk.ijse.pos.dao.custom.impl;

import lk.ijse.pos.dao.custom.CustomerDAO;
import lk.ijse.pos.dto.CustomerDTO;
import lk.ijse.pos.entity.Customer;
import lk.ijse.pos.util.CrudUtil;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class CustomerDAOImpl implements CustomerDAO {
    @Override
    public ArrayList<Customer> getAll() throws SQLException, ClassNotFoundException {
        ResultSet result = CrudUtil.execute("SELECT * FROM Customer");
        ArrayList<Customer> allCustomers = new ArrayList<>();
        while(result.next()){
            allCustomers.add(new Customer(
                    result.getString(1),
                    result.getString(2),
                    result.getString(3),
                    result.getString(4),
                    result.getString(5),
                    result.getString(6),
                    result.getString(7),
                    result.getString(8)
            ));
        }
        return allCustomers;
    }

    @Override
    public boolean save(Customer entity) throws SQLException, ClassNotFoundException {
        return CrudUtil.execute("INSERT INTO Customer VALUES (?,?,?,?,?,?,?,?)",entity.getCustID(),entity.getCustTitle(),entity.getCustName(),entity.getCustAddress(),entity.getCity(),entity.getProvince(),entity.getPostalCode(),entity.getMobileNumber());
    }

    @Override
    public boolean update(Customer entity) throws SQLException, ClassNotFoundException {
        return CrudUtil.execute("UPDATE Customer SET custTitle=?,custName=?,custAddress=?,city=?,province=?,postalCode=?,mobileNumber=? WHERE custID=?", entity.getCustTitle(),entity.getCustName(),entity.getCustAddress(),entity.getCity(),entity.getProvince(),entity.getPostalCode(),entity.getMobileNumber(),entity.getCustID());
    }

    @Override
    public boolean delete(String id) throws SQLException, ClassNotFoundException {
        return CrudUtil.execute("DELETE FROM Customer WHERE custID=?",id);
    }

    @Override
    public Customer search(String id) throws SQLException, ClassNotFoundException {
        ResultSet result = CrudUtil.execute("SELECT * FROM Customer WHERE custID=?",id);
        if(result.next()){
            return new Customer(
                    result.getString(1),
                    result.getString(2),
                    result.getString(3),
                    result.getString(4),
                    result.getString(5),
                    result.getString(6),
                    result.getString(7),
                    result.getString(8)
            );
        }
        return null;
    }

    @Override
    public boolean isExists(String customerId) throws SQLException, ClassNotFoundException {
        ResultSet result = CrudUtil.execute("SELECT custID FROM Customer WHERE custID=?",customerId);
        return result.next();
    }

    @Override
    public String generateNewId() throws SQLException, ClassNotFoundException {
        ResultSet result = CrudUtil.execute("SELECT custID FROM Customer ORDER BY custID DESC LIMIT 1");
        if(result.next()){
            return result.getString(1);
        }
        return null;
    }

    @Override
    public int getAllCount() throws SQLException, ClassNotFoundException {
        ResultSet result = CrudUtil.execute("SELECT COUNT(custID) FROM Customer");
        int customersCount = 0;
        while (result.next()){
            customersCount = result.getInt(1);
        }
        return customersCount;
    }

    @Override
    public Customer searchCustomerByMobileNumber(String mobileNumber) throws SQLException, ClassNotFoundException {
        ResultSet result = CrudUtil.execute("SELECT * FROM Customer WHERE mobileNumber=?",mobileNumber);
        if(result.next()){
            return new Customer(
                    result.getString(1),
                    result.getString(2),
                    result.getString(3),
                    result.getString(4),
                    result.getString(5),
                    result.getString(6),
                    result.getString(7),
                    result.getString(8)
            );
        }
        return null;
    }
}
