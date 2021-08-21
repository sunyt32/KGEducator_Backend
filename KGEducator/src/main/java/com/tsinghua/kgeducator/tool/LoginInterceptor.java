package com.tsinghua.kgeducator.tool;


import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

@Component
public class LoginInterceptor implements HandlerInterceptor {
    //    在请求处理之前调用,只有返回true才会执行要执行的请求
    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception
    {
        if (httpServletRequest.getRequestURI().contains("/login") || httpServletRequest.getRequestURI().contains("/register")) {
           return true;
        }
        httpServletResponse.setCharacterEncoding("UTF-8");
        String token=httpServletRequest.getHeader("Token");
        if(null == token)
        {
            Map<String,Object> map = new HashMap<>();
            map.put("data","Token is null");
            map.put("code","401");
            httpServletResponse.getWriter().write(JSONObject.toJSONString(map));
        }
        else
        {
            boolean result= Token.verify(token);
            if (result)
            {
                return true;
            }
            Map<String,Object> map=new HashMap<>();
            map.put("data","Token is invalid");
            map.put("code","401");
            httpServletResponse.getWriter().write(JSONObject.toJSONString(map));

        }
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