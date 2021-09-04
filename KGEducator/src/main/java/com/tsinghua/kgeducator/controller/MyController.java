package com.tsinghua.kgeducator.controller;

import com.alibaba.fastjson.*;
import com.tsinghua.kgeducator.entity.Exam;
import com.tsinghua.kgeducator.entity.User;
import com.tsinghua.kgeducator.service.ExamService;
import com.tsinghua.kgeducator.service.UserService;
import com.tsinghua.kgeducator.tool.Token;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

import static java.lang.Math.abs;

@Controller
public class MyController
{
    JavaMailSender javaMailSender;
    private UserService userService;
    private ExamService examService;
    static final String emailRegex = "^[0-9a-zA-Z]+\\w*@([0-9a-z]+\\.)*+[0-9a-z]+$";
    private Map<String, Object> map;
    private HashMap<String, String> codeMap = new HashMap<>();
    public MyController(UserService userService, ExamService examService, JavaMailSender javaMailSender)
    {
        this.userService = userService;
        this.examService = examService;
        this.javaMailSender = javaMailSender;
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
        map = new HashMap<>();
        if(userService.getUserByEmail(email) == null && email.matches(emailRegex))
        {
            userService.addUser(user);
            map.put("Token", Token.token(userService.getUserByEmail(email).id));
        }
        else if(userService.getUserByEmail(email) != null)
        {
            map.put("msg", "User exists");
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        }
        else
        {
            map.put("msg", "Invalid Email Address");
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        }
        return JSONObject.toJSONString(map);
    }

    @RequestMapping (value = "/sendcode", method = RequestMethod.POST)
    @ResponseBody
    public String sendCode(HttpServletRequest request, HttpServletResponse response)
    {
        String email = request.getParameter("email");
        if(userService.getUserByEmail(email) == null)
        {
            map.put("msg", "User exists");
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        }
        Random r = new Random(System.currentTimeMillis());
        String code = String.valueOf(abs(r.nextInt()) % (999999 - 100000) + 100000);
        codeMap.put(email, code);
        map = new HashMap<>();
        SimpleMailMessage message =	new SimpleMailMessage();
        message.setFrom("1847767524@qq.com");
        message.setSubject("验证码");
        message.setTo(email);
        message.setText("您的验证码为：\n" + code);
        javaMailSender.send(message);
        map.put("msg", "Success");
        return JSONObject.toJSONString(map);
    }

    @RequestMapping (value = "/changepwd", method = RequestMethod.POST)
    @ResponseBody
    public String changePassword(HttpServletRequest request, HttpServletResponse response)
    {
        map = new HashMap<>();
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String code = request.getParameter("code");
        if(codeMap.get(email).equals(code))
        {
            User user = userService.getUserByEmail(email);
            user.password = password;
            userService.updateUserById(user);
            map.put("msg", "Success");
        }
        else
        {
            map.put("msg", "Invalid Code");
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
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
        user.subject = request.getParameter("subject");
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
        String userSubjectList = user.subject;
        map.put("data", userSubjectList);
        return JSONObject.toJSONString(map);
    }

    @RequestMapping (value = "/collection", method = RequestMethod.POST)
    @ResponseBody
    public String updateCollection(HttpServletRequest request)
    {
        map = new HashMap<>();
        User user = getUserByToken(request.getHeader("Token"));
        HashSet<List<String>> userCollection = JSON.parseObject(user.collection, new TypeReference<HashSet<List<String>>>(){});
        List<List<String>> delCollection = JSON.parseObject(request.getParameter("delete"), new TypeReference<List<List<String>>>(){});
        List<List<String>> addCollection = JSON.parseObject(request.getParameter("add"), new TypeReference<List<List<String>>>(){});
        if(delCollection != null)
        {
            delCollection.forEach(userCollection::remove);
        }
        if(addCollection != null)
        {
            userCollection.addAll(addCollection);
        }
        user.collection = JSON.toJSONString(userCollection);
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
        List<List<String>> userHistory = JSON.parseObject(user.history, new TypeReference<List<List<String>>>(){});
        List<List<String>> addHistory = JSON.parseObject(request.getParameter("history"), new TypeReference<List<List<String>>>(){});
        userHistory.removeAll(addHistory);
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

    @RequestMapping (value = "/search/upload", method = RequestMethod.POST)
    @ResponseBody
    public String uploadSearch(HttpServletRequest request)
    {
        map = new HashMap<>();
        User user = getUserByToken(request.getHeader("Token"));
        List<List<String>> userSearch = JSON.parseObject(user.search, new TypeReference<List<List<String>>>(){});
        List<List<String>> addSearch = JSON.parseObject(request.getParameter("search"), new TypeReference<List<List<String>>>(){});
        userSearch.removeAll(addSearch);
        userSearch.addAll(addSearch);
        user.search = JSON.toJSONString(userSearch);
        userService.updateUserById(user);
        map.put("msg", "Success");
        return JSONObject.toJSONString(map);
    }

    @RequestMapping (value = "/search/download", method = RequestMethod.GET)
    @ResponseBody
    public String downloadSearch(HttpServletRequest request)
    {
        map = new HashMap<>();
        User user = getUserByToken(request.getHeader("Token"));
        map.put("data", user.search);
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
