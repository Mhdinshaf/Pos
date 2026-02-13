package org.Pos.repository.impl;
import org.Pos.db.DbConnection;
import org.Pos.repository.UserLoginRepo;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import  java.sql.Connection;

public class UserLoginImplRepo implements UserLoginRepo {

    @Override
    public boolean checkCredential(String email, String password) throws SQLException, ClassNotFoundException {
        Connection connection = DbConnection.getInstance().getConnection();

        String sql = "SELECT * FROM users WHERE email = ? AND password = ?";

        PreparedStatement pstm = connection.prepareStatement(sql);
        pstm.setString(1, email);
        pstm.setString(2, password);

        ResultSet resultSet = pstm.executeQuery();


        return resultSet.next();
    }
}
