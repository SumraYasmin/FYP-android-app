package com.example.ayesha.fyp;

/**
 * Created by Ayesha on 12/29/2017.
 */

public class AssignedTask {

    public String status; //New Task:    Assigned again!
    public String statement;

    public AssignedTask(String status, String statement) {
        this.status = status;
        this.statement = statement;
    }

    public AssignedTask(){}

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatement() {
        return statement;
    }

    public void setStatement(String statement) {
        this.statement = statement;
    }


}

