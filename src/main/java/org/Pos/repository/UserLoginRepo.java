package org.Pos.repository;

import java.sql.SQLException;

public interface UserLoginRepo {
    boolean checkCredential(String email, String password) throws SQLException, ClassNotFoundException;
}
