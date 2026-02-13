package org.Pos.service;

import java.sql.SQLException;

public interface AdminLoginSer {
    boolean checkCredential(String email, String password) throws SQLException, ClassNotFoundException;
}
