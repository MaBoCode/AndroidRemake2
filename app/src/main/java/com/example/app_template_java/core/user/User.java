package com.example.app_template_java.core.user;

import java.io.Serializable;

public class User implements Serializable {

    public Long id;
    public String name;
    public String username;
    public String email;
    public String phone;

    public User(Long id, String name, String username, String email, String phone) {
        this.id = id;
        this.name = name;
        this.username = username;
        this.email = email;
        this.phone = phone;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                '}';
    }
}