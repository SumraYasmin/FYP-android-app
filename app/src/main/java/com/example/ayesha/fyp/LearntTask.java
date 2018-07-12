package com.example.ayesha.fyp;

import java.util.ArrayList;

/**
 * Created by Ayesha on 12/29/2017.
 */

public class LearntTask {

    public ArrayList<String> learntProcedures;
    //int views;

    public LearntTask(ArrayList<String> learntProcedures) {
        this.learntProcedures = learntProcedures;
    }

    public LearntTask(){}

    public ArrayList<String> getLearntProcedures() {
        return learntProcedures;
    }

    public void setLearntProcedures(ArrayList<String> learntProcedures) {
        this.learntProcedures = learntProcedures;
    }


}

