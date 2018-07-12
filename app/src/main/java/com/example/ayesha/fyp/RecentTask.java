package com.example.ayesha.fyp;

public class RecentTask {

    public String statement; //New Task:    Assigned again!
    public String click;

    public RecentTask(String statement, String click) {
        this.statement = statement;
        this.click = click;
    }

    public RecentTask(){}

    public String getStatement() {
        return statement;
    }

    public void setStatement(String statement) {
        this.statement = statement;
    }

    public String getClick() {
        return click;
    }

    public void setClick(String click) {
        this.click = click;
    }



}
