package com.tsinghua.kgeducator.controller;

import com.alibaba.fastjson.*;
import com.tsinghua.kgeducator.entity.Exam;
import com.tsinghua.kgeducator.entity.User;
import com.tsinghua.kgeducator.service.ExamService;
import com.tsinghua.kgeducator.service.UserService;
import com.tsinghua.kgeducator.tool.Token;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.*;

@Controller
public class MyController
{
    private UserService userService;
    private ExamService examService;
    static final String emailRegex = "^[0-9a-zA-Z]+\\w*@([0-9a-z]+\\.)*+[0-9a-z]+$";
    Map<String,Object> map;
    public MyController(UserService userService, ExamService examService)
    {
        this.userService = userService;
        this.examService = examService;
    }
    private User getUserByToken(String token)
    {
        Integer id = Token.getUserId(token);
        User user = userService.getUserById(id);
        return user;
    }
    @RequestMapping (value = "/login", method = RequestMethod.POST)
    @ResponseBody
    public String login(HttpServletRequest request, HttpServletResponse response)
    {
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        User user = userService.getUserByEmail(email);
        map = new HashMap<>();
        if(user == null)
        {
            map.put("msg", "The user doesn't exists");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
        else if(Objects.equals(user.password, password))
        {
            map.put("Token", Token.token(user.id));
        }
        else
        {
            map.put("msg", "Wrong password");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
        return JSONObject.toJSONString(map);
    }

    @RequestMapping (value = "/register", method = RequestMethod.POST)
    @ResponseBody
    public String register(HttpServletRequest request, HttpServletResponse response)
    {
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        User user = new User(email, password);
        map = new HashMap<>();if(userService.getUserByEmail(email) == null && email.matches(emailRegex))
        {
            userService.addUser(user);
            map.put("Token", Token.token(userService.getUserByEmail(email).id));
        }
        else if(userService.getUserByEmail(email) != null)
        {
            map.put("msg", "User exists");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
        else
        {
            map.put("msg", "Invalid Email Address");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
        return JSONObject.toJSONString(map);
    }

    @RequestMapping (value = "/info/upload", method = RequestMethod.POST)
    @ResponseBody
    public String uploadInfo(HttpServletRequest request)
    {
        map = new HashMap<>();
        User user = getUserByToken(request.getHeader("Token"));
        user.name = request.getParameter("name");
        user.grade = Integer.valueOf(request.getParameter("grade"));
        userService.updateUserById(user);
        map.put("msg", "Success");
        return JSONObject.toJSONString(map);
    }

    @RequestMapping (value = "/info/download", method = RequestMethod.GET)
    @ResponseBody
    public String downloadInfo(HttpServletRequest request)
    {
        map = new HashMap<>();
        HashMap<String, Object> info = new HashMap<>();
        User user = getUserByToken(request.getHeader("Token"));
        info.put("name", user.name);
        info.put("grade", user.grade);
        map.put("data", info);
        return JSONObject.toJSONString(map);
    }

    @RequestMapping (value = "/subject/upload", method = RequestMethod.POST)
    @ResponseBody
    public String uploadSubject(HttpServletRequest request)
    {
        map = new HashMap<>();
        User user = getUserByToken(request.getHeader("Token"));
        user.subject = userService.assembleSubject(request.getParameter("subject"));
        userService.updateUserById(user);
        map.put("msg", "Success");
        return JSONObject.toJSONString(map);
    }

    @RequestMapping (value = "/subject/download", method = RequestMethod.GET)
    @ResponseBody
    public String downloadSubject(HttpServletRequest request)
    {
        map = new HashMap<>();
        User user = getUserByToken(request.getHeader("Token"));
        List<String> userSubjectList = userService.disassembleSubject(user.subject);
        map.put("data", userSubjectList);
        return JSONObject.toJSONString(map);
    }

    @RequestMapping (value = "/collection", method = RequestMethod.POST)
    @ResponseBody
    public String updateCollection(HttpServletRequest request)
    {
        map = new HashMap<>();
        User user = getUserByToken(request.getHeader("Token"));
        HashSet<String> userCollection = JSON.parseObject(user.collection, new TypeReference<HashSet<String>>(){});
        List<String> delCollection = JSON.parseObject(request.getParameter("delete"), new TypeReference<List<String>>(){});
        List<String> addCollection = JSON.parseObject(request.getParameter("add"), new TypeReference<List<String>>(){});
        if(delCollection != null)
        {
            delCollection.forEach(userCollection::remove);
        }
        if(addCollection != null)
        {
            userCollection.addAll(addCollection);
        }
        user.collection = userCollection.toString();
        userService.updateUserById(user);
        map.put("data", user.collection);
        return JSONObject.toJSONString(map);
    }

    @RequestMapping (value = "/history/upload", method = RequestMethod.POST)
    @ResponseBody
    public String uploadHistory(HttpServletRequest request)
    {
        map = new HashMap<>();
        User user = getUserByToken(request.getHeader("Token"));
        List<String> userHistory = JSON.parseArray(user.history,String.class);
        List<String> addHistory = JSON.parseArray(request.getParameter("history"), String.class);
        userHistory.addAll(addHistory);
        user.history = JSON.toJSONString(userHistory);
        userService.updateUserById(user);
        map.put("msg", "Success");
        return JSONObject.toJSONString(map);
    }

    @RequestMapping (value = "/history/download", method = RequestMethod.GET)
    @ResponseBody
    public String downloadHistory(HttpServletRequest request)
    {
        map = new HashMap<>();
        User user = getUserByToken(request.getHeader("Token"));
        map.put("data", user.history);
        return JSONObject.toJSONString(map);
    }

    @RequestMapping (value = "/exam/upload", method = RequestMethod.POST)
    @ResponseBody
    public String uploadExam(HttpServletRequest request)
    {
        map = new HashMap<>();
        User user = getUserByToken(request.getHeader("Token"));
        List<HashMap<String, String>> userHistory = JSON.parseObject(request.getParameter("data"),new TypeReference<List<HashMap<String, String>>>(){});
        for(HashMap<String, String> history : userHistory)
        {
            Exam newExam = new Exam(Integer.parseInt(history.get("id")), history.get("name"), history.get("body"), history.get("realAns"), history.get("userAns"), user.id);
            examService.addExam(newExam);
        }
        map.put("msg", "Success");
        return JSONObject.toJSONString(map);
    }

    @RequestMapping (value = "/exam/download", method = RequestMethod.GET)
    @ResponseBody
    public String downloadExam(HttpServletRequest request)
    {
        map = new HashMap<>();
        User user = getUserByToken(request.getHeader("Token"));
        List<String> finalExams = examService.recommendExams(user.id);
        map.put("data", finalExams.toString());
        return JSONObject.toJSONString(map);
    }

}
