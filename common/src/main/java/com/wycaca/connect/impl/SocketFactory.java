package com.wycaca.connect.impl;

import com.wycaca.connect.ConnectFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class SocketFactory implements ConnectFactory {
    private final static Logger logger = LoggerFactory.getLogger(SocketFactory.class);
    private final Socket socket;

    public SocketFactory(Socket socket) {
        this.socket = socket;
    }

    @Override
    public InputStream getInput() {
        try {
            return socket.getInputStream();
        } catch (IOException e) {
            logger.error("获取Socket输入流失败");
            return null;
        }
    }

    @Override
    public OutputStream getOutPut() {
        try {
            return socket.getOutputStream();
        } catch (IOException e) {
            logger.error("获取Socket输出流失败");
            return null;
        }
    }

    @Override
    public void endOutput() {
        try {
            socket.shutdownOutput();
        } catch (IOException e) {
            logger.error("停止Socket输入流失败");
        }
    }
}
