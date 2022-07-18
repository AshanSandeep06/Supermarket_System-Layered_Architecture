package lk.ijse.pos.bo.custom.impl;

import lk.ijse.pos.bo.custom.LoginBO;
import lk.ijse.pos.dao.DAOFactory;
import lk.ijse.pos.dao.custom.LoginDAO;
import lk.ijse.pos.dto.LoginDTO;
import lk.ijse.pos.entity.Login;

import java.sql.SQLException;

public class LoginBOImpl implements LoginBO {
    private final LoginDAO loginDAO = (LoginDAO) DAOFactory.getDaoFactory().getDAO(DAOFactory.DAOTypes.LOGIN);

    @Override
    public LoginDTO getLoginAccess(String userName, String password) throws SQLException, ClassNotFoundException {
        Login entity = loginDAO.getLogin(userName, password);
        if(entity!=null){
            return new LoginDTO(entity.getPassword(),entity.getUserName(),entity.getJobRole());
        }
        return null;
    }
}
