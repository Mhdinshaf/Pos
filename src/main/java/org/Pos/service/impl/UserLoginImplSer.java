package org.Pos.service.impl;

import org.Pos.repository.UserLoginRepo;
import org.Pos.repository.impl.UserLoginImplRepo;
import org.Pos.service.UserLoginSer;

import java.sql.SQLException;

public class UserLoginImplSer implements UserLoginSer {
    private UserLoginRepo userLogin=new UserLoginImplRepo();


    @Override
    public boolean checkCredential(String email, String password) throws SQLException, ClassNotFoundException {
        return userLogin.checkCredential(email, password);
    }
}
