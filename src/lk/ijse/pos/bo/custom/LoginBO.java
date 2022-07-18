package lk.ijse.pos.bo.custom;

import lk.ijse.pos.bo.SuperBO;
import lk.ijse.pos.dto.LoginDTO;

import java.sql.SQLException;

public interface LoginBO extends SuperBO {
    LoginDTO getLoginAccess(String userName, String password) throws SQLException, ClassNotFoundException;
}
