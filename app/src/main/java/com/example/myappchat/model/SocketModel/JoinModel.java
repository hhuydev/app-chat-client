package com.example.myappchat.model.SocketModel;

import com.example.myappchat.model.Conversation;

import java.io.Serializable;

public class JoinModel implements Serializable {
    private String username;
    private String room;

    public JoinModel() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public JoinModel(String username, String room) {
        this.username = username;
        this.room = room;
    }
}
