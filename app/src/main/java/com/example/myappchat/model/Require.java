package com.example.myappchat.model;

public class Require {
    private String name;
    private int resImage;

    public Require(String name, int resImage) {
        this.name = name;
        this.resImage = resImage;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getResImage() {
        return resImage;
    }

    public void setResImage(int resImage) {
        this.resImage = resImage;
    }
}
