package com.tsinghua.kgeducator.tool;


import com.alibaba.fastjson.JSONObject;
import com.tsinghua.kgeducator.entity.User;
import com.tsinghua.kgeducator.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Component
public class LoginInterceptor implements HandlerInterceptor {
    private UserService userService;
    private Map<String,Object> map;
    private ArrayList<String> accessList;
    public LoginInterceptor(UserService userService)
    {
        this.userService = userService;
        accessList = new ArrayList<>();
        accessList.add("/login");
        accessList.add("/register");
        accessList.add("/sendcode");
        accessList.add("/changepwd");
    }

    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception
    {
        for(String uri : accessList)
        {
            if (httpServletRequest.getRequestURI().contains(uri)) {
                return true;
            }
        }
        httpServletResponse.setCharacterEncoding("UTF-8");
        String token = httpServletRequest.getHeader("Token");
        map = new HashMap<>();
        if(null == token)
        {
            map.put("msg","Token is null");
        }
        else
        {
            boolean result= Token.verify(token);
            if (result && userService.getUserById(Token.getUserId(token)) != null)
            {
                return true;
            }
            map.put("msg","Token is invalid");
        }
        httpServletResponse.getWriter().write(JSONObject.toJSONString(map));
        return false;
    }

    //    试图渲染之后执行
    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception
    {

    }

    //    在请求处理之后,视图渲染之前执行
    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception
    {

    }
}
