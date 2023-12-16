package com.example.layeredarchitecture.dao;

import com.example.layeredarchitecture.db.DBConnection;
import com.example.layeredarchitecture.model.ItemDTO;
import com.example.layeredarchitecture.model.OrderDetailDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class OrderDetailDAOImpl implements OrderDetailDAO{

    ItemDAO itemDAO = new ItemDAOImpl();

    @Override
    public boolean saveOrderDetail(String orderId, List<OrderDetailDTO> orderDetails) throws SQLException, ClassNotFoundException {

        Connection connection = DBConnection.getDbConnection().getConnection();
        PreparedStatement stm;
        stm = connection.prepareStatement("INSERT INTO OrderDetails (oid, itemCode, unitPrice, qty) VALUES (?,?,?,?)");

        for (OrderDetailDTO detail : orderDetails) {
            stm.setString(1, orderId);
            stm.setString(2, detail.getItemCode());
            stm.setBigDecimal(3, detail.getUnitPrice());
            stm.setInt(4, detail.getQty());

            if (stm.executeUpdate() != 1) {
                connection.rollback();
                connection.setAutoCommit(true);
                return false;
            }

            ItemDTO item = itemDAO.findItemByCode(detail.getItemCode());
            item.setQtyOnHand(item.getQtyOnHand() - detail.getQty());

            boolean isUpdate = itemDAO.updateItem(item);

            /*PreparedStatement pstm = connection.prepareStatement("UPDATE Item SET description=?, unitPrice=?, qtyOnHand=? WHERE code=?");
            pstm.setString(1, item.getDescription());
            pstm.setBigDecimal(2, item.getUnitPrice());
            pstm.setInt(3, item.getQtyOnHand());
            pstm.setString(4, item.getCode());*/

            if (!isUpdate) {
                connection.rollback();
                connection.setAutoCommit(true);
                return false;
            }

        }
        return true;
    }

}
