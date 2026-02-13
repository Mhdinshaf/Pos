package org.Pos.service.impl;

import org.Pos.repository.UserLoginRepo;
import org.Pos.repository.impl.UserLoginImplRepo;
import org.Pos.service.AdminLoginSer;

import java.sql.SQLException;

public class AdminLoginImpl implements AdminLoginSer {
    private final UserLoginRepo userLoginRepo=new UserLoginImplRepo();

    @Override
    public boolean checkCredential(String email, String password) throws SQLException, ClassNotFoundException {
        return userLoginRepo.checkCredential(email, password);
    }
}
