package lk.ijse.pos.dao.custom.impl;

import lk.ijse.pos.dao.custom.LoginDAO;
import lk.ijse.pos.dto.LoginDTO;
import lk.ijse.pos.entity.Login;
import lk.ijse.pos.util.CrudUtil;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class LoginDAOImpl implements LoginDAO {
    @Override
    public Login getLogin(String userName, String password) throws SQLException, ClassNotFoundException {
        ResultSet result = CrudUtil.execute("SELECT * FROM Login WHERE userName=? AND password=?", userName, password);
        if (result.next()) {
            return new Login(result.getString(1), result.getString(2), result.getString(3));
        }
        return null;
    }

    @Override
    public ArrayList<Login> getAll() throws SQLException, ClassNotFoundException {
        return null;
    }

    @Override
    public boolean save(Login entity) throws SQLException, ClassNotFoundException {
        return false;
    }

    @Override
    public boolean update(Login entity) throws SQLException, ClassNotFoundException {
        return false;
    }

    @Override
    public boolean delete(String s) throws SQLException, ClassNotFoundException {
        return false;
    }

    @Override
    public Login search(String s) throws SQLException, ClassNotFoundException {
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
}
