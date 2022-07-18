package lk.ijse.pos.dao.custom;

import lk.ijse.pos.dao.SuperDAO;
import lk.ijse.pos.entity.CustomEntity;

import java.sql.SQLException;
import java.util.ArrayList;

public interface JoinQueryDAO extends SuperDAO {
    ArrayList<CustomEntity> searchOrderDetailsByOrderId(String orderId) throws SQLException, ClassNotFoundException;
}
