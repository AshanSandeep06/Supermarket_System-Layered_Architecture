package lk.ijse.pos.dao.custom.impl;

import lk.ijse.pos.dao.custom.OrderDetailDAO;
import lk.ijse.pos.dto.OrderDetailDTO;
import lk.ijse.pos.entity.Customer;
import lk.ijse.pos.entity.OrderDetail;
import lk.ijse.pos.util.CrudUtil;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class OrderDetailDAOImpl implements OrderDetailDAO {

    @Override
    public ArrayList<OrderDetail> getAll() throws SQLException, ClassNotFoundException {
        return null;
    }

    @Override
    public boolean save(OrderDetail entity) throws SQLException, ClassNotFoundException {
        return CrudUtil.execute("INSERT INTO OrderDetail VALUES(?,?,?,?,?,?)",entity.getOrderID(),entity.getItemCode(),entity.getDescription(),entity.getUnitPrice(),entity.getOrderQty(),entity.getDiscount());
    }

    @Override
    public boolean update(OrderDetail entity) throws SQLException, ClassNotFoundException {
        return false;
    }

    @Override
    public boolean delete(String orderId) throws SQLException, ClassNotFoundException {
        return CrudUtil.execute("DELETE FROM OrderDetail WHERE orderID=?",orderId);
    }

    @Override
    public OrderDetail search(String orderId) throws SQLException, ClassNotFoundException {
        return null;
    }

    @Override
    public boolean isExists(String s) throws SQLException, ClassNotFoundException {
        return false;
    }

    @Override
    public String generateNewId() throws SQLException, ClassNotFoundException {
        return null;
    }

    @Override
    public int getAllCount() throws SQLException, ClassNotFoundException {
        return 0;
    }

    @Override
    public ArrayList<OrderDetail> getAllOrderDetailsByOrderId(String orderId) throws SQLException, ClassNotFoundException{
        ArrayList<OrderDetail> orderDetails = new ArrayList<>();
        ResultSet result = CrudUtil.execute("SELECT * FROM OrderDetail WHERE orderID=?",orderId);
        while(result.next()){
            orderDetails.add(new OrderDetail(
                    result.getString(1),
                    result.getString(2),
                    result.getString(3),
                    result.getBigDecimal(4),
                    result.getInt(5),
                    result.getBigDecimal(6)
            ));
        }
        return orderDetails;
    }
}
