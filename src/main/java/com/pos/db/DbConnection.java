package com.pos.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class DbConnection {
    private static DbConnection dbConnection;
    private Connection connection;

    private DbConnection() throws SQLException, ClassNotFoundException {
        try {
            connection= DriverManager.getConnection("jdbc:mysql://localhost:3306/clothify_store","root","");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public static DbConnection getInstance() throws SQLException, ClassNotFoundException {
        if (dbConnection == null){
            dbConnection = new DbConnection();
        }
        return dbConnection;
    }

    public Connection getConnection() {
        return connection;
    }



}
