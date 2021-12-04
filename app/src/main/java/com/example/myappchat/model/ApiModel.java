package com.example.myappchat.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class ApiModel {

    @SerializedName("conversations")
    @Expose
    private ArrayList<Conversation> conversations;
    @SerializedName("user")
    @Expose
    private User user;
    @SerializedName("newMessage")
    @Expose
    private MessageModel message;
    @SerializedName("messages")
    @Expose
    private ArrayList<Message> messageArrayList;
    @SerializedName("latestMessage")
    @Expose
    private Message lastMessage;
    @SerializedName("users")
    @Expose
    private ArrayList<User> users;
    @SerializedName("listFriends")
    @Expose
    private ArrayList<User> friends;
    @SerializedName("message")
    @Expose
    private String msg;

    @SerializedName("newConversation")
    @Expose
    private Conversation newConversation;

    @SerializedName("conversation")
    @Expose
    private Conversation conversation;

    @SerializedName("listUser")
    @Expose
    private List<User> admUserList;

    @SerializedName("otpAuth2")
    @Expose
    private OtpAuth otpAuth;

    public OtpAuth getOtpAuth() {
        return otpAuth;
    }

    public void setOtpAuth(OtpAuth otpAuth) {
        this.otpAuth = otpAuth;
    }

    public List<User> getAdmUserList() {
        return admUserList;
    }

    public void setAdmUserList(List<User> admUserList) {
        this.admUserList = admUserList;
    }

    public Conversation getConversation() {
        return conversation;
    }

    public void setConversation(Conversation conversation) {
        this.conversation = conversation;
    }

    public Conversation getNewConversation() {
        return newConversation;
    }

    public void setNewConversation(Conversation newConversation) {
        this.newConversation = newConversation;
    }

    public ArrayList<Conversation> getConversations() {
        return conversations;
    }

    public void setConversations(ArrayList<Conversation> conversations) {
        this.conversations = conversations;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public MessageModel getMessage() {
        return message;
    }

    public void setMessage(MessageModel message) {
        this.message = message;
    }

    public ArrayList<Message> getMessageArrayList() {
        return messageArrayList;
    }

    public void setMessageArrayList(ArrayList<Message> messageArrayList) {
        this.messageArrayList = messageArrayList;
    }

    public Message getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(Message lastMessage) {
        this.lastMessage = lastMessage;
    }

    public ArrayList<User> getUsers() {
        return users;
    }

    public void setUsers(ArrayList<User> users) {
        this.users = users;
    }

    public ArrayList<User> getFriends() {
        return friends;
    }

    public void setFriends(ArrayList<User> friends) {
        this.friends = friends;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
