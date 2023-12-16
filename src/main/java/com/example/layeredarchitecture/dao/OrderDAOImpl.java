package com.example.layeredarchitecture.dao;

import com.example.layeredarchitecture.db.DBConnection;
import com.example.layeredarchitecture.model.ItemDTO;
import com.example.layeredarchitecture.model.OrderDetailDTO;

import java.sql.*;
import java.time.LocalDate;
import java.util.List;

public class OrderDAOImpl implements OrderDAO{

    OrderDetailDAO orderDetailDAO = new OrderDetailDAOImpl();

    @Override
    public String generateNewOrderId() throws SQLException, ClassNotFoundException {

        Connection connection = DBConnection.getDbConnection().getConnection();
        Statement stm = connection.createStatement();
        ResultSet rst = stm.executeQuery("SELECT oid FROM `Orders` ORDER BY oid DESC LIMIT 1;");

        return rst.next() ? String.format("OID-%03d", (Integer.parseInt(rst.getString("oid").replace("OID-", "")) + 1)) : "OID-001";

    }

    @Override
    public boolean findExistance(String id) throws SQLException, ClassNotFoundException {

        Connection connection = DBConnection.getDbConnection().getConnection();
        PreparedStatement stm = connection.prepareStatement("SELECT oid FROM `Orders` WHERE oid=?");
        stm.setString(1, id);

        //if order id already exist
        if (stm.executeQuery().next()) {
            return true;
        }

        return false;
    }

    @Override
    public boolean saveOrder(String orderId, LocalDate orderDate, String customerId, List<OrderDetailDTO> orderDetails) throws SQLException, ClassNotFoundException {

        Connection connection = DBConnection.getDbConnection().getConnection();
        PreparedStatement stm;

        connection.setAutoCommit(false);
        stm = connection.prepareStatement("INSERT INTO `Orders` (oid, date, customerID) VALUES (?,?,?)");
        stm.setString(1, orderId);
        stm.setDate(2, Date.valueOf(orderDate));
        stm.setString(3, customerId);

        if (stm.executeUpdate() != 1) {
            connection.rollback();
            connection.setAutoCommit(true);
            return false;
        }

        boolean isSaved = orderDetailDAO.saveOrderDetail(orderId,orderDetails);

        if (isSaved){
            connection.commit();
            connection.setAutoCommit(true);
            return true;
        }

        return false;

        /*stm = connection.prepareStatement("INSERT INTO OrderDetails (oid, itemCode, unitPrice, qty) VALUES (?,?,?,?)");

        for (OrderDetailDTO detail : orderDetails) {
            stm.setString(1, orderId);
            stm.setString(2, detail.getItemCode());
            stm.setBigDecimal(3, detail.getUnitPrice());
            stm.setInt(4, detail.getQty());

            if (stm.executeUpdate() != 1) {
                connection.rollback();
                connection.setAutoCommit(true);
                return false;
            }*/

//                //Search & Update Item
            /*ItemDTO item = findItem(detail.getItemCode());
            item.setQtyOnHand(item.getQtyOnHand() - detail.getQty());

            PreparedStatement pstm = connection.prepareStatement("UPDATE Item SET description=?, unitPrice=?, qtyOnHand=? WHERE code=?");
            pstm.setString(1, item.getDescription());
            pstm.setBigDecimal(2, item.getUnitPrice());
            pstm.setInt(3, item.getQtyOnHand());
            pstm.setString(4, item.getCode());

            if (!(pstm.executeUpdate() > 0)) {
                connection.rollback();
                connection.setAutoCommit(true);
                return false;
            }*/
    }

}
