package com.example.ayesha.fyp;

/**
 * Created by Ayesha on 12/28/2017.
 */

public class Task {

    public String id,name,username,password,email;
    //Assigned date , completion date views for learning score waghera for practice
    public byte[] image;

    public Task(String id,String username, String name, String password, String email) {
        this.id=id;
        this.username = username;
        this.name = name;
        this.password = password;
        this.email = email;
    }

    public Task(){}

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }
}
