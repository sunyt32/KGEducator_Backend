package com.tsinghua.kgeducator.entity;

import java.io.Serializable;

public class User implements Serializable
{
    public Integer id;//编号
    public String email;//邮箱
    public String password;//密码
    public String name;//用户名
    public Integer grade;//年级
    public String subject; //对应的学科
    public String collection;//收藏夹
    public String history;//历史记录
    public String search;

    public User(String email, String password)
    {
        this.email = email;
        this.password = password;
        this.subject = "[\"chinese\", \"math\", \"english\", \"physics\", \"history\", \"chemistry\", \"biology\", \"geo\", \"politics\"]";
        this.collection = "[]";
        this.history = "[]";
        this.search = "[]";
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", name='" + name + '\'' +
                ", grade=" + grade +
                ", subject=" + subject +
                ", collection='" + collection + '\'' +
                ", history='" + history + '\'' +
                ", search='" + search + '\'' +
                '}';
    }
}
