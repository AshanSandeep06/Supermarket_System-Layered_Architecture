package lk.ijse.pos.bo.custom;

import javafx.collections.ObservableList;
import lk.ijse.pos.bo.SuperBO;
import lk.ijse.pos.dao.DAOFactory;
import lk.ijse.pos.dao.custom.ItemDAO;
import lk.ijse.pos.dto.*;
import lk.ijse.pos.entity.Customer;
import lk.ijse.pos.entity.Item;
import lk.ijse.pos.entity.Order;
import lk.ijse.pos.view.tdm.OrderDetailTM;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanArrayDataSource;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.view.JasperViewer;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public interface ManageOrdersBO extends SuperBO {
    ArrayList<ItemDTO> getAllItems() throws SQLException, ClassNotFoundException;

    ArrayList<OrderDTO> getOrdersByCustomerId(String customerId) throws SQLException, ClassNotFoundException;

    boolean customerIsExists(String id) throws SQLException, ClassNotFoundException;

    ItemDTO searchItem(String itemCode) throws SQLException, ClassNotFoundException;

    ArrayList<CustomDTO> getOrderDetailsByOrderId(String orderId) throws SQLException, ClassNotFoundException;

    OrderDTO searchOrder(String orderId) throws SQLException, ClassNotFoundException;

    boolean orderIsExists(String orderId) throws SQLException, ClassNotFoundException;

    boolean updateOrder(OrderDTO dto) throws SQLException, ClassNotFoundException;

    boolean deleteOrder(String orderId) throws SQLException, ClassNotFoundException;

    CustomerDTO searchCustomer(String customerId) throws SQLException, ClassNotFoundException;
}
