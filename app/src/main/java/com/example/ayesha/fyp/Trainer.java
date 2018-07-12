package com.example.ayesha.fyp;

/**
 * Created by Ayesha on 10/30/2017.
 */

public class Trainer {

    public String id,password,email;

    public Trainer(String id, String password, String email) {
        this.id=id;
        this.password = password;
        this.email = email;
    }

    public Trainer(){}

    public String getId() {
        return id;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }
}
