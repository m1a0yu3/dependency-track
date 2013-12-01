/*
 * Copyright 2013 Axway
 *
 * This file is part of OWASP Dependency-Track.
 *
 * Dependency-Track is free software: you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * Dependency-Track is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with Dependency-Track.
 * If not, see http://www.gnu.org/licenses/.
 */

package org.owasp.dependencytrack.service;

import org.owasp.dependencytrack.dao.UserDao;
import org.owasp.dependencytrack.model.Roles;
import org.owasp.dependencytrack.model.Users;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Users: nchitlurnavakiran
 * Date: 8/14/13
 * Time: 11:18 AM
 * To change this template use File | Settings | File Templates.
 */
@Service
public class UserService {
    @Autowired
    private UserDao userDao;

    @Transactional
    public void registerUser(String username,String password, Integer role)
    {
        userDao.registerUser(username,password, role);

    }

    @Transactional
    public String hashpwd(String username, String password)
    {
       return userDao.hashpwd(username, password);
    }

    @Transactional
    public List<Users> accountManagement()
    {
        return userDao.accountManagement();
    }

    @Transactional
      public void validateuser(int userid)
    {
        userDao.validateuser(userid);
    }

    @Transactional
    public void deleteUser(int userid)
    {
        userDao.deleteUser(userid);
    }

    @Transactional
    public List<Roles> getRoleList()
    {
       return userDao.getRoleList();
    }

    @Transactional
    public void changeUserRole(int userid,int role)
    {
        userDao.changeUserRole(userid, role);
    }
}