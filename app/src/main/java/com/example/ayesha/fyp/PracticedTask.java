    package com.example.ayesha.fyp;

    /**
 * Created by Ayesha on 4/22/2018.
 */

public class PracticedTask {

        public String procedureName;
        public String score;


        public PracticedTask(String procedureName, String score) {
            this.procedureName = procedureName;
            this.score = score;
        }

        public PracticedTask(){}

        public String getProcedureName() {
            return procedureName;
        }

        public void setProcedureName(String procedureName) {
            this.procedureName = procedureName;
        }

        public String getScore() {
            return score;
        }

        public void setScore(String score) {
            this.score = score;
        }
}
