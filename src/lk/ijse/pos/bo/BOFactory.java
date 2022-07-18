package lk.ijse.pos.bo;

import lk.ijse.pos.bo.custom.ManageOrdersBO;
import lk.ijse.pos.bo.custom.impl.*;
import lk.ijse.pos.dao.custom.impl.CustomerDAOImpl;

public class BOFactory {
    private static BOFactory boFactory;

    private BOFactory(){

    }

    public static BOFactory getBoFactory(){
        return boFactory==null ? boFactory=new BOFactory() : boFactory;
    }

    public enum BOTypes{
        LOGIN, ADMINDASHBOARDBO, CASHIERDASHBOARDBO,MANAGEORDERSBO,CUSTOMER, ITEM, STOCKREPORTBO, INCOMEREPORTSBO
    }

    public SuperBO getBO(BOTypes types){
        switch (types){
            case LOGIN:
                return new LoginBOImpl();
            case ADMINDASHBOARDBO:
                return new AdminDashBoardBOImpl();
            case CASHIERDASHBOARDBO:
                return new CashierDashBoardBOImpl();
            case MANAGEORDERSBO:
                return new ManageOrdersBOImpl();
            case CUSTOMER:
                return new CustomerBOImpl();
            case ITEM:
                return new ItemBOImpl();
            case STOCKREPORTBO:
                return new StockReportBOImpl();
            case INCOMEREPORTSBO:
                return new IncomeReportsBOImpl();
            default:
                return null;
        }
    }
}
