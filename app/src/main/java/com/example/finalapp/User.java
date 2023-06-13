package com.example.finalapp;

import android.widget.EditText;
import android.widget.ImageView;

import java.util.ArrayList;

public class User {

    private String userName;
    private String email;
    private String password;
    private ArrayList<ProjectClass> userProjects;

    private String banner;

    public User() {
    }
    public User(String userName, String email, String password) {
        this.userName = userName;
        this.email = email;
        this.password = password;
    }

    public User(String userName, String email, String password, ArrayList<ProjectClass> userProjects, String banner) {
        this.userName = userName;
        this.email = email;
        this.password = password;
        this.userProjects = userProjects;
        this.banner = banner;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public ArrayList<ProjectClass> getUserProjects() {
        return userProjects;
    }

    public void setUserProjects(ArrayList<ProjectClass> userProjects) {
        this.userProjects = userProjects;
    }

    public String getBanner() {
        return banner;
    }

    public void setBanner(String banner) {
        this.banner = banner;
    }
}
