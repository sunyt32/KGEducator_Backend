package com.tsinghua.kgeducator.service;


import com.tsinghua.kgeducator.entity.User;
import com.tsinghua.kgeducator.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService
{
    @Autowired
    UserMapper userMapper;
    public List<User> getAllUsers()
    {
        return userMapper.getAllUsers();
    }

    public User getUserById(Integer id)
    {
        return userMapper.getUserById(id);
    }

    public User getUserByEmail(String email)
    {
        return userMapper.getUserByEmail(email);
    }

    public List<User> getUsersByName(String name)
    {
        return userMapper.getUsersByName(name);
    }

    public Integer addUser(User user)
    {
        return userMapper.addUser(user);
    }

    public Integer updateUserById(User user)
    {
        return userMapper.updateUserById(user);
    }

    public Integer deleteUserById(Integer id)
    {
        return userMapper.deleteUserById(id);
    }

    public void deleteAllUsers()
    {
        userMapper.deleteAllUsers();
    }
}