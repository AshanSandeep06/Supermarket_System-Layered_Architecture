package lk.ijse.pos.dao;

import lk.ijse.pos.dao.custom.CustomerDAO;
import lk.ijse.pos.dao.custom.ItemDAO;
import lk.ijse.pos.dao.custom.OrderDAO;
import lk.ijse.pos.dao.custom.impl.*;

public class DAOFactory {
    private static DAOFactory daoFactory;

    private DAOFactory(){

    }

    public static DAOFactory getDaoFactory(){
        return daoFactory==null ? daoFactory=new DAOFactory() : daoFactory;
    }

    public enum DAOTypes{
        LOGIN, CUSTOMER, ITEM, ORDER, ORDERDETAIL, JOINQUERYDAO
    }

    // This Method for hide the object creation logic and return what user wants
    public SuperDAO getDAO(DAOTypes types){
        switch (types){
            case LOGIN:
                return new LoginDAOImpl(); // SuperDAO superDAO=new LoginDAOImpl();
                                            // return superDAO;
            case CUSTOMER:
                return new CustomerDAOImpl(); // SuperDAO superDAO=new CustomerDAOImpl();
            case ITEM:
                return new ItemDAOImpl();  // SuperDAO superDAO=new ItemDAOImpl();
            case ORDER:
                return new OrderDAOImpl();  // SuperDAO superDAO=new OrderDAOImpl();
            case ORDERDETAIL:
                return new OrderDetailDAOImpl();  // SuperDAO superDAO=new OrderDetailDAOImpl();
            case JOINQUERYDAO:
                return new JoinQueryDAOImpl();  // SuperDAO superDAO=new JoinQueryDAOImpl();
            default:
                return null;
        }
    }
}
