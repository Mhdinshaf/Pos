package org.Pos.service.impl;

import org.Pos.Model.dto.UserDto;
import org.Pos.repository.UserLoginRepo;
import org.Pos.repository.impl.UserLoginImplRepo;
import org.Pos.service.UserLoginSer;

import java.sql.SQLException;

public class UserLoginImplSer implements UserLoginSer {
    private UserLoginRepo userLogin=new UserLoginImplRepo();


    @Override
    public boolean checkCredential(String email, String password) throws SQLException{
        return userLogin.checkCredential(email, password);
    }

    @Override
    public String getUserRole(String email) throws SQLException{
        return userLogin.getUserRole(email);
    }
}
