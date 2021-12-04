package com.example.myappchat.model;

import java.io.Serializable;

public class Message {
    private String _id;
    private String conversationId;
    private String sender;
    private String text;
    private String createdAt;
    private String updatedAt;
    private int __v;

    public Message(String conversationId, String sender, String text) {
        this.conversationId = conversationId;
        this.sender = sender;
        this.text = text;
    }

    public Message(String _id, String conversationId, String sender, String text, String createdAt, String updatedAt, int __v) {
        this._id = _id;
        this.conversationId = conversationId;
        this.sender = sender;
        this.text = text;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.__v = __v;
    }

    public Message() {
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getConversationId() {
        return conversationId;
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
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

    public int get__v() {
        return __v;
    }

    public void set__v(int __v) {
        this.__v = __v;
    }
}
