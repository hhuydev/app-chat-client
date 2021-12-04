package com.example.myappchat.Env;

import com.example.myappchat.model.User;

public class env {
    private static String token;
    private static User user;
    private static String urlServer = "https://app-chat-api-cnm.herokuapp.com/";
    private static String urlAvatar = "";

    public static void setUrlServer(String urlServer) {
        env.urlServer = urlServer;
    }

    public static String getUrlAvatar() {
        return urlAvatar;
    }

    public static void setUrlAvatar(String urlAvatar) {
        env.urlAvatar = urlAvatar;
    }

    public static String getUrlServer() {
        return urlServer;
    }

    public static void setToken(String token) {
        env.token = token;
    }


    public static String getToken() {
        return token;
    }

    public static User getUser() {
        return user;
    }

    public static void setUser(User user) {
        env.user = user;
    }


    public env() {
    }
}