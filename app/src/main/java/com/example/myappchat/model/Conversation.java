package com.example.myappchat.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class Conversation implements Serializable{
    @SerializedName("_id")
    @Expose
    private String _id;

    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("isGroup")
    @Expose
    private boolean isGroup;

    @SerializedName("owner")
    @Expose
    private String owner;
    @SerializedName("members")
    @Expose
    private ArrayList<User> users;
    @SerializedName("createdAt")
    @Expose
    private String createdAt;
    @SerializedName("updatedAt")
    @Expose
    private String updatedAt;
    @SerializedName("__v")
    @Expose
    private String __v;

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isGroup() {
        return isGroup;
    }

    public void setGroup(boolean group) {
        isGroup = group;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public ArrayList<User> getUsers() {
        return users;
    }

    public void setUsers(ArrayList<User> users) {
        this.users = users;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String get__v() {
        return __v;
    }

    public void set__v(String __v) {
        this.__v = __v;
    }

    public Conversation() {
    }

    public Conversation(String _id, String name, boolean isGroup, String owner, ArrayList<User> users, String createdAt, String updatedAt, String __v) {
        this._id = _id;
        this.name = name;
        this.isGroup = isGroup;
        this.owner = owner;
        this.users = users;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.__v = __v;
    }

    @Override
    public String toString() {
        return "Conversation{" +
                "_id='" + _id + '\'' +
                ", name='" + name + '\'' +
                ", isGroup=" + isGroup +
                ", owner='" + owner + '\'' +
                ", users=" + users +
                ", createdAt='" + createdAt + '\'' +
                ", updatedAt='" + updatedAt + '\'' +
                ", __v='" + __v + '\'' +
                '}';
    }
}
