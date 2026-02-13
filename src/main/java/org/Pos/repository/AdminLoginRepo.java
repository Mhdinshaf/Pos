package org.Pos.repository;

import java.sql.SQLException;

public interface AdminLoginRepo {
    boolean checkCredential(String email, String password) throws SQLException, ClassNotFoundException;
}
