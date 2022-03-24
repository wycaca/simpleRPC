package com.wycaca.service.impl;

import com.wycaca.service.ConnectFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class SocketImpl implements ConnectFactory {
    private final Socket socket;

    public SocketImpl(Socket socket) throws IOException {
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
