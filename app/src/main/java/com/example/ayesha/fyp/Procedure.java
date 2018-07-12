package com.example.ayesha.fyp;

/**
 * Created by Ayesha on 3/21/2018.
 */

public class Procedure {

    public String id;
    public String name;
    public int level;
    //Animation apk file in Blob

    public Procedure() {}

    public Procedure(String id, String name, int level ) {
        this.id = id;
        this.name = name;
        this.level = level;
    }

    public Procedure(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}
