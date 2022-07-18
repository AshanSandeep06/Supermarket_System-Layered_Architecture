package lk.ijse.pos.dao.custom.impl;

import lk.ijse.pos.dao.custom.JoinQueryDAO;
import lk.ijse.pos.entity.CustomEntity;
import lk.ijse.pos.util.CrudUtil;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class JoinQueryDAOImpl implements JoinQueryDAO {
    @Override
    public ArrayList<CustomEntity> searchOrderDetailsByOrderId(String orderId) throws SQLException, ClassNotFoundException {
        String sql = "SELECT o.itemCode,i.description,i.packSize,o.unitPrice,o.orderQty,o.discount FROM OrderDetail o INNER JOIN Item i ON o.itemCode=i.itemCode WHERE o.orderID=?";
        ResultSet result = CrudUtil.execute(sql, orderId);
        ArrayList<CustomEntity> arrayList = new ArrayList<>();

        while (result.next()) {
            arrayList.add(new CustomEntity(
                    result.getString(1),
                    result.getString(2),
                    result.getString(3),
                    result.getBigDecimal(4),
                    result.getInt(5),
                    result.getBigDecimal(6)
            ));
        }
        return arrayList;
    }
}
