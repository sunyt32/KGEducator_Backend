package com.tsinghua.kgeducator.mapper;

import com.tsinghua.kgeducator.entity.User;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface UserMapper
{
    List<User> getAllUsers();

    User getUserById(Integer id);

    User getUserByEmail(String email);

    Integer addUser(User user);

    Integer updateUserById(User user);

    Integer deleteUserById(Integer id);

    void deleteAllUsers();
}