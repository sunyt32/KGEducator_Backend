package com.tsinghua.kgeducator.entity;

import java.io.Serializable;

public class Exam implements Serializable
{
    public Integer pid;
    public String name;
    public String body;
    public String realAns;
    public String userAns;
    public Integer userId;

    public Exam(Integer pid, String name, String body, String realAns, String userAns, Integer userId)
    {
        this.pid = pid;
        this.name = name;
        this.body = body;
        this.realAns = realAns;
        this.userAns = userAns;
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "Exam{" +
                "pid=" + pid +
                ", name='" + name + '\'' +
                ", body='" + body + '\'' +
                ", realAns='" + realAns + '\'' +
                ", userAns='" + userAns + '\'' +
                ", userId=" + userId +
                '}';
    }
}
