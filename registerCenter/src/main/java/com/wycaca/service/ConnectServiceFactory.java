package com.wycaca.service;

import com.wycaca.service.impl.SocketServiceImpl;

import java.net.Socket;

public class ConnectServiceFactory {
    private static ConnectService connectService;

    public static ConnectService get(Object data) {
        if (data instanceof Socket) {
            Socket socket = (Socket) data;
            return new SocketServiceImpl(socket);
        }
        return connectService;
    }
}
