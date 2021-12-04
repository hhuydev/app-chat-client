package com.example.myappchat.model;

import java.io.Serializable;

public class MessageModel implements Serializable {
    private String conversationId;
    private String sender;
    private String text;
    private String _id;
    private String createdAt;
    private String updatedAt;

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

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
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

    public MessageModel() {
    }

    public MessageModel(String conversationId, String sender, String text, String _id, String createdAt, String updatedAt) {
        this.conversationId = conversationId;
        this.sender = sender;
        this.text = text;
        this._id = _id;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
