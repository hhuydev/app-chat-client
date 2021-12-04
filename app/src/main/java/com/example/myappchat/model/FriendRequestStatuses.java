package com.example.myappchat.model;

public class FriendRequestStatuses {
    private String _id;
    private User senderId;
    private User receiverId;
    private String status;

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public User getSenderId() {
        return senderId;
    }

    public void setSenderId(User senderId) {
        this.senderId = senderId;
    }

    public User getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(User receiverId) {
        this.receiverId = receiverId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public FriendRequestStatuses(String _id, User senderId, User receiverId, String status) {
        this._id = _id;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.status = status;
    }

    public FriendRequestStatuses() {
    }
}
