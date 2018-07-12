package com.example.ayesha.fyp;

import java.util.ArrayList;

/**
 * Created by Ayesha on 10/30/2017.
 */

public class Trainee {

    public String id,name,username,password,email;
    public ArrayList<AssignedTask> assignedTasksList;
    public ArrayList<String> learntTasksList;
    public ArrayList<PracticedTask> practiceTasksList;

    public ArrayList<RecentTask> recentTasksList;

    public Trainee(String id, String username, String name, String password, String email, ArrayList<AssignedTask> assignedTasksList
    ,ArrayList<String> learntTasksList, ArrayList<PracticedTask> practiceTasksList, ArrayList<RecentTask> recentTasksList) {
        this.id = id;
        this.username = username;
        this.name = name;
        this.password = password;
        this.email = email;

        this.assignedTasksList = assignedTasksList;
        this.learntTasksList = learntTasksList;
        this.practiceTasksList = practiceTasksList;
        this.recentTasksList = recentTasksList;

        //AssignedTasksList = new ArrayList<AssignedTask>();
        //LearntTasksList = new ArrayList<String>();
        //PracticeTasksList = new ArrayList<PracticedTask>();
    }

    public Trainee(){}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public ArrayList<PracticedTask> getPracticeTasksList() {
        return practiceTasksList;
    }

    public void setPracticeTasksList(ArrayList<PracticedTask> practiceTasksList) {
        this.practiceTasksList = practiceTasksList;
    }

    public ArrayList<String> getLearntTasksList() {
        return learntTasksList;
    }

    public void setLearntTasksList(ArrayList<String> learntTasksList) {
        this.learntTasksList = learntTasksList;
    }

    public ArrayList<AssignedTask> getAssignedTasksList() {
        return assignedTasksList;
    }

    public void setAssignedTasksList(ArrayList<AssignedTask> assignedTasksList) {
        this.assignedTasksList = assignedTasksList;
    }

    public ArrayList<RecentTask> getRecentTasksList() {
        return recentTasksList;
    }

    public void setRecentTasksList(ArrayList<RecentTask> recentTasksList) {
        this.recentTasksList = recentTasksList;
    }
}
