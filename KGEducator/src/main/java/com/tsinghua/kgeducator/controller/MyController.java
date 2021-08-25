package com.tsinghua.kgeducator.controller;

import com.alibaba.fastjson.*;
import com.tsinghua.kgeducator.entity.User;
import com.tsinghua.kgeducator.service.UserService;
import com.tsinghua.kgeducator.tool.Token;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Controller
public class MyController
{
    @Autowired
    private UserService userService;
    static final String emailRegex = "^[0-9a-zA-Z]+\\w*@([0-9a-z]+\\.)*+[0-9a-z]+$";
    static final String[] subjectList = {"chinese", "english", "math", "physics", "chemistry", "biology", "history", "geo", "politics"};
    Map<String,Object> map;

    private User getUserByToken(String token)
    {
        Integer id = Token.getUserId(token);
        User user = userService.getUserById(id);
        return user;
    }
    @RequestMapping ( "/login" )
    @ResponseBody
    public String login(HttpServletRequest request) {
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        User user = userService.getUserByEmail(email);
        map = new HashMap<>();
        if(user == null)
        {
            map.put("msg", "The user doesn't exists");
            map.put("code","401");
        }
        else if(Objects.equals(user.password, password))
        {
            map.put("Token", Token.token(user.id));
            map.put("code","200");
        }
        else
        {
            map.put("msg", "Wrong password");
            map.put("code","401");
        }
        return JSONObject.toJSONString(map);
    }

    @RequestMapping ( "/register" )
    @ResponseBody
    public String register(HttpServletRequest request) {
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        User user = new User(email, password);
        map = new HashMap<>();
        if(userService.getUserByEmail(email) == null && email.matches(emailRegex))
        {
            userService.addUser(user);
            map.put("Token", Token.token(userService.getUserByEmail(email).id));
            map.put("code","200");
        }
        else if(userService.getUserByEmail(email) != null)
        {
            map.put("msg", "User exists");
            map.put("code","401");
        }
        else
        {
            map.put("msg", "Invalid Email Address");
            map.put("code","401");
        }
        return JSONObject.toJSONString(map);
    }

    @RequestMapping ( "/subject/upload" )
    @ResponseBody
    public String uploadSubject(HttpServletRequest request) {
        map = new HashMap<>();
        int subjectMap = 0;
        User user = getUserByToken(request.getHeader("Token"));
        List<String> userSubjectList = JSON.parseArray(request.getParameter("subject"), String.class);
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
        user.subject = subjectMap;
        userService.updateUserById(user);
        map.put("msg", "Success");
        map.put("code","200");
        return JSONObject.toJSONString(map);
    }

    @RequestMapping ( "/subject/download" )
    @ResponseBody
    public String downloadSubject(HttpServletRequest request)
    {
        map = new HashMap<>();
        User user = getUserByToken(request.getHeader("Token"));
        List<String> userSubjectList = new ArrayList<>();
        for(int i = 0; i < subjectList.length; i++)
        {
            if(((user.subject >> i) & 1) == 1)
            {
                userSubjectList.add(subjectList[i]);
            }
        }
        map.put("subject", userSubjectList);
        map.put("code","200");
        return JSONObject.toJSONString(map);
    }

}
