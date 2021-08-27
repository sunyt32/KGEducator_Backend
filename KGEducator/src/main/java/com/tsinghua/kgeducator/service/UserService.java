package com.tsinghua.kgeducator.service;


import com.alibaba.fastjson.JSON;
import com.tsinghua.kgeducator.entity.User;
import com.tsinghua.kgeducator.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService
{
    UserMapper userMapper;
    static final String[] subjectList = {"chinese", "english", "math", "physics", "chemistry", "biology", "history", "geo", "politics"};
    public UserService(UserMapper userMapper)
    {
        this.userMapper = userMapper;
    }
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

    public Integer assembleSubject(String subjects)
    {
        int subjectMap = 0;
        List<String> userSubjectList = JSON.parseArray(subjects, String.class);
        for(String userSubject : userSubjectList)
        {
            for(int i = 0; i < subjectList.length; i++)
            {
                if(userSubject.equals(subjectList[i]))
                {
                    subjectMap |= (1 << i);
                }
            }
        }
        return subjectMap;
    }

    public List<String> disassembleSubject(int subjectMap)
    {
        List<String> userSubjectList = new ArrayList<>();
        for(int i = 0; i < subjectList.length; i++)
        {
            if(((subjectMap >> i) & 1) == 1)
            {
                userSubjectList.add(subjectList[i]);
            }
        }
        return userSubjectList;
    }
}