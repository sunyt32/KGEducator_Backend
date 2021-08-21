package com.tsinghua.kgeducator.entity;

import java.io.Serializable;

public class User implements Serializable
{
    public Integer id;//编号
    public String email;//邮箱
    public String password;//密码
    public String name;//用户名
    public Integer grade;//年级

    public User(String email, String password)
    {
        this.email = email;
        this.password = password;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                ", grade=" + grade +
                '}';
    }
}
