package com.example.layeredarchitecture.dao;

import com.example.layeredarchitecture.db.DBConnection;
import com.example.layeredarchitecture.model.CustomerDTO;
import javafx.scene.control.Alert;

import java.sql.*;
import java.util.ArrayList;

public class CustomerDAOImpl implements CustomerDAO {

    @Override
    public ArrayList<CustomerDTO> getAllCustomers() throws SQLException, ClassNotFoundException {

        Connection connection = DBConnection.getDbConnection().getConnection();
        Statement stm = connection.createStatement();
        ResultSet rst = stm.executeQuery("SELECT * FROM Customer");
        ArrayList<CustomerDTO> getAllCustomer = new ArrayList<>();

        while(rst.next()){
            CustomerDTO customerDTO = new CustomerDTO(rst.getString("id"), rst.getString("name"), rst.getString("address"));
            getAllCustomer.add(customerDTO);
        }

        return getAllCustomer;
    }

    @Override
    public boolean saveCustomer(CustomerDTO dto) throws SQLException, ClassNotFoundException {

        Connection connection = DBConnection.getDbConnection().getConnection();
        PreparedStatement pstm = connection.prepareStatement("INSERT INTO Customer (id,name, address) VALUES (?,?,?)");
        pstm.setString(1, dto.getId());
        pstm.setString(2, dto.getName());
        pstm.setString(3, dto.getAddress());

        return pstm.executeUpdate()>0;
    }

    @Override
    public boolean updateCustomer(CustomerDTO dto) throws SQLException, ClassNotFoundException {

        Connection connection = DBConnection.getDbConnection().getConnection();
        PreparedStatement pstm = connection.prepareStatement("UPDATE Customer SET name=?, address=? WHERE id=?");
        pstm.setString(1, dto.getName());
        pstm.setString(2, dto.getAddress());
        pstm.setString(3, dto.getId());

        return pstm.executeUpdate()>0;
    }

    @Override
    public boolean findExistence(String id) throws SQLException, ClassNotFoundException {

        Connection connection = DBConnection.getDbConnection().getConnection();
        PreparedStatement pstm = connection.prepareStatement("SELECT id FROM Customer WHERE id=?");
        pstm.setString(1, id);

        return pstm.executeQuery().next();

    }

    @Override
    public boolean deleteCustomer(String id) throws SQLException, ClassNotFoundException {

        Connection connection = DBConnection.getDbConnection().getConnection();
        PreparedStatement pstm = connection.prepareStatement("DELETE FROM Customer WHERE id=?");
        pstm.setString(1, id);

        return pstm.executeUpdate()>0;

    }

    @Override
    public String generateNewId() throws SQLException, ClassNotFoundException {

        Connection connection = DBConnection.getDbConnection().getConnection();
        ResultSet rst = connection.createStatement().executeQuery("SELECT id FROM Customer ORDER BY id DESC LIMIT 1;");
        if (rst.next()) {
            String id = rst.getString("id");
            int newCustomerId = Integer.parseInt(id.replace("C00-", "")) + 1;
            return String.format("C00-%03d", newCustomerId);
        } else {
            return "C00-001";
        }
    }

    @Override
    public CustomerDTO findById(String newValue) throws SQLException, ClassNotFoundException {

        Connection connection = DBConnection.getDbConnection().getConnection();

            if (!existCustomer(newValue + "")) {
//                            "There is no such customer associated with the id " + id
                new Alert(Alert.AlertType.ERROR, "There is no such customer associated with the id " + newValue + "").show();
            }

            PreparedStatement pstm = connection.prepareStatement("SELECT * FROM Customer WHERE id=?");
            pstm.setString(1, newValue + "");
            ResultSet rst = pstm.executeQuery();
            rst.next();
            CustomerDTO customerDTO = new CustomerDTO(newValue + "", rst.getString("name"), rst.getString("address"));

            return customerDTO;

    }

    @Override
    public boolean existCustomer(String id) throws SQLException, ClassNotFoundException {
        Connection connection = DBConnection.getDbConnection().getConnection();
        PreparedStatement pstm = connection.prepareStatement("SELECT id FROM Customer WHERE id=?");
        pstm.setString(1, id);
        return pstm.executeQuery().next();
    }
}
