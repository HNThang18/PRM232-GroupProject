package com.example.group_2.entities;

import java.io.Serializable;

public class User implements Serializable {
    private String username;
    private String password;
    private int money;

    public User(String username, String password, int money) {
        this.username = username;
        this.password = password;
        this.money = money;
    }

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
    }
}
