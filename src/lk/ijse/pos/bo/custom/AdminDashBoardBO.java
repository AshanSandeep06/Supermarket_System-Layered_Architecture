package lk.ijse.pos.bo.custom;

import lk.ijse.pos.bo.SuperBO;

import java.sql.SQLException;

public interface AdminDashBoardBO extends SuperBO {
    int getAllOrdersCount() throws SQLException, ClassNotFoundException;

    int getAllCustomersCount() throws SQLException, ClassNotFoundException;

    int getAllItemsCount() throws SQLException, ClassNotFoundException;
}
