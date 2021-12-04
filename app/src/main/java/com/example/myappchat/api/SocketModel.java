package com.example.myappchat.api;

import io.socket.client.Socket;

public class SocketModel {
    public static Socket socket;

    public SocketModel() {
    }

    public static Socket getSocket() {
        return socket;
    }

    public static void setSocket(Socket socket) {
        SocketModel.socket = socket;
    }
}
