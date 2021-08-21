package com.tsinghua.kgeducator.controller;

import com.alibaba.fastjson.JSONObject;
import com.tsinghua.kgeducator.entity.User;
import com.tsinghua.kgeducator.service.UserService;
import com.tsinghua.kgeducator.tool.Token;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Controller
public class MyController
{
    @Autowired
    private UserService userService;
    String emailRegex = "^[0-9a-zA-Z]+\\w*@([0-9a-z]+\\.)*+[0-9a-z]+$";
    Map<String,Object> map;
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
            map.put("token", Token.token(user.id));
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
            map.put("token", Token.token(userService.getUserByEmail(email).id));
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
}
