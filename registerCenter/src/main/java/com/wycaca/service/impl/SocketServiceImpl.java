package com.wycaca.service.impl;

import com.wycaca.service.ConnectService;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class SocketServiceImpl extends ConnectService {
    private final Socket socket;

    public SocketServiceImpl(Socket socket) {
        super();
        this.socket = socket;
    }

    @Override
    public InputStream getInput() throws IOException {
        return socket.getInputStream();
    }

    @Override
    public OutputStream getOutPut() throws IOException {
        return socket.getOutputStream();
    }
}
