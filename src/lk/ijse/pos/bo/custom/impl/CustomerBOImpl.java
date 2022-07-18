package lk.ijse.pos.bo.custom.impl;

import lk.ijse.pos.bo.custom.CustomerBO;
import lk.ijse.pos.dao.DAOFactory;
import lk.ijse.pos.dao.custom.CustomerDAO;
import lk.ijse.pos.dto.CustomerDTO;
import lk.ijse.pos.entity.Customer;

import java.sql.SQLException;
import java.util.ArrayList;

public class CustomerBOImpl implements CustomerBO {
    //Property Injection (DI)
    private final CustomerDAO customerDAO = (CustomerDAO) DAOFactory.getDaoFactory().getDAO(DAOFactory.DAOTypes.CUSTOMER);

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
    public String generateNewCustomerId() throws SQLException, ClassNotFoundException {
        String customerId = customerDAO.generateNewId();
        String finalId = "C001";

        if (customerId != null) {

            String[] splitString = customerId.split("C");
            int id = Integer.parseInt(splitString[1]);
            id++;

            if (id < 10) {
                finalId = "C00" + id;
            } else if (id < 100) {
                finalId = "C0" + id;
            } else {
                finalId = "C" + id;
            }
            return finalId;
        } else {
            return finalId;
        }
    }

    @Override
    public boolean customerIsExists(String id) throws SQLException, ClassNotFoundException {
        return customerDAO.isExists(id);
    }

    @Override
    public boolean saveCustomer(CustomerDTO dto) throws SQLException, ClassNotFoundException {
        return customerDAO.save(new Customer(
                dto.getCustID(),
                dto.getCustTitle(),
                dto.getCustName(),
                dto.getCustAddress(),
                dto.getCity(),
                dto.getProvince(),
                dto.getPostalCode(),
                dto.getMobileNumber()
        ));
    }

    @Override
    public boolean updateCustomer(CustomerDTO dto) throws SQLException, ClassNotFoundException {
        return customerDAO.update(new Customer(
                dto.getCustID(),
                dto.getCustTitle(),
                dto.getCustName(),
                dto.getCustAddress(),
                dto.getCity(),
                dto.getProvince(),
                dto.getPostalCode(),
                dto.getMobileNumber()
        ));
    }

    @Override
    public boolean deleteCustomer(String id) throws SQLException, ClassNotFoundException {
        return customerDAO.delete(id);
    }

    @Override
    public CustomerDTO searchCustomer(String id) throws SQLException, ClassNotFoundException {
        Customer entity = customerDAO.search(id);
        if(entity!=null){
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
}
