package com.example.layeredarchitecture.dao;

import com.example.layeredarchitecture.db.DBConnection;
import com.example.layeredarchitecture.model.OrderDetailDTO;

import java.sql.*;
import java.time.LocalDate;
import java.util.List;

public interface OrderDAO {
    String generateNewOrderId() throws SQLException, ClassNotFoundException;
    boolean findExistance(String id) throws SQLException, ClassNotFoundException;
    boolean saveOrder(String orderId, LocalDate orderDate, String customerId, List<OrderDetailDTO> orderDetails) throws SQLException, ClassNotFoundException;
}
