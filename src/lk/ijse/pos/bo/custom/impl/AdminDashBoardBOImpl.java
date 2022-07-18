package lk.ijse.pos.bo.custom.impl;

import lk.ijse.pos.bo.custom.AdminDashBoardBO;
import lk.ijse.pos.dao.DAOFactory;
import lk.ijse.pos.dao.custom.CustomerDAO;
import lk.ijse.pos.dao.custom.ItemDAO;
import lk.ijse.pos.dao.custom.OrderDAO;
import lk.ijse.pos.dao.custom.impl.CustomerDAOImpl;
import lk.ijse.pos.dao.custom.impl.ItemDAOImpl;
import lk.ijse.pos.dao.custom.impl.OrderDAOImpl;

import java.sql.SQLException;

public class AdminDashBoardBOImpl implements AdminDashBoardBO {
    //Property Injection (DI)
    private final OrderDAO orderDAO = (OrderDAO) DAOFactory.getDaoFactory().getDAO(DAOFactory.DAOTypes.ORDER);
    private final CustomerDAO customerDAO = (CustomerDAO) DAOFactory.getDaoFactory().getDAO(DAOFactory.DAOTypes.CUSTOMER);
    private final ItemDAO itemDAO = (ItemDAO) DAOFactory.getDaoFactory().getDAO(DAOFactory.DAOTypes.ITEM);

    @Override
    public int getAllOrdersCount() throws SQLException, ClassNotFoundException {
        return orderDAO.getAllCount();
    }

    @Override
    public int getAllCustomersCount() throws SQLException, ClassNotFoundException {
        return customerDAO.getAllCount();
    }

    @Override
    public int getAllItemsCount() throws SQLException, ClassNotFoundException {
        return itemDAO.getAllCount();
    }
}
