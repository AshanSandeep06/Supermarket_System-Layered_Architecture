package lk.ijse.pos.bo.custom;

import lk.ijse.pos.bo.SuperBO;
import lk.ijse.pos.dto.CustomerDTO;
import lk.ijse.pos.dto.ItemDTO;
import lk.ijse.pos.dto.OrderDTO;
import lk.ijse.pos.entity.Customer;
import lk.ijse.pos.entity.Item;

import java.sql.SQLException;
import java.util.ArrayList;

public interface CashierDashBoardBO extends SuperBO {
    CustomerDTO getCustomerByMobileNumber(String id) throws SQLException, ClassNotFoundException;

    ArrayList<CustomerDTO> getAllCustomers() throws SQLException, ClassNotFoundException;

    ArrayList<ItemDTO> getAllItems() throws SQLException, ClassNotFoundException;

    boolean customerIsExists(String id) throws SQLException, ClassNotFoundException;

    boolean itemIsExists(String id) throws SQLException, ClassNotFoundException;

    String generateNewOrderId() throws SQLException, ClassNotFoundException;

    ItemDTO searchItem(String itemCode) throws SQLException, ClassNotFoundException;

    boolean orderIsExists(String orderId) throws SQLException, ClassNotFoundException;

    boolean placeOrder(OrderDTO dto) throws SQLException, ClassNotFoundException;
}
