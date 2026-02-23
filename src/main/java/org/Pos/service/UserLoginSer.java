package org.Pos.service;

import java.sql.SQLException;

public interface UserLoginSer {
    boolean checkCredential(String email, String password) throws SQLException;
    String getUserRole(String email) throws SQLException;
}
